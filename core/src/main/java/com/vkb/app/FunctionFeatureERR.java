package com.vkb.app;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.alg.SignatureValidatorFactory;
import com.vkb.alg.determine.FunctionFeatureDeterminer;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FARFRRPlotter;
import com.vkb.gui.Application;
import com.vkb.io.UserLoader;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Signatures;
import com.vkb.model.User;
import com.vkb.quality.farfrr.FARFRRCalculator;
import com.vkb.quality.farfrr.ui.FARFRRPrinter;

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
	
	private List<User<ThresholdedSignatureValidatorAdaptor>> users;
	private ExecutorService executor;
	private static double ThresholdsToCheck[] = buildThresholdsToCheck();
	
	public FunctionFeatureERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		
		SignatureValidatorFactory<ThresholdedSignatureValidatorAdaptor> factory = 
							new ThresholdedSignatureValidatorAdaptorFactory( FeatureId.VELOCITY_Y );
		users = UserLoader.load( executor, factory, inputFolders );
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


	public void run() throws Exception {
		FARFRRCalculator errCalculator = new FARFRRCalculator( executor );
		
		List<FARFRRCalculator.Result> result = errCalculator.execute( users, ThresholdsToCheck );
		
		FARFRRPrinter printer = new FARFRRPrinter();
		printer.print( ThresholdsToCheck, result );

		FARFRRPlotter plotter = new FARFRRPlotter();
		Application application = new Application();
		XYPlot tracesPlot = plotter.plot( ThresholdsToCheck, result );
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
	
	private static class ThresholdedSignatureValidatorAdaptorFactory 
					implements SignatureValidatorFactory<ThresholdedSignatureValidatorAdaptor> {
		private FeatureId featureId;
		private DefaultSignatureBuilder signatureBuilder;
		
		public ThresholdedSignatureValidatorAdaptorFactory( FeatureId featureId ) {
			this.featureId = featureId;
			this.signatureBuilder = new DefaultSignatureBuilder();
		}

		@Override
		public ThresholdedSignatureValidatorAdaptor generateValidator( List<CapturedData> patternSamples ) throws Exception {
			List<Signature> patternTraces = signatureBuilder.buildSignatures( patternSamples );
			List<FunctionFeatureData> patternData = Signatures.extractFeatureData(patternTraces, featureId);
			FunctionFeatureDeterminer validator = new FunctionFeatureDeterminer( patternData );
			return new ThresholdedSignatureValidatorAdaptor( featureId, validator, signatureBuilder );
		}
		
	}
	
	private static class ThresholdedSignatureValidatorAdaptor implements ThresholdedSignatureValidator, SignatureBuilder {
		private FunctionFeatureDeterminer featureValidator ;
		private FeatureId featureId;
		private SignatureBuilder signatureBuilder;
		
		public ThresholdedSignatureValidatorAdaptor( FeatureId featureId,
					FunctionFeatureDeterminer impl, SignatureBuilder signatureBuilder ) {
			this.featureValidator = impl;
			this.featureId = featureId;
			this.signatureBuilder = signatureBuilder;
		}
		
		@Override
		public void setThreshold( double threshold ) {
			featureValidator.setThreshold(threshold);
		}
		
		@Override
		public boolean check(Signature signature) throws Exception {
			FunctionFeatureData featureData = signature.getFeatures().get(featureId).getData();
			return featureValidator.check(featureData);
		}

		@Override
		public Signature buildSignature(CapturedData capturedData) throws Exception {
			return signatureBuilder.buildSignature(capturedData);
		}
	}
}
