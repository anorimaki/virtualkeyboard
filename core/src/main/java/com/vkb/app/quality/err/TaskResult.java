package com.vkb.app.quality.err;

import java.util.ArrayList;
import java.util.List;

class TaskResult {
	private List<List<boolean[]>> data;
	
	public TaskResult() {
		data = new ArrayList<List<boolean[]>>();
	}
	
	public void add( List<boolean[]> thresholdResult ) {
		data.add( thresholdResult );
	}
	
	public int usersSize() {
		if ( data.isEmpty() ) {
			return 0;
		}
		return data.get(0).size();
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
	
	public boolean get( int thresholdIndex, int userindex, int signatureIndex ) {
		return data.get(thresholdIndex).get(userindex)[signatureIndex];
	}
}
