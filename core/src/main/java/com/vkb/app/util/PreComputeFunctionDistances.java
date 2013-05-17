package com.vkb.app.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.vkb.alg.outlierfeature.ConfigurableOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.alg.outlierfeature.OutlierFeaturePatternGenerator;
import com.vkb.concurrent.Parallelizer;
import com.vkb.io.NoOpUserLoaderTraits;
import com.vkb.io.NoOpUserLoaderTraits.Validator;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.math.dtw.PreCalculatedFunctionFeatureComparator;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;
import com.vkb.model.Signatures;
import com.vkb.model.User;

public class PreComputeFunctionDistances {
	private FunctionFeatureComparator comparator;
	private ExecutorService executor;
	
	public PreComputeFunctionDistances( ExecutorService executor, FunctionFeatureComparator comparator ) {
		this.comparator = comparator;
		this.executor = executor;
	}
	

	public PreCalculatedFunctionFeatureComparator apply( List<User<NoOpUserLoaderTraits.Validator>> users ) throws Exception {
		Parallelizer<PreCalculatedFunctionFeatureComparator> parallelizer = 
							new Parallelizer<PreCalculatedFunctionFeatureComparator>( executor );
		for( User<NoOpUserLoaderTraits.Validator> user : users ) {
			parallelizer.submit( new Task( users, user, comparator ) );
		}
		
		List<PreCalculatedFunctionFeatureComparator> partialResults = parallelizer.join();
		
		PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();
		for( PreCalculatedFunctionFeatureComparator partialResult : partialResults ) {
			ret.put( partialResult );
		}
		return ret;
	}

	
	public List<User<OutlierFeatureAlgorithm>> generateUsers(
					List<User<NoOpUserLoaderTraits.Validator>> users,
					ConfigurableOutlierFeatureAlgorithmTraits algorithmTraits ) throws Exception {
		
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
			List<Signature> allSignatures = new ArrayList<Signature>();
			
			allSignatures.addAll( user.getValidator().getPatternSignatures() );
			
			for( User<NoOpUserLoaderTraits.Validator> user2 : users ) {
				if ( user2 != user) {
					allSignatures.addAll( user2.getOwnSignatures() );
				}
			}
			
			Map<FeatureId, List<FunctionFeatureData>> features =
						Signatures.extractFeatureDatasByModel( allSignatures, FunctionFeatureData.class );
			
			PreCalculatedFunctionFeatureComparator ret = new PreCalculatedFunctionFeatureComparator();
			for( Map.Entry<FeatureId, List<FunctionFeatureData>> featureDatas : features.entrySet() ) {
				ret.put( comparator, featureDatas.getValue() );
			}
			return ret;
		}
		
	}
}
