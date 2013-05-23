package com.vkb.alg.determine;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.math.dtw.DefaultFunctionFeatureComparator;
import com.vkb.math.dtw.FunctionFeatureComparator;
import com.vkb.model.FunctionFeatureData;

public class FunctionFeatureDeterminerPrototype implements FunctionFeatureDeterminer{
	private static final double DEFAULT_THRESHOLD = 1.0d;
	private double threshold = DEFAULT_THRESHOLD;
	private List<FunctionFeatureData> patternSamples;
	private StatisticalSummary patternStatistics;

	private FunctionFeatureComparator comparator;
	
	private FunctionFeatureData prototypeF;
	
		
	public FunctionFeatureDeterminerPrototype( List<FunctionFeatureData> patternSamples ) throws Exception 
	{
		this( patternSamples, new DefaultFunctionFeatureComparator() );
	}
	
	public FunctionFeatureDeterminerPrototype( List<FunctionFeatureData> patternSamples,
								FunctionFeatureComparator functionFeatureComparator ) throws Exception {
		this.patternSamples = patternSamples;
		this.comparator = functionFeatureComparator;
		this.patternStatistics = calculateFunctionsStatistics( patternSamples );
		
	}
	
	public void setFunctionComparator( FunctionFeatureComparator comparator ) {
		this.comparator = comparator;
	}
	
	public void setThreshold( double threshold ) {
		this.threshold = threshold;		//alpha factor
	}

	public boolean check( FunctionFeatureData featureData ) throws Exception {
		double distance = comparator.distance( this.prototypeF, featureData );
				
		return distance < (threshold * patternStatistics.getMean());
	}
	
	
	private DescriptiveStatistics calculateFunctionsStatistics( List<FunctionFeatureData> datas ) throws Exception {
		//Calculem a l'hora D(j) i la funció prototip(j)
		
		System.out.println("Creant D(j) i funcio prototips");
		
		double distancePrototype=-1.0d;
		FunctionFeatureData protoF=null;
		double sum;
		
		DescriptiveStatistics ret = new DescriptiveStatistics();
		
		for ( int i=0; i<datas.size(); i++ ){
			sum=0.0;
			for ( int j=0; j<datas.size(); j++ ){
				if(j!=i){
					double distance = comparator.distance( datas.get(i), datas.get(j) );
					sum+=distance;
					if(j>i)
						ret.addValue( distance );
				}
			}
			if(distancePrototype>=sum || distancePrototype<0)
			{ // El primer cop agafa la primera distancia ja que distancePrototype<0
				distancePrototype=sum;
				protoF=datas.get(i);
			}
			
		}
		//this.prototypeF=new FunctionFeatureData(protoF.getSamples());
		this.prototypeF=protoF;
		System.out.println("Distancia minima:"+distancePrototype);
		return ret;
	}
	
	
}
