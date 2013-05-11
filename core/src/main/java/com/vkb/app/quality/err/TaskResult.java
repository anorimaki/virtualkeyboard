package com.vkb.app.quality.err;

import java.util.List;

class TaskResult {
	private List<boolean[]> data;
	
	public TaskResult( List<boolean[]> data ) {
		this.data = data;
	}
	
	public int countTotalSignatures( int userindex ) {
		return data.get(userindex).length;
	}
	
	public int countPassedSignatures( int userindex ) {
		int counter = 0;
		for( boolean b : data.get(userindex) ) {
			if (b) {
				++counter;
			}
		}
		return counter;
	}
}
