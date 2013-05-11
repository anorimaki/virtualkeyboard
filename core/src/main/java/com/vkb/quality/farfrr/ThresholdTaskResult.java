package com.vkb.quality.farfrr;

import java.util.ArrayList;
import java.util.List;

class ThresholdTaskResult {
	private List<List<boolean[]>> data;
	
	public ThresholdTaskResult() {
		data = new ArrayList<List<boolean[]>>();
	}
	
	public void add( List<boolean[]> thresholdResult ) {
		data.add( thresholdResult );
	}
	
	public int countTotalSignatures( int thresholdIndex, int userindex ) {
		return data.get(thresholdIndex).get(userindex).length;
	}
	
	public int countPassedSignatures( int thresholdIndex, int userindex ) {
		int counter = 0;
		for( boolean b : data.get(thresholdIndex).get(userindex) ) {
			if (b) {
				++counter;
			}
		}
		return counter;
	}
}
