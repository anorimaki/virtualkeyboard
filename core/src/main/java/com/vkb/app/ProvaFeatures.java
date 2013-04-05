package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vkb.alg.SignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.io.CapturedDataParser;
import com.vkb.model.*;

public class ProvaFeatures {
	private static final String BLANKS = "                                ";
	//private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1/A_192.168.7.13_1358442748589.json" )};
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1/A_192.168.7.13_1358442752195.json" )};
	private static final DecimalFormat doubleFormat = new DecimalFormat("#.#####");

	private File[] inputFolders;
	
	public ProvaFeatures ( File[] inputFolders ) {
		this.inputFolders = inputFolders;
	}

	private void run() throws Exception {
		CapturedDataParser inputDataParser = new CapturedDataParser();
		SignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		Signature sig=null;
		
		for ( File inputFolder : inputFolders ) {
			CapturedData inputData = inputDataParser.parse(inputFolder);
			sig = signatureBuilder.build(inputData);
		}
		if(sig!=null){
			
			Feature f = sig.getFeature(FeatureId.POSITION_X);
			FunctionFeatureData ffd = f.getData();
			
			System.out.println(ffd.toString());
			
			f = sig.getFeature(FeatureId.POSITION_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			
			f = sig.getFeature(FeatureId.VELOCITY_X);
			ffd = f.getData();
			System.out.println(ffd.toString());
			
			f = sig.getFeature(FeatureId.VELOCITY_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			
			f = sig.getFeature(FeatureId.ACCELERATION_X);
			ffd = f.getData();
			System.out.println(ffd.toString());
			
			f = sig.getFeature(FeatureId.ACCELERATION_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			
			f = sig.getFeature(FeatureId.RELATION_X_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
						
			f=sig.getFeature(FeatureId.POSITION_X_AVG);
			ScalarFeatureData sfd = f.getData();
			System.out.println("AVG_X: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.POSITION_Y_AVG);
			sfd = f.getData();
			System.out.println("AVG_Y: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.VELOCITY_X_AVG);
			sfd = f.getData();
			System.out.println("AVG_VEL_X: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.VELOCITY_Y_AVG);
			sfd = f.getData();
			System.out.println("AVG_VEL_Y: "+sfd.toString());

			f=sig.getFeature(FeatureId.ACCELERATION_X_AVG);
			sfd = f.getData();
			System.out.println("AVG_ACC_X: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.ACCELERATION_Y_AVG);
			sfd = f.getData();
			System.out.println("AVG_ACC_Y: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.AREA_X);
			sfd = f.getData();
			System.out.println("AREA_X: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.AREA_Y);
			sfd = f.getData();
			System.out.println("AREA_Y: "+sfd.toString());
			
			f=sig.getFeature(FeatureId.RELATION_AREA);
			sfd = f.getData();
			System.out.println("REL_AREA: "+sfd.toString());
			
		}
		
		
		System.out.println("\nOK Termination...");
	}
	
		
	public static void main(String[] args) {
		try {
			ProvaFeatures prueba = new ProvaFeatures( INPUT_FOLDERS );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
