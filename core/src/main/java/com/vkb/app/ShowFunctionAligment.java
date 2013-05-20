package com.vkb.app;

import java.io.File;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.app.aligment.FuntionAligmentUserInfoGenerator;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDataParser;
import com.vkb.math.DiscreteFunction;
import com.vkb.math.DiscreteVectorFunction;
import com.vkb.math.dtw.EuclideanCellCostFunction;
import com.vkb.math.dtw.FastDTW;
import com.vkb.math.dtw.FunctionWarper;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;

public class ShowFunctionAligment {
	private File trace1;
	private File trace2;
	private FeatureId featureId;
	private FunctionWarper functionAligner;
	
	public ShowFunctionAligment( File trace1, File trace2, FeatureId featureId ) {
		this.trace1 = trace1;
		this.trace2 = trace2;
		this.featureId = featureId;
		this.functionAligner = new FastDTW( new EuclideanCellCostFunction() );
	}
	
	
	public void run() throws Exception {
		CapturedDataParser parser = new CapturedDataParser();
		
		CapturedData capturedDatas1 = parser.parse( trace1 ) ;
		CapturedData capturedDatas2 = parser.parse( trace2 ) ;
		
		GenericSignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		Signature signature1 = signatureBuilder.buildSignature(capturedDatas1);
		Signature signature2 = signatureBuilder.buildSignature(capturedDatas2);
		
		XYPlot plot = apply( signature1, signature2 );
		
		Application application = new Application();
		application.run( "Alignment of " + featureId.getName(), plot );
	}	
	
	
	private XYPlot apply( Signature signature1, Signature signature2 ) throws Exception {
		FunctionFeatureData fv1 = signature1.getFeature( featureId ).getData();
		DiscreteFunction f1 = fv1.getSamples();
		FunctionFeatureData fv2 = signature2.getFeature( featureId ).getData();
		DiscreteFunction f2 = fv2.getSamples();

		FunctionWarper.Result result = functionAligner.align( 
								new DiscreteVectorFunction(f1), new DiscreteVectorFunction(f2) );
		
		FuntionAligmentUserInfoGenerator infoGenerator = new FuntionAligmentUserInfoGenerator();
		infoGenerator.print( f1, f2, result );
		return infoGenerator.generatePlot(f1, f2, result);
	}
	
	public static void main( String[] args ) {
		try {
			ShowFunctionAligment prueba = new ShowFunctionAligment( 
				new File( Environment.RESOURCES_DIR, "user_jig/pattern/JIG_MANUS_1364981565919.json" ),
				new File( Environment.RESOURCES_DIR, "user_jig/pattern/JIG_MANUS_1364981580900.json" ),
				FeatureId.ACCELERATION );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
