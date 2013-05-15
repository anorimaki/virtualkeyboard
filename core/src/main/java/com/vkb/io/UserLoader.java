package com.vkb.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
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
	private static String SAMPLES_FOLDER_STARTS = "samples_";
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

		List<Signature> ownSignatures = loadSamples( new SignaturesBuilder(validator), 
													inputDataParser, userFolder );
		
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
	
	private List<Signature> loadSamples( SignaturesBuilder signaturesBuilder, 
			CapturedDatasParser inputDataParser, File userFolder ) throws Exception {
		File[] samplesFolders = userFolder.listFiles( new FilenameFilter() {
				@Override
				public boolean accept( File dir, String name ) {
					File folder = new File( dir, name );
					return name.startsWith( SAMPLES_FOLDER_STARTS ) && folder.isDirectory();
				}
			} );
	
		List<Signature> ret = new ArrayList<Signature>();
		for ( File sampleFolders : samplesFolders ) {
			List<CapturedData> capturesDatas = inputDataParser.parse(sampleFolders);
			ret.addAll( signaturesBuilder.buildSignatures(capturesDatas) );
		}
		return ret;
	}
}
