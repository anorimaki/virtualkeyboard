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
	private static int NTHREADS = 6;
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
	
	private List<User<UserLoaderValidator>> users;
	private ExecutorService executor;
	private static double ThresholdsToCheck[] = buildThresholdsToCheck();
	
	public FunctionFeatureERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		
		SignatureValidatorFactory<UserLoaderValidator> factory = new UserLoaderValidatorFactory();
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
		Set<FeatureId> features = FeatureId.getByModel(FunctionFeatureData.class);
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
		
		List<User<UserLoaderValidator.Validator>> usersToCheck = generateUsers( feauture );
		
		List<FARFRRCalculator.Result> result = errCalculator.execute( usersToCheck, ThresholdsToCheck );
		
		FARFRRPrinter printer = new FARFRRPrinter();
		printer.print( ThresholdsToCheck, result );

		FARFRRPlotter plotter = new FARFRRPlotter();
		return plotter.plot( ThresholdsToCheck, result );
	}
	

	private List<User<UserLoaderValidator.Validator>> generateUsers( FeatureId feauture ) throws Exception {
		List<User<UserLoaderValidator.Validator>> ret = new ArrayList<User<UserLoaderValidator.Validator>>();
		for( User<UserLoaderValidator> user : users ) {
			User<UserLoaderValidator.Validator> newUser = 
						new User<UserLoaderValidator.Validator>( user.getValidator().getValidator(feauture), 
														user.getOwnSignatures() );
			ret.add( newUser );
		}
		return ret;
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
	
	
	private static class UserLoaderValidatorFactory 
					implements SignatureValidatorFactory<UserLoaderValidator> {
		private DefaultSignatureBuilder signatureBuilder;
		
		public UserLoaderValidatorFactory() {
			this.signatureBuilder = new DefaultSignatureBuilder();
		}

		@Override
		public UserLoaderValidator generateValidator( List<CapturedData> patternSamples ) throws Exception {
			List<Signature> patternSignatures = signatureBuilder.buildSignatures( patternSamples );
			Map<FeatureId, List<FunctionFeatureData>> featuresDatas = 
					Signatures.extractFeatureDatasByModel( patternSignatures, FunctionFeatureData.class );
			
			Map<FeatureId, FunctionFeatureDeterminer> functionFeatureDeterminers = 
											new HashMap<FeatureId, FunctionFeatureDeterminer>();
			for( Map.Entry<FeatureId, List<FunctionFeatureData>> featureDatas : featuresDatas.entrySet() ) {
				functionFeatureDeterminers.put( featureDatas.getKey(), 
								new FunctionFeatureDeterminer( featureDatas.getValue() ) );
			}
			
			return new UserLoaderValidator( functionFeatureDeterminers, signatureBuilder );
		}
		
	}
	
	private static class UserLoaderValidator implements SignatureBuilder {
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
		
		private Map<FeatureId, FunctionFeatureDeterminer> validators ;
		private SignatureBuilder signatureBuilder;
		
		public UserLoaderValidator( Map<FeatureId, FunctionFeatureDeterminer> validators,
													SignatureBuilder signatureBuilder ) {
			this.signatureBuilder = signatureBuilder;
			this.validators = validators;
		}
		
		
		public Validator getValidator( FeatureId featureId ) {
			return new Validator( validators.get(featureId), featureId );
		}
		

		@Override
		public Signature buildSignature(CapturedData capturedData) throws Exception {
			return signatureBuilder.buildSignature(capturedData);
		}
	}
}
