package com.vkb.app.aligment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDatasParser;
import com.vkb.math.DiscreteFunction;
import com.vkb.math.dtw.FunctionWarper;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signature;

public class FunctionFeatureAligment {
	private File inputFolder1;
	private File inputFolder2;
	private FeatureId featureId;
	
	public FunctionFeatureAligment( File inputFolder1, File inputFolder2, 
								FeatureId featureId ) {
		this.inputFolder1 = inputFolder1;
		this.inputFolder2 = inputFolder2;
		this.featureId = featureId;
	}
	

	public void run( Pair<String,FunctionFeatureAligmentMode>... modes ) throws Exception {
		run( Arrays.asList(modes) );
	}
		
	public void run( Iterable<Pair<String,FunctionFeatureAligmentMode>> modes ) throws Exception {
		CapturedDatasParser parser = new CapturedDatasParser();
		List<CapturedData> capturedDatas1 = parser.parse( inputFolder1 ) ;
		List<CapturedData> capturedDatas2 = parser.parse( inputFolder2 ) ;
		
		GenericSignatureBuilder traceBuilder = new DefaultSignatureBuilder();
		List<Signature> traces1 = traceBuilder.buildSignatures(capturedDatas1);
		List<Signature> traces2 = traceBuilder.buildSignatures(capturedDatas2);
		
		Application application = new Application();
		for( Pair<String,FunctionFeatureAligmentMode> aligmentMode : modes ) {
			List<XYPlot> plots = new ArrayList<XYPlot>();
	//		plots.add( apply( aligmentMode.getSecond(), traces1.get(0), traces1.get(1) ) );		//Same user
	//		plots.add( apply( aligmentMode.getSecond(), traces1.get(0), traces2.get(0) ) );		//Different user
			plots.add( apply( aligmentMode.getSecond(), traces2.get(1), traces2.get(2) ) );		//Same user
			plots.add( apply( aligmentMode.getSecond(), traces1.get(2), traces2.get(2) ) );		//Different user

			application.start( aligmentMode.getFirst(), plots );
		}
		
		application.join();
	}
	
		
	private XYPlot apply( FunctionFeatureAligmentMode aligmentMode, Signature signature1,
							Signature signature2 ) throws Exception {
		FunctionFeatureData fv1 = signature1.getFeature( featureId ).getData();
		DiscreteFunction f1 = fv1.getSamples();
		FunctionFeatureData fv2 = signature2.getFeature( featureId ).getData();
		DiscreteFunction f2 = fv2.getSamples();
		
		FunctionWarper.Result result = aligmentMode.apply( signature1, signature2 );
		
		FuntionAligmentUserInfoGenerator infoGenerator = new FuntionAligmentUserInfoGenerator();
		infoGenerator.print( f1, f2, result );
		return infoGenerator.generatePlot(f1, f2, result);
	}
}
