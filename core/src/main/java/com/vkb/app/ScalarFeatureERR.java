package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import com.vkb.app.util.Environment;
import com.vkb.app.util.FARFRRPlotter;
import com.vkb.gui.Application;
import com.vkb.io.UserLoader;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.User;
import com.vkb.quality.farfrr.FARFRRCalculator;
import com.vkb.quality.farfrr.ui.FARFRRPrinter;

public class ScalarFeatureERR {
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
	private static double ThresholdsToCheck[] = buildThresholdsToCheck();
	
	public ScalarFeatureERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		
		SignatureValidatorFactory<OutlierFeatureAlgorithm> factory = 
							new OutlierFeatureAlgorithmFactory( PATTERN_THRESHOLD );

		users = UserLoader.load( executor, factory, inputFolders );
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


	public void run() throws Exception {
		Set<FeatureId> features = FeatureId.getByModel(ScalarFeatureData.class);
		List<String> titles = new ArrayList<String>();
		List<XYPlot> plots = new ArrayList<XYPlot>();
		for( FeatureId feature : features ) {
			XYPlot plot = run( feature );
			titles.add( feature.getName() );
			plots.add(plot);
		}
		Application application = new Application();
		application.run( "FAR/FRR Graphics", plots, titles.toArray( new String[0] ) );
	}
	
	
	public XYPlot run( FeatureId feauture ) throws Exception {
		FARFRRCalculator errCalculator = new FARFRRCalculator( executor );
		
		List<User<Validator>> usersToCheck = generateUsers( feauture );
		
		List<FARFRRCalculator.Result> result = errCalculator.execute( usersToCheck, ThresholdsToCheck );
		
		FARFRRPrinter printer = new FARFRRPrinter();
		printer.print( ThresholdsToCheck, result );

		FARFRRPlotter plotter = new FARFRRPlotter();
		return plotter.plot( ThresholdsToCheck, result );
	}
	

	private List<User<Validator>> generateUsers( FeatureId feature ) throws Exception {
		List<User<Validator>> ret = new ArrayList<User<Validator>>();
		for( User<OutlierFeatureAlgorithm> user : users ) {
			ScalarFeatureDeterminer functionValidator = user.getValidator().getPattern().getFeatureValidator(feature);
			User<Validator> newUser = 
						new User<Validator>( new Validator(functionValidator, feature), 
											user.getOwnSignatures() );
			ret.add( newUser );
		}
		return ret;
	}


	public static void main(String[] args) {
		try {
			ScalarFeatureERR app = new ScalarFeatureERR( INPUT_FOLDERS );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
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
}
