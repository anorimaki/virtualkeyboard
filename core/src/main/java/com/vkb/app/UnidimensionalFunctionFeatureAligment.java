package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import com.vkb.app.aligment.FunctionFeatureAligment;
import com.vkb.app.aligment.FunctionFeatureAligmentMode;
import com.vkb.app.aligment.UnidimensionalFunctionFeatureAligmentMode;
import com.vkb.app.util.Environment;
import com.vkb.math.dtw.DerivativeCellCostFunction;
import com.vkb.math.dtw.EuclideanCellCostFunction;
import com.vkb.model.FeatureId;

public class UnidimensionalFunctionFeatureAligment {
	private FunctionFeatureAligment impl;
	
	private UnidimensionalFunctionFeatureAligment( File inputFolder1, File inputFolder2 ) {
		impl = new FunctionFeatureAligment( inputFolder1, inputFolder2, FeatureId.VELOCITY_X );
	}
	

	private void run() throws Exception {
		List<Pair<String, FunctionFeatureAligmentMode>> items = 
					new ArrayList<Pair<String, FunctionFeatureAligmentMode>>();
		items.add( new Pair<String, FunctionFeatureAligmentMode>(
						"UnidimensionalFunctionFeatureAligment - euclidean", 
						new UnidimensionalFunctionFeatureAligmentMode( FeatureId.VELOCITY_X, new EuclideanCellCostFunction() ) ) );
		items.add( new Pair<String, FunctionFeatureAligmentMode>(
						"UnidimensionalFunctionFeatureAligment - derivate", 
						new UnidimensionalFunctionFeatureAligmentMode( FeatureId.VELOCITY_X,
								new DerivativeCellCostFunction( new EuclideanCellCostFunction() ) ) ) );
		
		impl.run( items );
	}
	
	public static void main(String[] args) {
		try {
			File inputFile1 = new File( Environment.RESOURCES_DIR, "user1" );
			File inputFile2 = new File( Environment.RESOURCES_DIR, "user2" );
			UnidimensionalFunctionFeatureAligment prueba = new UnidimensionalFunctionFeatureAligment( inputFile1, inputFile2 );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
