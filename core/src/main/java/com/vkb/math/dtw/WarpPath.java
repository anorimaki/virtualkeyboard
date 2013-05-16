package com.vkb.math.dtw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import com.vkb.math.DiscreteFunction;
import com.vkb.math.DiscreteVectorFunction;

public class WarpPath {
	public static class Cell {
		private int column;
		private int row;
		
		public Cell( int column, int row ) {
			this.column = column;
			this.row = row;
		}
		
		public int getColumn() {
			return column;
		}
		
		public int getRow() {
			return row;
		}
		
		@Override
		public String toString() {
			return "(" + column + "," + row + ")";
		}
	}
	
	private class IteratorImpl implements Iterator<Cell> {
		private int position = path.size()-1;
		
		@Override
		public boolean hasNext() {
			return position >= 0; 
		}

		@Override
		public Cell next() {
			Cell ret = path.get(position);
			--position;
			return ret;
		}

		@Override
		public void remove() {
		}
	}
	
	private class IterableImpl implements Iterable<Cell> {
		@Override
		public Iterator<Cell> iterator() {
			return new IteratorImpl();
		}
	}
	
	private List<Cell> path;
	
	public WarpPath() {
		path = new ArrayList<Cell>();
	}
	
	public void pushFront( int column, int row ) {
		path.add( new Cell(column, row) );
	}
	
	public int size() {
		return path.size();
	}
	
	public Cell get( int index ) {
		return path.get( (path.size()-1)-index );
	}
	
	public Iterable<Cell> getCells() {
		return new IterableImpl();
	}
	
	public Iterator<Cell> iterator() {
		return new IteratorImpl();
	}
	
	
	public Pair<DiscreteFunction, DiscreteFunction> warp( DiscreteFunction f1, DiscreteFunction f2 ) {
		DiscreteFunction warpF1 = new DiscreteFunction( f1.getName() );
		DiscreteFunction warpF2 = new DiscreteFunction( f2.getName() );
		
		int i=0;
		for ( Cell cell : getCells() ) {
			DiscreteFunction.Point p1 = f1.get( cell.getColumn() );
			DiscreteFunction.Point p2 = f2.get( cell.getRow() );
			
			double time = calculateTime( i, p1.getX(), p2.getX() );
			warpF1.add( time, p1.getY() );
			warpF2.add( time, p2.getY() );
			
			i++;
		}
		
		return new Pair<DiscreteFunction, DiscreteFunction>( warpF1, warpF2 );
	}
	
	
	public Pair<DiscreteVectorFunction, DiscreteVectorFunction> warp( 
						DiscreteVectorFunction f1, DiscreteVectorFunction f2 ) {
		DiscreteVectorFunction warpF1 = new DiscreteVectorFunction( f1.getDimensions() );
		DiscreteVectorFunction warpF2 = new DiscreteVectorFunction( f2.getDimensions() );

		int i=0;
		for ( Cell cell : getCells() ) {
			DiscreteVectorFunction.Point p1 = f1.get( cell.getColumn() );
			DiscreteVectorFunction.Point p2 = f2.get( cell.getRow() );
			
			double time = calculateTime( i, p1.getX(), p2.getX() );
			warpF1.add( time, p1.getValues() );
			warpF2.add( time, p2.getValues() );

			i++;
		}

		return new Pair<DiscreteVectorFunction, DiscreteVectorFunction>( warpF1, warpF2 );
	}
	
	private double calculateTime(int i, double x, double y) {
		return (x+y)/2;
	//	return i;
	}
}
