package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.fastdtw.dtw.TimeWarpInfo;
import com.vkb.Feature;
import com.vkb.RawTrace;
import com.vkb.Trace;
import com.vkb.alg.FeaturesStatistics;
import com.vkb.alg.TraceBuilder;
import com.vkb.alg.TraceComparator;
import com.vkb.alg.TracesComparators;
import com.vkb.io.TracesParser;

public class FeatureSelection {
	private static final String BLANKS = "                                ";
	private static final File INPUT_FOLDERS[] = { new File( "src/resources/sara" ), new File( "src/resources/jig" ) };
	private static final DecimalFormat doubleFormat = new DecimalFormat("#.#####");

	private File[] inputFolders;
	
	public FeatureSelection( File[] inputFolders ) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		TracesParser tracesParser = new TracesParser();
		List<List<Trace>> tracesGroups = new ArrayList<List<Trace>>();
		TraceBuilder traceBuilder = new TraceBuilder();
		for ( File inputFolder : inputFolders ) {
			List<RawTrace> traces = tracesParser.parse(inputFolder);
			tracesGroups.add( traceBuilder.build(traces) );
		}
		
		TracesComparators tracesComparator  = new TracesComparators();
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 0" );
		System.out.println( "*********************************************" );
		TracesComparators.Result result_0 = tracesComparator.compare( tracesGroups.get(0) );
		FeaturesStatistics resultStatistics_0 = dump( result_0 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 1 vs Group 0" );
		System.out.println( "*********************************************" );
		TracesComparators.Result result_1_0 = tracesComparator.compare( tracesGroups.get(1), tracesGroups.get(0) );
		FeaturesStatistics resultStatistics_1_0 = dump( result_1_0 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Compare result statistics: Group 0 vs Group 0 <-> Group 1 vs Group 0" );
		System.out.println( "*********************************************" );
		compare( resultStatistics_0, resultStatistics_1_0 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 1" );
		System.out.println( "*********************************************" );
		TracesComparators.Result result_1 = tracesComparator.compare( tracesGroups.get(1) );
		FeaturesStatistics resultStatistics_1 = dump( result_1 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Group 0 vs Group 1" );
		System.out.println( "*********************************************" );
		TracesComparators.Result result_0_1 = tracesComparator.compare( tracesGroups.get(0), tracesGroups.get(1) );
		FeaturesStatistics resultStatistics_0_1 = dump( result_0_1 );
		
		System.out.println( "*********************************************" );
		System.out.println( "**** Compare result statistics: Group 1 vs Group 1 <-> Group 0 vs Group 1" );
		System.out.println( "*********************************************" );
		compare( resultStatistics_1, resultStatistics_0_1 );
	}
	
	
	private void compare( FeaturesStatistics inGroupResults, FeaturesStatistics foreingGroupResults ) {
		for( Map.Entry<Feature, ? extends StatisticalSummary> featureStat : inGroupResults.getValues().entrySet() ) {
			Feature feature = featureStat.getKey();
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
	

	private void dump(TraceComparator.Result result) {
		for( Map.Entry<Feature, TimeWarpInfo> featureResult : result.getPartialResults().entrySet() ) {
			System.out.print( featureResult.getKey() + ": " );
			System.out.print( featureResult.getValue().getDistance() );
			System.out.println();
		}
	}
	
		
	private FeaturesStatistics dump( TracesComparators.Result result ) {
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
		for( Map.Entry<Feature, ? extends StatisticalSummary> featureStat : featuresStatistics.getValues().entrySet() ) {
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
