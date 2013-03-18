package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.alg.SignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FeaturesStatistics;
import com.vkb.app.util.FunctionFeaturesComparators;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class FeatureSelection {
	private static final String BLANKS = "                                ";
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" ), 
													new File( Environment.RESOURCES_DIR, "user2" ) };
	private static final DecimalFormat doubleFormat = new DecimalFormat("#.#####");

	private File[] inputFolders;
	
	public FeatureSelection( File[] inputFolders ) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		List<List<Signature>> signaturesGroups = new ArrayList<List<Signature>>();
		SignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		for ( File inputFolder : inputFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(inputFolder);
			signaturesGroups.add( signatureBuilder.build(inputData) );
		}
		
		FunctionFeaturesComparators signaturesCompoarators  = new FunctionFeaturesComparators();
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 0" );
		System.out.println( "*********************************************" );
		FunctionFeaturesComparators.Result result_0 = signaturesCompoarators.compare( signaturesGroups.get(0) );
		FeaturesStatistics resultStatistics_0 = dump( result_0 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 1 vs Group 0" );
		System.out.println( "*********************************************" );
		FunctionFeaturesComparators.Result result_1_0 = signaturesCompoarators.compare( signaturesGroups.get(1), signaturesGroups.get(0) );
		FeaturesStatistics resultStatistics_1_0 = dump( result_1_0 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Compare result statistics: Group 0 vs Group 0 <-> Group 1 vs Group 0" );
		System.out.println( "*********************************************" );
		compare( resultStatistics_0, resultStatistics_1_0 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 1" );
		System.out.println( "*********************************************" );
		FunctionFeaturesComparators.Result result_1 = signaturesCompoarators.compare( signaturesGroups.get(1) );
		FeaturesStatistics resultStatistics_1 = dump( result_1 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 0 vs Group 1" );
		System.out.println( "*********************************************" );
		FunctionFeaturesComparators.Result result_0_1 = signaturesCompoarators.compare( signaturesGroups.get(0), signaturesGroups.get(1) );
		FeaturesStatistics resultStatistics_0_1 = dump( result_0_1 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Compare result statistics: Group 1 vs Group 1 <-> Group 0 vs Group 1" );
		System.out.println( "*********************************************" );
		compare( resultStatistics_1, resultStatistics_0_1 );
	}
	
	
	private void compare( FeaturesStatistics inGroupResults, FeaturesStatistics foreingGroupResults ) {
		for( Map.Entry<FeatureId, ? extends StatisticalSummary> featureStat : inGroupResults.getValues().entrySet() ) {
			FeatureId feature = featureStat.getKey();
			StatisticalSummary inGroupSummary = featureStat.getValue();
			StatisticalSummary foreingGroupSummary = foreingGroupResults.getValues().get(feature);
			
			boolean result = inGroupSummary.getMax() < foreingGroupSummary.getMin();
			System.out.println( feature.getName() + ": " + (result ? "Good" : "Bad") );
			System.out.println( tab(1) + "In group maximun distance: " + inGroupSummary.getMax());
			System.out.println( tab(1) + "Foreing group minimun distance: " + foreingGroupSummary.getMin());
			System.out.println( tab(1) + "Averages score: " + foreingGroupSummary.getMean() / inGroupSummary.getMean() );
			System.out.println();
		}
	}
	
		
	private FeaturesStatistics dump( FunctionFeaturesComparators.Result result ) {
		FeaturesStatistics featuresStatistics = new FeaturesStatistics();
		for( int i=0; i<result.size(); ++i ) {
			FeaturesStatistics traceFeaturesStatistics = new FeaturesStatistics( result.trace(i) );
			System.out.println( "Trace " + i + ": " );
			dump( 1, traceFeaturesStatistics );
			
			featuresStatistics.add(result.trace(i));
			
			System.out.println();
		}
		
		System.out.println( "Global: " );
		dump( 1, featuresStatistics );
		System.out.println();
		
		return featuresStatistics;
	}


	private void dump(int tabN, FeaturesStatistics featuresStatistics) {
		for( Map.Entry<FeatureId, ? extends StatisticalSummary> featureStat : featuresStatistics.getValues().entrySet() ) {
			System.out.print( tab(tabN) + featureStat.getKey() + ": " );
			System.out.print( "min: " + print(featureStat.getValue().getMin()) );
			System.out.print( " | max: " + print(featureStat.getValue().getMax()) );
			System.out.print( " | mean: " + print(featureStat.getValue().getMean()) );
			System.out.println( " | sig2: " + print(featureStat.getValue().getVariance()) );
		}
	}

	private static String print( double v ) {
		return doubleFormat.format( v );
	}

	private static String tab(int i) {
		return BLANKS.substring(0, i*2);
	}

	public static void main(String[] args) {
		try {
			FeatureSelection prueba = new FeatureSelection( INPUT_FOLDERS );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
