package com.vkb.math.dtw;

import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.util.DistanceFunction;
import com.vkb.math.DiscreteVectorFunction;

public class FastDTW implements FunctionComparator {
	public final static int DEFAULT_SEARCH_RADIUS = 3;
	private DistanceFunction distanceFunction;
	private int searchRadius;

	public FastDTW( CellCostFunction cellCostFunction ) {
		this( cellCostFunction, DEFAULT_SEARCH_RADIUS );
	}
	
	public FastDTW( CellCostFunction cellCostFunction, int searchRadius ) {
		this( new CellCostFunctionToDistanceFunctionConverter( cellCostFunction ), searchRadius );
	}
	
	public FastDTW( DistanceFunction distanceFunction ) {
		this( distanceFunction, DEFAULT_SEARCH_RADIUS );
	}
	
	public FastDTW( DistanceFunction distanceFunction, int searchRadius ) {
		this.distanceFunction = distanceFunction;
		this.searchRadius = searchRadius;
	}
	
	@Override
	public double distance(DiscreteVectorFunction f1, DiscreteVectorFunction f2) throws Exception {
		TimeSeries ts1 = DataConvert.getTimeSeries(f1);
		TimeSeries ts2 = DataConvert.getTimeSeries(f2);
		
		return com.fastdtw.dtw.FastDTW.getWarpDistBetween( ts1, ts2, searchRadius, distanceFunction );
	}

	private static class CellCostFunctionToDistanceFunctionConverter implements DistanceFunction {
		private CellCostFunction cellCostFunction;
		
		public CellCostFunctionToDistanceFunctionConverter( CellCostFunction cellCostFunction ) {
			this.cellCostFunction = cellCostFunction;
		}
		

		@Override
		public double calcDistance( double[] vector1, double[] vector2 ) {
			DiscreteVectorFunction v1 = new DiscreteVectorFunction( "dummy" );
			v1.add( 0.0, vector1 );
			
			DiscreteVectorFunction v2 = new DiscreteVectorFunction( "dummy" );
			v2.add( 0.0, vector2 );
			
			try {
				cellCostFunction.init( v1, v2 );
				return cellCostFunction.calculateCost(0, 0);
			} 
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
