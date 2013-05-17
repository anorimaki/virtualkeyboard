package com.vkb.app.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.vkb.alg.outlierfeature.ConfigurableOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.OutlierFeaturePatternGenerator;
import com.vkb.concurrent.Parallelizer;
import com.vkb.io.NoOpUserLoaderTraits;
import com.vkb.io.NoOpUserLoaderTraits.Validator;
import com.vkb.io.UserLoader;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.math.dtw.PreCalculatedFunctionFeatureComparator;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Signatures;
import com.vkb.model.User;

public class PreComputedDataUserLoader {
	private ExecutorService executor;
	
	public PreComputedDataUserLoader( ExecutorService executor ) {
		this.executor = executor;
	}
	
	
	public List<User<OutlierFeatureAlgorithm>> load( File[] inputFolders, 
										OutlierFeatureAlgorithmTraits algorithmTraits ) throws Exception {
		List<User<NoOpUserLoaderTraits.Validator>> users = 
				UserLoader.load( executor, new NoOpUserLoaderTraits.Factory(), inputFolders );
		
		PreCalculatedFunctionFeatureComparator preComputedDistances = 
				precalculate( users, algorithmTraits.getFunctionFeatureComparator() );
		
		ConfigurableOutlierFeatureAlgorithmTraits newAlgorithmTraits = 
				new ConfigurableOutlierFeatureAlgorithmTraits( algorithmTraits );
		newAlgorithmTraits.setFunctionFeatureComparator( preComputedDistances );
		
		return generateUsers( users, newAlgorithmTraits );
	}

	
	private PreCalculatedFunctionFeatureComparator precalculate( List<User<NoOpUserLoaderTraits.Validator>> users,
							FunctionFeatureComparator funtionComparator ) throws Exception {
		Parallelizer<PreCalculatedFunctionFeatureComparator> parallelizer = 
							new Parallelizer<PreCalculatedFunctionFeatureComparator>( executor );
		for( User<NoOpUserLoaderTraits.Validator> user : users ) {
			parallelizer.submit( new Task( users, user, funtionComparator ) );
		}
		
		List<PreCalculatedFunctionFeatureComparator> partialResults = parallelizer.join();
		
		PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();
		for( PreCalculatedFunctionFeatureComparator partialResult : partialResults ) {
			ret.put( partialResult );
		}
		return ret;
	}
	

	private List<User<OutlierFeatureAlgorithm>> generateUsers(
					List<User<NoOpUserLoaderTraits.Validator>> users,
					OutlierFeatureAlgorithmTraits algorithmTraits ) throws Exception {
		
		OutlierFeaturePatternGenerator patternGenerator = new OutlierFeaturePatternGenerator( 
				algorithmTraits.getThreshold(), algorithmTraits.getFunctionFeatureComparator() );
		
		List<User<OutlierFeatureAlgorithm>> ret = new ArrayList<User<OutlierFeatureAlgorithm>>();
		for( User<NoOpUserLoaderTraits.Validator> user : users ) {
			
			OutlierFeaturePatternGenerator.Result patternResult = 
						patternGenerator.generate( user.getValidator().getPatternSignatures() );
			
			OutlierFeatureAlgorithm algorithm = 
						new OutlierFeatureAlgorithm( patternResult.getPattern(), algorithmTraits );
			
			User<OutlierFeatureAlgorithm> newUser = 
						new User<OutlierFeatureAlgorithm>( user.getId(), algorithm, user.getOwnSignatures() );
			
			ret.add( newUser );
		}
		return ret;
	}
	
	
	private static class Task implements Callable<PreCalculatedFunctionFeatureComparator> {
		private User<Validator> user;
		private List<User<Validator>> users;
		private FunctionFeatureComparator comparator;

		public Task( List<User<Validator>> users, User<Validator> user, 
					FunctionFeatureComparator comparator ) {
			this.user = user;
			this.users = users;
			this.comparator = comparator;
		}

		@Override
		public PreCalculatedFunctionFeatureComparator call() throws Exception {
			List<Signature> patternSignatures = user.getValidator().getPatternSignatures();
			Map<FeatureId, List<FunctionFeatureData>> patternFeaturesDatas =
						Signatures.extractFeatureDatasByModel( patternSignatures, FunctionFeatureData.class );

			PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();
			for( Map.Entry<FeatureId, List<FunctionFeatureData>> patternFeatureDatas : patternFeaturesDatas.entrySet() ) {
				ret.put( comparator, patternFeatureDatas.getValue() );
				
				for( User<NoOpUserLoaderTraits.Validator> user2 : users ) {
					List<Signature> userSignatures = user2.getOwnSignatures();
					List<FunctionFeatureData> userFeatureDatas = 
							Signatures.extractFeatureData( userSignatures, patternFeatureDatas.getKey() );
						
					addUserSignaturers( ret, patternFeatureDatas.getValue(), userFeatureDatas );
				}
			}
			
			return ret;
		}
		

		private void addUserSignaturers( PreCalculatedFunctionFeatureComparator ret,
				List<FunctionFeatureData> patternValues, List<FunctionFeatureData> userValues ) throws Exception {
			for( FunctionFeatureData patternValue : patternValues) {
				for( FunctionFeatureData userValue : userValues) {
					double value = comparator.distance( patternValue, userValue );
					ret.put( patternValue, userValue, value );
				}
			}
		}
		
	}
}
