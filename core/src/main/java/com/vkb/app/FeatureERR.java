package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.alg.determine.FunctionFeatureDeterminer;
import com.vkb.alg.determine.ScalarFeatureDeterminer;
import com.vkb.alg.outlierfeature.ConfigurableOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.DefaultOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.outlierfeature.OutlierFeatureSignaturePattern;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FARFRRPlotter;
import com.vkb.app.util.PreComputedDataUserLoader;
import com.vkb.gui.Application;
import com.vkb.model.Feature;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.User;
import com.vkb.quality.farfrr.ERRCalculator;
import com.vkb.quality.farfrr.FARFRRCalculator;
import com.vkb.quality.farfrr.ui.FARFRRPrinter;

public class FeatureERR {
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user_a" ),
		new File( Environment.RESOURCES_DIR, "user_doh" ),
		new File( Environment.RESOURCES_DIR, "user_egg" ),
		new File( Environment.RESOURCES_DIR, "user_fj" ),
		new File( Environment.RESOURCES_DIR, "user_jig" ),
		new File( Environment.RESOURCES_DIR, "user_ma" ),
		new File( Environment.RESOURCES_DIR, "user_xf" ) };  
	
	/*private static final File INPUT_FOLDERS[] = { 
		new File( "src/main/resources2", "user_doh" ),
		new File( "src/main/resources2", "user_xf" ) };*/ 
	
	private static int NTHREADS = INPUT_FOLDERS.length;
	
	private List<User<OutlierFeatureAlgorithm>> users;
	private ExecutorService executor;
	
	public FeatureERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		
		ConfigurableOutlierFeatureAlgorithmTraits algorithmTraits =
				new ConfigurableOutlierFeatureAlgorithmTraits( DefaultOutlierFeatureAlgorithmTraits.getInstance() );
		
		algorithmTraits.setThreshold( 0.0d );
		
		long startTime = System.currentTimeMillis();
		
		PreComputedDataUserLoader preComputeFunctionDistances = new PreComputedDataUserLoader( executor );
		this.users = preComputeFunctionDistances.load( inputFolders, algorithmTraits );
		
		long loadTime = System.currentTimeMillis() - startTime;
		System.out.println( "Users loaded in " + loadTime/1000 + " seconds." );
	}
	
	
	public void run() throws Exception {
		Set<FeatureId> scalarFeatures = new TreeSet<FeatureId>();
		Set<FeatureId> functionFeatures = new TreeSet<FeatureId>();
		getFeaturesToCheck( scalarFeatures, functionFeatures );
		
		Application application = new Application();
		Map<FeatureId, ERRCalculator.Result> scalarResults = run( application, scalarFeatures,
															ScalarFeatureTraits.instance );
		Map<FeatureId, ERRCalculator.Result> functionResults = run( application, functionFeatures,
															FuntionFeatureTraits.instance );
		
		Map<FeatureId, ERRCalculator.Result> allResults = new TreeMap<FeatureId, ERRCalculator.Result>();
		allResults.putAll( scalarResults );
		allResults.putAll( functionResults );
		
		Map<FeatureId, Double> weights = calculateWeights( allResults );
		
		System.out.println( "***************************************************** " );
		for ( Map.Entry<FeatureId, ERRCalculator.Result> errResult : allResults.entrySet() ) {
			FeatureId feature = errResult.getKey();
			System.out.print( "* Feature " + feature.getName() + ": " );
			System.out.print( "Threshold = " + errResult.getValue().getThreshold() + ", " );
			System.out.print( "Err = " + errResult.getValue().getValue() + ", " );
			System.out.println( "Weight = " + weights.get(feature) + "." );
		}
		
		System.out.println( "***************************************************** " );
		System.out.println( "Weights:" );
		for ( Map.Entry<FeatureId, Double> entry : weights.entrySet() ) {
			System.out.println( "ret.put( " + entry.getKey().getCodeName() + ", " +
								entry.getValue() + " );" );
		}
		
		
		System.out.println( "***************************************************** " );
		System.out.println( "Thresholds:" );
		for ( Map.Entry<FeatureId, ERRCalculator.Result> entry : allResults.entrySet() ) {
			System.out.println( "ret.put( " + entry.getKey().getCodeName() + ", " +
								entry.getValue().getThreshold() + " );" );
		}
	}
	

	private Map<FeatureId, Double> calculateWeights( Map<FeatureId, ERRCalculator.Result> allResults ) {
		Map<FeatureId, Double> ret = new TreeMap<FeatureId, Double>();
		
		for ( Map.Entry<FeatureId, ERRCalculator.Result> errResult : allResults.entrySet() ) {
			FeatureId feature = errResult.getKey();
			
			double weight = 1 / errResult.getValue().getValue();
			ret.put( feature, weight );
		}
		
		return ret;
	}


	public Map<FeatureId, ERRCalculator.Result> run( Application application, Set<FeatureId> features,
											FeatureTraits traits ) throws Exception {
		List<String> titles = new ArrayList<String>();
		List<XYPlot> plots = new ArrayList<XYPlot>();
		
		Map<FeatureId, ERRCalculator.Result> ret = new TreeMap<FeatureId, ERRCalculator.Result>();
		for( FeatureId feature : features ) {
			titles.add( feature.getName() );
			ERRCalculator.Result err = run( plots, feature, traits );
			ret.put( feature, err );
		}
		
		application.start( "FAR/FRR Graphics", titles, plots );
		
		return ret;
	}
	
	
	public ERRCalculator.Result run( List<XYPlot> plots, FeatureId feature, FeatureTraits traits ) throws Exception {
		List<FARFRRCalculator.Result> result = calculateFarFrr( feature, traits );
		
		System.out.println( "***************************************************** " );
		System.out.println( feature.getName() + ":" );
		FARFRRPrinter printer = new FARFRRPrinter();
		printer.print( traits.getThresholdsToCheck(), result );

		FARFRRPlotter plotter = new FARFRRPlotter();
		XYPlot plot = plotter.plot( traits.getThresholdsToCheck(), result );
		plots.add( plot );
		
		return ERRCalculator.calculate( result, traits.getThresholdsToCheck() );
	}
	

	private List<FARFRRCalculator.Result> calculateFarFrr( FeatureId feature, FeatureTraits traits ) throws Exception {
		FARFRRCalculator errCalculator = new FARFRRCalculator( executor );
		
		List<User<ThresholdedSignatureValidator>> usersToCheck = generateUsers( feature, traits );
		
		return errCalculator.execute( usersToCheck, traits.getThresholdsToCheck() );
	}


	private List<User<ThresholdedSignatureValidator>> generateUsers( FeatureId feature,
						FeatureTraits traits ) throws Exception {
		List<User<ThresholdedSignatureValidator>> ret = new ArrayList<User<ThresholdedSignatureValidator>>();
		for( User<OutlierFeatureAlgorithm> user : users ) {
			ThresholdedSignatureValidator validator =
					traits.generateValidator( user.getValidator().getPattern(), feature );
			User<ThresholdedSignatureValidator> newUser = 
						new User<ThresholdedSignatureValidator>( user.getId(), validator, user.getOwnSignatures() );
			ret.add( newUser );
		}
		return ret;
	}
	
	private void getFeaturesToCheck( Set<FeatureId> scalarFeatureIds, Set<FeatureId> functionFeatureIds ) {
		Signature signature = users.get(0).getOwnSignatures().get(0);
		Set<Feature> scalarfeatures = signature.getFeatures().getAllByModel( ScalarFeatureData.class );
		Set<Feature> functionfeatures = signature.getFeatures().getAllByModel( FunctionFeatureData.class );
		for( Feature feature : scalarfeatures ) {
			if ( OutlierFeatureSignaturePattern.getFeatureWeights().containsKey( feature.getId() ) ) {
				scalarFeatureIds.add( feature.getId() );
			}
		}
		for( Feature feature : functionfeatures ) {
			if ( OutlierFeatureSignaturePattern.getFeatureWeights().containsKey( feature.getId() ) ) {
				functionFeatureIds.add( feature.getId() );
			}
		}
	}


	public static void main(String[] args) {
		try {
			FeatureERR app = new FeatureERR( INPUT_FOLDERS );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	private static interface FeatureTraits {
		public ThresholdedSignatureValidator generateValidator( OutlierFeatureSignaturePattern pattern, FeatureId feature );
		public double[] getThresholdsToCheck();
	};
	
	
	private static class ScalarFeatureTraits implements FeatureTraits {
		private static double ThresholdsToCheck[] = buildThresholdsToCheck();
		public static ScalarFeatureTraits instance = new ScalarFeatureTraits();
		
		@Override
		public ThresholdedSignatureValidator generateValidator( OutlierFeatureSignaturePattern pattern, FeatureId feature ) {
			ScalarFeatureDeterminer determiner = pattern.getFeatureValidator(feature);
			return new Validator( determiner, feature );
		}
		
		@Override
		public double[] getThresholdsToCheck() {
			return ThresholdsToCheck;
		}
		
		private static double[] buildThresholdsToCheck() {
			final int N = 25;
			double[] ret = new double[N];
			ret[0] = 0.70d;
			for ( int i=1; i<ret.length; ++i ) {
				ret[i] = ret[i-1] + 0.05d;
			}
			return ret;
		}
		
		private static class Validator implements ThresholdedSignatureValidator {
			private ScalarFeatureDeterminer featureValidator;
			private FeatureId featureId;
			
			public Validator( ScalarFeatureDeterminer featureValidator, FeatureId featureId ) {
				this.featureValidator = featureValidator;
				this.featureId = featureId;
			}

			@Override
			public boolean check(Signature signature) throws Exception {
				ScalarFeatureData featureData = signature.getFeatures().get(featureId).getData();
				return featureValidator.check(featureData);
			}

			@Override
			public void setThreshold(double threshold) {
				featureValidator.setThreshold(threshold);
			}
		}
	};
	
	
	private static class FuntionFeatureTraits implements FeatureTraits {
		private static double ThresholdsToCheck[] = buildThresholdsToCheck();
		public static FuntionFeatureTraits instance = new FuntionFeatureTraits();
		
		@Override
		public ThresholdedSignatureValidator generateValidator( OutlierFeatureSignaturePattern pattern, FeatureId feature ) {
			FunctionFeatureDeterminer determiner = pattern.getFeatureValidator(feature);
			return new Validator( determiner, feature );
		}
		
		@Override
		public double[] getThresholdsToCheck() {
			return ThresholdsToCheck;
		}
		
		private static double[] buildThresholdsToCheck() {
			final int N = 30;
			double[] ret = new double[N];
			ret[0] = 0.80d;
			for ( int i=1; i<ret.length; ++i ) {
				ret[i] = ret[i-1] + 0.05d;
			}
			return ret;
		}
		
		private static class Validator implements ThresholdedSignatureValidator {
			private FunctionFeatureDeterminer featureValidator;
			private FeatureId featureId;
			
			public Validator( FunctionFeatureDeterminer featureValidator, FeatureId featureId ) {
				this.featureValidator = featureValidator;
				this.featureId = featureId;
			}

			@Override
			public boolean check(Signature signature) throws Exception {
				FunctionFeatureData featureData = signature.getFeatures().get(featureId).getData();
				return featureValidator.check(featureData);
			}

			@Override
			public void setThreshold(double threshold) {
				featureValidator.setThreshold(threshold);
			}
		}
	};
}
