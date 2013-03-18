package com.vkb.gui;

import java.awt.BasicStroke;

public enum LineStroke {
	SOLID( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_MITER, 10.0f ) ),
	DASHED( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_MITER, 10.0f, getPattern(1), 0.0f ) ), 
	DASH_DOTTED( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_MITER, 10.0f, getPattern(2), 0.0f ) ), 
	DOTTED( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, getPattern(3), 0.0f ) );
	
	private static float[] getPattern( int i ) {
		final float[][] pattern = { {10.0f}, {10.0f,10.0f}, {10.0f,10.0f,2.0f,10.0f}, {1.0f,3.0f} };
		return pattern[i];
	}
	
	private BasicStroke stroke;
	
	private LineStroke( BasicStroke stroke ) {
		this.stroke = stroke;
	}
	
	public BasicStroke getStroke() {
		return stroke;
	}
}
