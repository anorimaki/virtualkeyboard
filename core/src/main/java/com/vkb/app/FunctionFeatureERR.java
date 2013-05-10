package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.determine.FunctionFeatureDeterminer;
import com.vkb.app.model.User;
import com.vkb.app.quality.err.EERCalculator;
import com.vkb.app.quality.err.ERRResult;
import com.vkb.app.quality.err.ThresholdedSignatureValidator;
import com.vkb.app.quality.err.ThresholdedSignatureValidatorFactory;
import com.vkb.app.quality.err.ui.ERRDisplay;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.UserLoader;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;

public class FunctionFeatureERR {
	private static int NTHREADS = 10;
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
	
	private List<User> users = new ArrayList<User>();
	private ExecutorService executor;
	private static double ThresholdsToCheck[] = { 0.7d, 0.75d, 0.8d, 0.85d, 0.9d, 0.95d,
		1.0d, 1.05d, 1.1d, 1.15d, 1.2d, 1.25d };
	
	public FunctionFeatureERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		users = new UserLoader(executor).load(inputFolders);
	}
	
	
	private static class ThresholdedSignatureValidatorImpl implements 
					ThresholdedSignatureValidator {
		
		private FunctionFeatureDeterminer featureValidator ;
		private FeatureId featureId ;
		
		public ThresholdedSignatureValidatorImpl( User user, FeatureId featureId ) {
			this.featureId = featureId; 
			this.featureValidator = 
					user.getValidationAlgorithm().getValidator().getFeatureValidator(featureId);
		}

		@Override
		public void setThreshold(double threshold) {
			featureValidator.setThreshold(threshold);
		}

		@Override
		public boolean check(Signature signature) throws Exception {
			FunctionFeatureData featureData = signature.getFeatures().get(featureId).getData();
			return featureValidator.check(featureData);
		}
	}
	

	private static class ThresholdedSignatureValidatorFactoryImpl implements 
					ThresholdedSignatureValidatorFactory {
		private FeatureId featureId ;
		
		public ThresholdedSignatureValidatorFactoryImpl( FeatureId featureId ) {
			this.featureId = featureId;
		}
		
		@Override
		public ThresholdedSignatureValidator generateSignatureValidator( User user ) {
			return new ThresholdedSignatureValidatorImpl( user, featureId );
		}
	}
	
	
	public void run() throws Exception {
		ThresholdedSignatureValidatorFactoryImpl factory = 
					new ThresholdedSignatureValidatorFactoryImpl( FeatureId.VELOCITY_Y );
		EERCalculator errCalculator = new EERCalculator( executor, factory, ThresholdsToCheck );
		
		List<ERRResult> result = errCalculator.execute(users);
		
		ERRDisplay printer = new ERRDisplay();
		printer.print( ThresholdsToCheck, result );

		Application application = new Application();
		XYPlot tracesPlot = printer.plot( ThresholdsToCheck, result );
		application.run( "FAR/FRR Graphics", tracesPlot );
	}
	

	public static void main(String[] args) {
		try {
			FunctionFeatureERR app = new FunctionFeatureERR( INPUT_FOLDERS );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
