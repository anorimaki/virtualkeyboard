package com.vkb.app.util;

import java.util.Arrays;
import java.util.List;

import com.vkb.model.Signature;

public class SignaturesComparators {
	public static class Result {
		private SignatureComparator.Result[][] data;
		
		Result( SignatureComparator.Result[][] data ) {
			this.data = data;
		}
		
		public SignatureComparator.Result[] trace( int index ) {
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
	
	private SignatureComparator traceComparator = new SignatureComparator();
	
	public Result compare( Signature trace, List<Signature> traces ) throws Exception {
		return compare( Arrays.asList( trace), traces );
	}

	public Result compare( List<Signature> traces1, List<Signature> traces2 ) throws Exception {
		SignatureComparator.Result[][] results = new SignatureComparator.Result[traces1.size()][];
		
		for ( int i=0; i<traces1.size(); ++i ) {
			SignatureComparator.Result[] result = internalCompare( traces1.get(i), traces2 );
			results[i] = result;
		}
		
		return new Result( results );
	}
	
	public Result compare( List<Signature> traces ) throws Exception {
		SignatureComparator.Result[][] results = new SignatureComparator.Result[traces.size()][traces.size()];
		for ( int i=0; i<traces.size(); ++i ) {
			for( int j=i+1; j<traces.size(); ++j ) {
				SignatureComparator.Result result = traceComparator.compare( traces.get(i), traces.get(j) );
				results[i][j] = result;
				results[j][i] = result;
			}
		}
		
		return new Result( results );
	}
	
	private SignatureComparator.Result[] internalCompare( Signature trace, List<Signature> traces ) throws Exception {
		SignatureComparator.Result[] results = new SignatureComparator.Result[traces.size()];
		
		for ( int i=0; i<traces.size(); ++i ) {
			SignatureComparator.Result result = traceComparator.compare( trace, traces.get(i) );
			results[i] = result;
		}
		
		return results;
	}
}
