package com.vkb.app.quality.err;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.vkb.alg.SignatureValidator;
import com.vkb.alg.ThresholdedSignatureValidator;
import com.vkb.app.model.User;
import com.vkb.concurrent.Parallelizer;
import com.vkb.model.Signature;

public class EERCalculator {
	private ExecutorService executor;
	
	public EERCalculator( ExecutorService executor ) {
		this.executor = executor;
	}
	
	public <T extends SignatureValidator> ERRResult execute( List<User<T>> users ) throws Exception {
		List<List<Signature>> signaturesList = getSignaturesList(users);
		
		Parallelizer<TaskResult> parallelizer = new Parallelizer<TaskResult>( executor );
		for ( User<T> user : users ) {
			Task task = new Task( user.getValidator(), signaturesList ); 
			parallelizer.submit( task );
		}
		List<TaskResult> taskResults = parallelizer.join();
		
		return computeResult( new TaskAdapter( taskResults ) );
	}
	
	public <T extends ThresholdedSignatureValidator> List<ERRResult> execute( List<User<T>> users, double[] thresholds ) throws Exception {
		List<List<Signature>> signaturesList = getSignaturesList(users);
		
		Parallelizer<ThresholdTaskResult> parallelizer = new Parallelizer<ThresholdTaskResult>( executor );
		for ( User<T> user : users ) {
			ThresholdTask task = new ThresholdTask( user.getValidator(), signaturesList, thresholds ); 
			parallelizer.submit( task );
		}
		List<ThresholdTaskResult> taskResults = parallelizer.join();
		
		List<ERRResult> ret = new ArrayList<ERRResult>();
		for( int i=0; i<thresholds.length; ++i ) {
			ERRResult thresholdResult = computeResult( new ThresholdTaskAdapter( taskResults, i ) );
			ret.add( thresholdResult );
		}
		return ret;
	}
	
	private ERRResult computeResult( ResultAccesor result ) {
		ERRResult.Matrix matrixResult = new ERRResult.Matrix( result.size() );
		ERRCounters indicatorCounters = new ERRCounters();
		
		for( int userIndex=0; userIndex<result.size(); ++userIndex ) {
			computeResult( result, userIndex, matrixResult, indicatorCounters );
		}
		
		return new ERRResult( matrixResult, indicatorCounters.getFAR(), indicatorCounters.getFRR() );
	}
	
	private void computeResult( ResultAccesor result, int userIndex,
				ERRResult.Matrix matrixResult, ERRCounters indicatorCounters  ) {
		for ( int userSignaturesIndex=0; userSignaturesIndex<result.size(); ++userSignaturesIndex ) {
			int total = result.totalSignatures( userIndex, userSignaturesIndex );
			int passed = result.passedSignatures( userIndex, userSignaturesIndex );
			
			matrixResult.set( userIndex, userSignaturesIndex, (double)passed/(double)total );
			
			if ( userSignaturesIndex==userIndex ) {
				indicatorCounters.addFRR( passed, total );
			}
			else {
				indicatorCounters.addFAR( passed, total );
			}
		}
	}
	
	private <T> List<List<Signature>> getSignaturesList( List<User<T>> users ) {
		List<List<Signature>> ret = new ArrayList<List<Signature>>();
		for( User<?> user : users  ) {
			ret.add( user.getOwnSignatures() );
		}
		return ret;
	}
	
	static private interface ResultAccesor {
		public int totalSignatures( int userIndex, int signatureListIndex );
		public int passedSignatures( int userIndex, int signatureListIndex );
		public int size();
	}
	
	static private class TaskAdapter implements ResultAccesor {
		private List<TaskResult> result;
		
		public TaskAdapter( List<TaskResult> result ) {
			this.result = result;
		}

		@Override
		public int totalSignatures( int userIndex, int signatureListIndex ) {
			return result.get(userIndex).countTotalSignatures( signatureListIndex );
		}

		@Override
		public int passedSignatures( int userIndex, int signatureListIndex ) {
			return result.get(userIndex).countPassedSignatures( signatureListIndex );
		}

		@Override
		public int size() {
			return result.size();
		}
	}
		
	static private class ThresholdTaskAdapter implements ResultAccesor {
		private List<ThresholdTaskResult> result;
		private int thresholIndex;
		
		public ThresholdTaskAdapter( List<ThresholdTaskResult> result, int thresholIndex ) {
			this.result = result;
			this.thresholIndex = thresholIndex;
		}

		@Override
		public int totalSignatures( int userIndex, int signatureListIndex ) {
			return result.get(userIndex).countTotalSignatures( thresholIndex, signatureListIndex );
		}

		@Override
		public int passedSignatures( int userIndex, int signatureListIndex ) {
			return result.get(userIndex).countPassedSignatures( thresholIndex, signatureListIndex );
		}
		
		@Override
		public int size() {
			return result.size();
		}
	}

}
