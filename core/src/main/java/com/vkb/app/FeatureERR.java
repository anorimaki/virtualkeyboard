package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.SignatureValidatorFactory;
import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.alg.determine.FunctionFeatureDeterminer;
import com.vkb.alg.determine.ScalarFeatureDeterminer;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithmFactory;
import com.vkb.alg.outlierfeature.OutlierFeatureSignaturePattern;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FARFRRPlotter;
import com.vkb.gui.Application;
import com.vkb.io.UserLoader;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.User;
import com.vkb.quality.farfrr.ERRCalculator;
import com.vkb.quality.farfrr.FARFRRCalculator;
import com.vkb.quality.farfrr.ui.FARFRRPrinter;

public class FeatureERR {
	private static int NTHREADS = 10;
	private static double PATTERN_THRESHOLD = 0.0d;
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
	
	private List<User<OutlierFeatureAlgorithm>> users;;
	private ExecutorService executor;
	
	public FeatureERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		
		SignatureValidatorFactory<OutlierFeatureAlgorithm> factory = 
							new OutlierFeatureAlgorithmFactory( PATTERN_THRESHOLD );

		users = UserLoader.load( executor, factory, inputFolders );
	}
	

	public void run() throws Exception {
		Set<FeatureId> scalarFeatures = FeatureId.getByModel(ScalarFeatureData.class);
		Set<FeatureId> functionFeatures = FeatureId.getByModel(FunctionFeatureData.class);

		Application application = new Application();
		Map<FeatureId, ERRCalculator.Result> scalarResults = run( application, scalarFeatures,
															ScalarFeatureTraits.instance );
		Map<FeatureId, ERRCalculator.Result> functionResults = run( application, functionFeatures,
															FuntionFeatureTraits.instance );
		
		Map<FeatureId, ERRCalculator.Result> allResults = new HashMap<FeatureId, ERRCalculator.Result>();
		allResults.putAll( scalarResults );
		allResults.putAll( functionResults );
		
		Map<FeatureId, Double> weights = calculateWeights( allResults );
		
		for ( Map.Entry<FeatureId, ERRCalculator.Result> errResult : allResults.entrySet() ) {
			FeatureId feature = errResult.getKey();
			System.out.print( "* Feature " + feature.getName() + ": " );
			System.out.print( "Threshold = " + errResult.getValue().getThreshold() + ", " );
			System.out.print( "Err = " + errResult.getValue().getValue() + ", " );
			System.out.println( "Weight =" + weights.get(feature) + "." );
		}
	}
	
	
	private Map<FeatureId, Double> calculateWeights( Map<FeatureId, ERRCalculator.Result> allResults ) {
		Map<FeatureId, Double> ret = new HashMap<FeatureId, Double>();
		
		for ( Map.Entry<FeatureId, ERRCalculator.Result> errResult : allResults.entrySet() ) {
			FeatureId feature = errResult.getKey();
			
			double weight = 1.0d / errResult.getValue().getValue();
			ret.put( feature, weight );
		}
		
		return ret;
	}


	public Map<FeatureId, ERRCalculator.Result> run( Application application, Set<FeatureId> features,
											FeatureTraits traits ) throws Exception {
		List<String> titles = new ArrayList<String>();
		List<XYPlot> plots = new ArrayList<XYPlot>();
		
		Map<FeatureId, ERRCalculator.Result> ret = new HashMap<FeatureId, ERRCalculator.Result>();
		for( FeatureId feature : features ) {
			titles.add( feature.getName() );
			ERRCalculator.Result err = run( plots, feature, traits );
			ret.put( feature, err );
		}
		
		application.start( "FAR/FRR Graphics", titles.toArray( new String[0] ), plots );
		
		return ret;
	}
	
	
	public ERRCalculator.Result run( List<XYPlot> plots, FeatureId feature, FeatureTraits traits ) throws Exception {
		List<FARFRRCalculator.Result> result = calculateFarFrr( feature, traits );
		
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
						new User<ThresholdedSignatureValidator>( validator, user.getOwnSignatures() );
			ret.add( newUser );
		}
		return ret;
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
			final int N = 50;
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
			ret[0] = 0.90d;
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
