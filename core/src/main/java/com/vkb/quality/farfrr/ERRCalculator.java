package com.vkb.quality.farfrr;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class ERRCalculator {
	public static class Result {
		private double value;
		private double threshold;
		
		private Result( double thresholdIndex, double value ) {
			this.value = value;
			this.threshold = thresholdIndex;
		}
		
		public double getValue() {
			return value;
		}
		
		public double getThreshold() {
			return threshold;
		}
	}

	public static Result calculate( List<FARFRRCalculator.Result> results, double[] thresholds ) throws Exception {
		if ( results.size() < 1 ) {
			throw new Exception( "Not enough data ");
		}
		
		double currentMin = calculateDiff( results.get(0) );
		double previousMin = currentMin;
		int previousIndex = 0;
		
		for( int i=1; i<results.size(); ++i ) {
			previousMin = currentMin;
			currentMin = calculateDiff( results.get(i) );
			
			if ( !hasSameSign(previousMin, currentMin) ) {
				return calculateIntersection( results, thresholds, i-1, i ); 
			}
			
			double currentMinAbs = Math.abs(currentMin);
			double previousMinAbs = Math.abs(previousMin);
			if ( previousMinAbs < currentMinAbs ) {
				return calculateIntersection( results, thresholds, previousIndex, i ); 
			}
			if ( previousMinAbs > currentMinAbs ) {
				previousIndex = i-1;
			}
		}
		
		return calculateIntersection( results, thresholds, previousIndex, results.size()-1 ); 
	}
	
		
	private static Result calculateIntersection( List<FARFRRCalculator.Result> results, double[] thresholds,
												int p1, int p2 ) throws Exception {
		Line farLine = new Line( new Vector2D( thresholds[p1], results.get(p1).getFAR() ), 
								new Vector2D( thresholds[p2], results.get(p2).getFAR() ) );
		
		Line frrLine = new Line( new Vector2D( thresholds[p1], results.get(p1).getFRR() ), 
								new Vector2D( thresholds[p2], results.get(p2).getFRR() ) );
		
		Vector2D intersection = farLine.intersection(frrLine);
		
		if ( intersection==null ) {
			throw new Exception( "Can't converge to ERR" );
		}
		
		return new Result( intersection.getX(), intersection.getY() );
	}
	

	private static boolean hasSameSign( double currentMin, double nextMin ) {
		return ((currentMin>=0) && (nextMin>=0)) || ((currentMin<=0) && (nextMin<=0));
	}


	private static double calculateDiff( FARFRRCalculator.Result result ) {
		return result.getFAR() - result.getFRR();
	}
}
