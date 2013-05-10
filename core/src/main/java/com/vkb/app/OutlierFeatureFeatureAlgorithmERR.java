package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.outlierfeature.OutlierFeatureSignatureValidator;
import com.vkb.app.model.User;
import com.vkb.app.quality.err.EERCalculator;
import com.vkb.app.quality.err.ERRResult;
import com.vkb.app.quality.err.ThresholdedSignatureValidator;
import com.vkb.app.quality.err.ThresholdedSignatureValidatorFactory;
import com.vkb.app.quality.err.ui.ERRDisplay;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.UserLoader;
import com.vkb.model.Signature;

public class OutlierFeatureFeatureAlgorithmERR {
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
	private static double ThresholdsToCheck[] = { 0.05d, 0.1d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.45d,
		0.5d, 0.55d, 0.60d, 0.65d, 0.7d, 0.75d, 0.8d, 0.85d, 0.9d, 0.95d };
	
	public OutlierFeatureFeatureAlgorithmERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		users = new UserLoader(executor).load(inputFolders);
	}
	
	
	private static class ThresholdedSignatureValidatorImpl implements 
					ThresholdedSignatureValidator {
		
		private OutlierFeatureSignatureValidator validator ;
		
		public ThresholdedSignatureValidatorImpl( User user ) {
			this.validator =  user.getValidationAlgorithm().getValidator();
		}

		@Override
		public void setThreshold(double threshold) {
			validator.setThreshold(threshold);
		}

		@Override
		public boolean check(Signature signature) throws Exception {
			return validator.check(signature);
		}
	}
	

	private static class ThresholdedSignatureValidatorFactoryImpl implements 
					ThresholdedSignatureValidatorFactory {
		@Override
		public ThresholdedSignatureValidator generateSignatureValidator( User user ) {
			return new ThresholdedSignatureValidatorImpl( user );
		}
	}
	
	
	public void run() throws Exception {
		ThresholdedSignatureValidatorFactoryImpl factory = 
					new ThresholdedSignatureValidatorFactoryImpl();
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
			OutlierFeatureFeatureAlgorithmERR app = new OutlierFeatureFeatureAlgorithmERR( INPUT_FOLDERS );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
