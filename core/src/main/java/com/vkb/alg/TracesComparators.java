package com.vkb.alg;

import java.util.Arrays;
import java.util.List;

import com.vkb.Trace;

public class TracesComparators {
	public static class Result {
		private TraceComparator.Result[][] data;
		
		Result( TraceComparator.Result[][] data ) {
			this.data = data;
		}
		
		public TraceComparator.Result[] trace( int index ) {
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
	
	private TraceComparator traceComparator = new TraceComparator();
	
	public Result compare( Trace trace, List<Trace> traces ) throws Exception {
		return compare( Arrays.asList( trace), traces );
	}

	public Result compare( List<Trace> traces1, List<Trace> traces2 ) throws Exception {
		TraceComparator.Result[][] results = new TraceComparator.Result[traces1.size()][];
		
		for ( int i=0; i<traces1.size(); ++i ) {
			TraceComparator.Result[] result = internalCompare( traces1.get(i), traces2 );
			results[i] = result;
		}
		
		return new Result( results );
	}
	
	public Result compare( List<Trace> traces ) throws Exception {
		TraceComparator.Result[][] results = new TraceComparator.Result[traces.size()][traces.size()];
		for ( int i=0; i<traces.size(); ++i ) {
			for( int j=i+1; j<traces.size(); ++j ) {
				TraceComparator.Result result = traceComparator.compare( traces.get(i), traces.get(j) );
				results[i][j] = result;
				results[j][i] = result;
			}
		}
		
		return new Result( results );
	}
	
	private TraceComparator.Result[] internalCompare( Trace trace, List<Trace> traces ) throws Exception {
		TraceComparator.Result[] results = new TraceComparator.Result[traces.size()];
		
		for ( int i=0; i<traces.size(); ++i ) {
			TraceComparator.Result result = traceComparator.compare( trace, traces.get(i) );
			results[i] = result;
		}
		
		return results;
	}
}
