package com.vkb;

import com.google.gson.annotations.SerializedName;

public class TracePoint {
	@SerializedName("x")
	private long x;
	
	@SerializedName("y")
	private long y;
	
	@SerializedName("t")
	private long time;
	
	public TracePoint( long x, long y, long time ) {
		this.x = x;
		this.y = y;
		this.time = time;
	}
	
	public long getX() {
		return x;
	}
	
	public long getY() {
		return y;
	}
	
	public long getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return time + ":" + "(" + x + "," + y + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    final TracePoint other = (TracePoint) obj;
	    return (time == other.time) && (x == other.x) && (y == other.y);
	}
	
	@Override
	public int hashCode() {
	    final int PRIME = 31;
	    int result = 1;
	    result = PRIME * result + (int)time;
	    result = PRIME * result + (int)x;
	    result = PRIME * result + (int)y;
	    return result;
	}
	
}
