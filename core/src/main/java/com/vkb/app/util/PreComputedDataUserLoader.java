package com.vkb.app.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.vkb.alg.outlierfeature.ConfigurableOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithmFactory;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithmTraits;
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
		
		return NoOpUserLoaderTraits.convert( users, new OutlierFeatureAlgorithmFactory(newAlgorithmTraits) );
	}

	public PreCalculatedFunctionFeatureComparator precalculate( 
							List<User<NoOpUserLoaderTraits.Validator>> users, 
							FunctionFeatureComparator funtionComparator ) throws Exception {
		
		if ( users.isEmpty() ) {
			throw new Exception( "Users can't be empty" );
		}
		
		Map<FeatureId, List<FunctionFeatureData>> patternFeaturesDatas =
				Signatures.extractFeatureDatasByModel( users.get(0).getValidator().getPatternSignatures(),
													FunctionFeatureData.class );
		
		return precalculate( users, funtionComparator, patternFeaturesDatas.keySet() );
	}
	
	public PreCalculatedFunctionFeatureComparator precalculate( 
							List<User<NoOpUserLoaderTraits.Validator>> users,
							FunctionFeatureComparator funtionComparator,
							Set<FeatureId> features ) throws Exception {
		Parallelizer<PreCalculatedFunctionFeatureComparator> parallelizer = 
							new Parallelizer<PreCalculatedFunctionFeatureComparator>( executor );
		for( User<NoOpUserLoaderTraits.Validator> user : users ) {
			parallelizer.submit( new Task( users, user, funtionComparator, features ) );
		}
		
		List<PreCalculatedFunctionFeatureComparator> partialResults = parallelizer.join();
		
		PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();
		for( PreCalculatedFunctionFeatureComparator partialResult : partialResults ) {
			ret.put( partialResult );
		}
		return ret;
	}
	
	
	private static class Task implements Callable<PreCalculatedFunctionFeatureComparator> {
		private User<Validator> user;
		private List<User<Validator>> users;
		private FunctionFeatureComparator comparator;
		private Set<FeatureId> features;

		public Task( List<User<Validator>> users, User<Validator> user, 
					FunctionFeatureComparator comparator, Set<FeatureId> features ) {
			this.user = user;
			this.users = users;
			this.comparator = comparator;
			this.features = features;
		}

		@Override
		public PreCalculatedFunctionFeatureComparator call() throws Exception {
			List<Signature> patternSignatures = user.getValidator().getPatternSignatures();
			
			PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();
			for( FeatureId feature : features ) {
				List<FunctionFeatureData> patternFeatureDatas =
							Signatures.extractFeatureData( patternSignatures, feature );
				
				ret.put( comparator, patternFeatureDatas );
				
				for( User<NoOpUserLoaderTraits.Validator> user2 : users ) {
					List<Signature> userSignatures = user2.getOwnSignatures();
					List<FunctionFeatureData> userFeatureDatas = 
							Signatures.extractFeatureData( userSignatures, feature );
						
					addUserSignaturers( ret, patternFeatureDatas, userFeatureDatas );
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
