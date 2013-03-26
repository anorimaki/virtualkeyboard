package com.vkb.app.util;

import java.util.Arrays;
import java.util.List;

import com.vkb.model.Signature;

public class FunctionFeaturesComparators {
	public static class Result {
		private FunctionFeatureComparator.Result[][] data;
		
		Result( FunctionFeatureComparator.Result[][] data ) {
			this.data = data;
		}
		
		public FunctionFeatureComparator.Result[] trace( int index ) {
			return data[index];	
		}
		
		public int size() {
			return data.length;
		}
		
		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for( int i=0; i<data.length; ++i ) {
				ret.append( "T " + i + ":\n");
				for( int j=0; j<data[i].length; ++j ) {
					if ( i != j ) {
						ret.append( j + ": " + data[i][j].toString() + "\n");
					}
				}
			}
			return ret.toString();
		}
	}
	
	private FunctionFeatureComparator traceComparator = new FunctionFeatureComparator();
	
	public Result compare( Signature trace, List<Signature> traces ) throws Exception {
		return compare( Arrays.asList( trace), traces );
	}

	public Result compare( List<Signature> traces1, List<Signature> traces2 ) throws Exception {
		FunctionFeatureComparator.Result[][] results = new FunctionFeatureComparator.Result[traces1.size()][];
		
		for ( int i=0; i<traces1.size(); ++i ) {
			FunctionFeatureComparator.Result[] result = internalCompare( traces1.get(i), traces2 );
			results[i] = result;
		}
		
		return new Result( results );
	}
	
	public Result compare( List<Signature> traces ) throws Exception {
		FunctionFeatureComparator.Result[][] results = new FunctionFeatureComparator.Result[traces.size()][traces.size()];
		for ( int i=0; i<traces.size(); ++i ) {
			for( int j=i+1; j<traces.size(); ++j ) {
				FunctionFeatureComparator.Result result = traceComparator.compare( traces.get(i), traces.get(j) );
				results[i][j] = result;
				results[j][i] = result;
			}
		}
		
		return new Result( results );
	}
	
	private FunctionFeatureComparator.Result[] internalCompare( Signature trace, List<Signature> traces ) throws Exception {
		FunctionFeatureComparator.Result[] results = new FunctionFeatureComparator.Result[traces.size()];
		
		for ( int i=0; i<traces.size(); ++i ) {
			FunctionFeatureComparator.Result result = traceComparator.compare( trace, traces.get(i) );
			results[i] = result;
		}
		
		return results;
	}
}
