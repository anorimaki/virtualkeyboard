package com.vkb.quality.farfrr;

class FARFRRResult {
	public static class Matrix {
		private double[][] data;
		
		public Matrix( int size ) {
			data = new double[size][size];
		}
		
		public int size() {
			return data.length;
		}
		
		public void set( int userIndex1, int userIndex2, double value ) {
			data[userIndex1][userIndex2] = value;
		}
		
		public double get( int userIndex1, int userIndex2 ) {
			return data[userIndex1][userIndex2];
		}
	}
	
	private Matrix matrix;
	private double far;
	private double frr;
	
	public FARFRRResult( Matrix matrix, double far, double frr ) {
		this.matrix = matrix;
		this.far = far;
		this.frr = frr;
	}
	
	public double getFRR() {
		return frr;
	}
	
	public double getFAR() {
		return far;
	}
	
	public Matrix getMatrix() {
		return matrix;
	}
}
