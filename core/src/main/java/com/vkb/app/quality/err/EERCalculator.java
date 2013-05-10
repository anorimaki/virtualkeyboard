package com.vkb.app.quality.err;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.vkb.app.model.User;
import com.vkb.concurrent.Parallelizer;
import com.vkb.model.Signature;

public class EERCalculator {
	private ExecutorService executor;
	private ThresholdedSignatureValidatorFactory signatureValidatorFactory;
	private double[] thresholdsToCheck;
	
	public EERCalculator( ExecutorService executor,
							ThresholdedSignatureValidatorFactory signatureValidatorFactory,
							double[] thresholdsToCheck ) {
		this.executor = executor;
		this.signatureValidatorFactory = signatureValidatorFactory;
		this.thresholdsToCheck = thresholdsToCheck;
	}
	
	
	public List<ERRResult> execute( List<User> users ) throws Exception {
		List<ThresholdedSignatureValidator> validators = createValidators( users );
		
		List<List<Signature>> signaturesList = getSignaturesList( users );
		
		return executeValidations( validators, signaturesList );
	}
		
	
	private List<ERRResult> executeValidations( List<ThresholdedSignatureValidator> validators,
										List<List<Signature>> signaturesList ) throws Exception {
		Parallelizer<TaskResult> parallelizer = new Parallelizer<TaskResult>( executor );
		for ( ThresholdedSignatureValidator validator : validators ) {
			parallelizer.submit( new Task( validator, signaturesList, thresholdsToCheck )  );
		}
		List<TaskResult> taskResults = parallelizer.join();
		
		List<ERRResult> ret = new ArrayList<ERRResult>();
		for( int i=0; i<thresholdsToCheck.length; ++i ) {
			ERRResult thresholdResult = computeResultForThreshold( taskResults, i );
			ret.add( thresholdResult );
		}
		return ret;
	}
	
		
	private ERRResult computeResultForThreshold( List<TaskResult> taskResults, int thresholdIndex ) {
		
		ERRResult.Matrix matrixResult = new ERRResult.Matrix( taskResults.size() );
		ERRCounters indicatorCounters = new ERRCounters();
		
		for( int validatorIndex=0; validatorIndex<taskResults.size(); ++validatorIndex ) {
			computeResultForValidator( taskResults.get(validatorIndex), thresholdIndex, validatorIndex, 
									matrixResult, indicatorCounters );
		}
		
		return new ERRResult( matrixResult, indicatorCounters.getFAR(), indicatorCounters.getFRR() );
	}
	

	private void computeResultForValidator( TaskResult taskResult, int thresholdIndex, int userIndex,
									ERRResult.Matrix matrixResult, ERRCounters indicatorCounters  ) {
		for ( int userSignaturesIndex=0; userSignaturesIndex<taskResult.usersSize(); ++userSignaturesIndex ) {
			int total = taskResult.countTotalSignatures( thresholdIndex, userSignaturesIndex );
			int passed = taskResult.countPassedSignatures( thresholdIndex, userSignaturesIndex );
			
			matrixResult.set( userIndex, userSignaturesIndex, (double)passed/(double)total );
			
			if ( userSignaturesIndex==userIndex ) {
				indicatorCounters.addFRR( passed, total );
			}
			else {
				indicatorCounters.addFAR( passed, total );
			}
		}
	}


	private List<List<Signature>> getSignaturesList(List<User> users) {
		List<List<Signature>> ret = new ArrayList<List<Signature>>();
		for( User user : users ) {
			ret.add( user.getOwnSignatures() );
		}
		return ret;
	}



	private List<ThresholdedSignatureValidator> createValidators( List<User> users ) throws Exception {
		Parallelizer<ThresholdedSignatureValidator> parallelizer = new Parallelizer<ThresholdedSignatureValidator>( executor );
		for( User user : users ) {
			final User currentUser = user;
			
			parallelizer.submit( new Callable<ThresholdedSignatureValidator>() {
					@Override
					public ThresholdedSignatureValidator call() throws Exception {
						return signatureValidatorFactory.generateSignatureValidator(currentUser);
					}
				});
		}
		return parallelizer.join();
	}
		
}
