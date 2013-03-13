package com.vkb.alg.extract;

import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.UnivariateFunction;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.math.DiscreteFunction;
import com.vkb.math.FunctionUtils;
import com.vkb.model.CapturedData;
import com.vkb.model.Feature;
import com.vkb.model.Features;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.ScalarFeatureData;
import com.vkb.model.FeatureId;
import com.vkb.model.Trace;

public class DefaultFeaturesExtractor implements FeaturesExtractor {
	@Override
	public Features extract( CapturedData inputData ) {
		Features features = new Features();
		
		Trace rawTrace = inputData.getTrace();
		
		DiscreteFunction traceX = rawTrace.getXFunction();
		DiscreteFunction traceY = rawTrace.getYFunction();
		
		extractPositionFeatures( features, traceX, traceY );
		
		FiniteDifferencesDifferentiator differentiator = 
				new FiniteDifferencesDifferentiator( 5, 0.5d, traceX.getMinX(), traceX.getMaxX() );
		extractVelocityFeatures( features, differentiator );
		extractAccelerationFeatures( features, differentiator );
		
		extractPositionAvgFeatures( features );
		extractVelocityAvgFeatures( features );
		extractAccelerationAvgFeatures( features );
		
		TrapezoidIntegrator integrator=new TrapezoidIntegrator();
		extractAreasFeatures( integrator, features );
		
		extractRelationFeatures( features );
		extractRelationAreaFeatures (features );
		
		return features;
	}
	
	
	private void extractPositionFeatures( Features features,
								DiscreteFunction traceX, DiscreteFunction traceY ) {
		features.put( new Feature( FeatureId.POSITION_X, new FunctionFeatureData( traceX ) ) );
		features.put( new Feature( FeatureId.POSITION_Y, new FunctionFeatureData( traceY ) ) );
	}
	
	
	private void extractVelocityFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, FeatureId.VELOCITY_X, features.get(FeatureId.POSITION_X) ) );
		features.put( createDerivate( differentiator, FeatureId.VELOCITY_Y, features.get(FeatureId.POSITION_Y) ) );
	}
		

	private void extractAccelerationFeatures( Features features,
								FiniteDifferencesDifferentiator differentiator ) {
		features.put( createDerivate( differentiator, FeatureId.ACCELERATION_X, features.get(FeatureId.VELOCITY_X) ) );
		features.put( createDerivate( differentiator, FeatureId.ACCELERATION_Y, features.get(FeatureId.VELOCITY_Y) ) );
	}
	
	
	private void extractPositionAvgFeatures( Features features ) {
		features.put( createAvg( FeatureId.POSITION_X_AVG, features.get(FeatureId.POSITION_X) ) );
		features.put( createAvg( FeatureId.POSITION_Y_AVG, features.get(FeatureId.POSITION_Y) ) );
    }
	
	private void extractVelocityAvgFeatures( Features features ) {
		features.put( createAvg( FeatureId.VELOCITY_X_AVG, features.get(FeatureId.VELOCITY_X) ) );
		features.put( createAvg( FeatureId.VELOCITY_Y_AVG, features.get(FeatureId.VELOCITY_Y) ) );
    }
	
	private void extractAccelerationAvgFeatures( Features features ) {
		features.put( createAvg( FeatureId.ACCELERATION_X_AVG, features.get(FeatureId.ACCELERATION_X) ) );
		features.put( createAvg( FeatureId.ACCELERATION_Y_AVG, features.get(FeatureId.ACCELERATION_Y) ) );
    }
	
	
	private void extractAreasFeatures( TrapezoidIntegrator integrator, Features features ) {
		features.put( createInt( integrator, FeatureId.AREA_X, features.get(FeatureId.POSITION_X) ) );
		features.put( createInt( integrator, FeatureId.AREA_Y, features.get(FeatureId.POSITION_Y) ) );
    }
	
	private void extractRelationFeatures( Features features ) {
		features.put( createRel(FeatureId.RELATION_X_Y, features.get(FeatureId.POSITION_X), features.get(FeatureId.POSITION_Y) ) );
	}
	
	
	private void extractRelationAreaFeatures( Features features ) {
		features.put( createRelArea(FeatureId.RELATION_AREA, features.get(FeatureId.AREA_X), features.get(FeatureId.AREA_Y) ) );
	}
	
	
		
	private Feature createAvg( FeatureId newFeatureId, Feature originalFeature ) {
		double avgd=0.0;
		FunctionFeatureData originalFeatureData = originalFeature.getData();
		DiscreteFunction dataFunction = originalFeatureData.getSamples();
		
		// Entenc que dataFunction.getY() retorna els valors de la serie
		// ja que a l'eix X hi trobem l'escala de temps.
		DescriptiveStatistics stats = new DescriptiveStatistics(dataFunction.getY());

		avgd = stats.getMean();
		
		ScalarFeatureData data = new ScalarFeatureData(avgd);
		return new Feature( newFeatureId, data );
	}


	private Feature createInt(TrapezoidIntegrator integrator,  FeatureId newFeatureId, Feature originalFeature ) {
		double area=0.0;
		FunctionFeatureData originalFeatureData = originalFeature.getData();
		UnivariateFunction dataFunction = originalFeatureData.getFunction();
		
		// Entenc que dataFunction.getY() retorna els valors de la serie
		// ja que a l'eix X hi trobem l'escala de temps.
		area=integrator.integrate(10000, dataFunction, originalFeatureData.getMinTime(), originalFeatureData.getMaxTime());
		
		ScalarFeatureData data = new ScalarFeatureData(area);
		return new Feature( newFeatureId, data );
	}
	
	private Feature createDerivate( FiniteDifferencesDifferentiator differentiator,
											FeatureId newFeatureId,
											Feature originalFeature ) {
		FunctionFeatureData originalFeatureData = originalFeature.getData();
		
		UnivariateDifferentiableFunction derivateFunction = 
				differentiator.differentiate(originalFeatureData.getFunction());
	
		DiscreteFunction samples = FunctionUtils.sample( newFeatureId.getName(),
				derivateFunction, 1, 
				originalFeatureData.getMinTime(), originalFeatureData.getMaxTime(),  1.0d );
		
		FunctionFeatureData data = new FunctionFeatureData( newFeatureId.getName(), 
				samples.interpolate(), 
				originalFeatureData.getMinTime(), originalFeatureData.getMaxTime() );
		
		return new Feature( newFeatureId, data );
	}
	

	private Feature createRel( FeatureId newFeatureId, 	Feature originalXFeature, Feature originalYFeature ) {

		// Caldria tuilitzar la function i no els samples ¿?
		
		FunctionFeatureData originalXFeatureData = originalXFeature.getData();
		DiscreteFunction dataSamplesX = originalXFeatureData.getSamples();
		
		FunctionFeatureData originalYFeatureData = originalYFeature.getData();
		DiscreteFunction dataSamplesY = originalYFeatureData.getSamples();
		
		DiscreteFunction dataFunction = new DiscreteFunction(newFeatureId.getName());
		
		// Entenc que dataFunction.getY() retorna els valors de la serie
		// ja que a l'eix X hi trobem l'escala de temps.
		double[] tempsX=dataSamplesX.getX();
		double[] puntsX=dataSamplesX.getY();
		double[] puntsY=dataSamplesY.getY();
		
		// Cal suposar que la signatura tindra els mateixos punts capturats en X i en Y. Aixi com
		// que han estat "samplejats" amb el mateix interval de temps
		if(puntsX.length == puntsY.length)
		{
			for(int i=0;i<puntsX.length;i++)
			{
				// CHIRRIA!!!
				if(puntsY[i]>0)
					dataFunction.add(tempsX[i],(puntsX[i]/puntsY[i]));
				else
					dataFunction.add(tempsX[i],0.0d);
			}
			
		}
		
		FunctionFeatureData data = new FunctionFeatureData(dataFunction);
		
		return new Feature( newFeatureId, data );
    }
	
	
	private Feature createRelArea( FeatureId newFeatureId, 	Feature originalXFeature, Feature originalYFeature ) 
	{	
	    double rel=0.0;
	    
	    ScalarFeatureData originalXFeatureData= originalXFeature.getData();
	    double areaX = originalXFeatureData.getValue();

	    ScalarFeatureData originalYFeatureData= originalYFeature.getData();
	    double areaY = originalYFeatureData.getValue();
	    
	    rel=areaX/areaY;
	    
		ScalarFeatureData data = new ScalarFeatureData(rel);
		return new Feature( newFeatureId, data );
	}
}

