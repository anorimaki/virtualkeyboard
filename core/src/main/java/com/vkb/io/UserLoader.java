package com.vkb.io;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.SignaturesBuilder;
import com.vkb.alg.SignatureValidatorFactory;
import com.vkb.concurrent.Parallelizer;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;
import com.vkb.model.User;

public class UserLoader<T extends SignatureBuilder>  {
	private static String SAMPLES_FOLDER = "samples";
	private static String PATTERN_FOLDER = "pattern";
	
	private ExecutorService executor;
	private SignatureValidatorFactory<T> validatorFactory;
	
	public UserLoader( ExecutorService executor, SignatureValidatorFactory<T> validatorFactory  ) {
		this.executor = executor;
		this.validatorFactory = validatorFactory;
	}
	
	public User<T> load( File userFolder ) throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		
		File patternSamplesFolder = new File( userFolder, PATTERN_FOLDER );
		List<CapturedData> patternSamples = inputDataParser.parse(patternSamplesFolder);
		T validator = validatorFactory.generateValidator( patternSamples );

		File ownSamplesFolder = new File( userFolder, SAMPLES_FOLDER );
		List<CapturedData> checkOwnSamples = inputDataParser.parse(ownSamplesFolder);
		List<Signature> ownSignatures = new SignaturesBuilder(validator).buildSignatures( checkOwnSamples );
		
		return new User<T>( validator, ownSignatures );
	}
	
	
	public List<User<T>> load( File[] inputFolders ) throws Exception {
		Parallelizer<User<T>> parallelizer = new Parallelizer<User<T>>(executor);
		for( File inputFolder : inputFolders ) {
			parallelizer.submit( new LoadTask(inputFolder) );
		}
		
		return parallelizer.join();
	}
	
	public static <K extends SignatureBuilder> User<K> 
			load( ExecutorService executor, SignatureValidatorFactory<K> validatorFactory, File userFolder  ) throws Exception {
		return new UserLoader<K>( executor, validatorFactory ).load( userFolder ); 
	}
	
	public static <K extends SignatureBuilder> List<User<K>>
			load( ExecutorService executor, SignatureValidatorFactory<K> validatorFactory, File[] userFolder  ) throws Exception {
		return new UserLoader<K>( executor, validatorFactory ).load( userFolder ); 
	}
	
	private class LoadTask implements Callable<User<T>> {
		private File userFolder;
		
		public LoadTask( File userFolder ) {
			this.userFolder = userFolder;
		}
		
		@Override
		public User<T> call() throws Exception {
			return load( userFolder );
		}
	}
}
