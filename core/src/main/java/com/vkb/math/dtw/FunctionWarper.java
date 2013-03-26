package com.vkb.math.dtw;

import com.vkb.math.DiscreteVectorFunction;

public interface FunctionWarper {
	public static class Result {
		private double distance;
		private WarpPath path;
		
 		public Result( WarpPath path, double distance ) {
 			this.path = path;
 			this.distance = distance;
		}
 		
 		public double getDistance() {
 			return distance;
 		}
 		
 		public WarpPath getPath() {
 			return path;
 		}
	}
	
	public Result align( DiscreteVectorFunction f1, DiscreteVectorFunction f2 ) throws Exception;
}
