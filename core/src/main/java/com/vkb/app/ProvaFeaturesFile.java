package com.vkb.app;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vkb.alg.GenericSignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.io.CapturedDataParser;
import com.vkb.model.*;

public class ProvaFeaturesFile {
	private static final String BLANKS = "                                ";
	//private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1/A_192.168.7.13_1358442748589.json" )};
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1/A_192.168.7.13_1358442752195.json" )};
	// Prova!
	private static final String OUTPUT_FILE = "src/main/resources/user1/A_192.168.7.13_1358442777001.out";
	private static final DecimalFormat doubleFormat = new DecimalFormat("#.#####");

	private File[] inputFolders;
	private String outputFile;
	private boolean sortidaFitxer = false;
	
	public ProvaFeaturesFile ( File[] inputFolders, String outputFile ) {
		this.inputFolders = inputFolders;
		this.outputFile = outputFile;
		sortidaFitxer = true;
	}
	
	public ProvaFeaturesFile ( File[] inputFolders) {
		this.inputFolders = inputFolders;
		this.outputFile = null;
	}

	private void run() throws Exception {
		FileWriter fw = null;
		BufferedWriter bw= null;
		
		CapturedDataParser inputDataParser = new CapturedDataParser();
		GenericSignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		Signature sig=null;
		
		if(sortidaFitxer)
		{
			fw = new FileWriter(outputFile);
			bw = new BufferedWriter(fw);
		}
		
		
		for ( File inputFolder : inputFolders ) {
			CapturedData inputData = inputDataParser.parse(inputFolder);
			sig = signatureBuilder.buildSignature(inputData);
		}
		
		if(sig!=null){
			
			Feature f = sig.getFeature(FeatureId.POSITION_X);
			FunctionFeatureData ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
			
			f = sig.getFeature(FeatureId.POSITION_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
			
			f = sig.getFeature(FeatureId.VELOCITY_X);
			ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
			
			f = sig.getFeature(FeatureId.VELOCITY_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
			
			f = sig.getFeature(FeatureId.ACCELERATION_X);
			ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
			
			f = sig.getFeature(FeatureId.ACCELERATION_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
			
			f = sig.getFeature(FeatureId.RELATION_X_Y);
			ffd = f.getData();
			System.out.println(ffd.toString());
			if(sortidaFitxer)
				bw.write(ffd.toString()+"\n");
						
			f=sig.getFeature(FeatureId.POSITION_X_AVG);
			ScalarFeatureData sfd = f.getData();
			System.out.println("AVG_X: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AVG_X: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.POSITION_Y_AVG);
			sfd = f.getData();
			System.out.println("AVG_Y: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AVG_Y: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.VELOCITY_X_AVG);
			sfd = f.getData();
			System.out.println("AVG_VEL_X: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AVG_VEL_X: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.VELOCITY_Y_AVG);
			sfd = f.getData();
			System.out.println("AVG_VEL_Y: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AVG_VEL_Y: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.ACCELERATION_X_AVG);
			sfd = f.getData();
			System.out.println("AVG_ACC_X: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AVG_ACC_X: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.ACCELERATION_Y_AVG);
			sfd = f.getData();
			System.out.println("AVG_ACC_Y: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AVG_ACC_Y: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.AREA_X);
			sfd = f.getData();
			System.out.println("AREA_X: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AREA_X: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.AREA_Y);
			sfd = f.getData();
			System.out.println("AREA_Y: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("AREA_Y: "+sfd.toString()+"\n");
			
			f=sig.getFeature(FeatureId.RELATION_AREA);
			sfd = f.getData();
			System.out.println("REL_AREA: "+sfd.toString());
			if(sortidaFitxer)
				bw.write("REL_AREA: "+sfd.toString()+"\n");
			
			if(sortidaFitxer)
				bw.close();
		}
		
		
		System.out.println("\nOK Termination...");
	}
	
		
	public static void main(String[] args) {
		try {
		    ProvaFeaturesFile prueba = new ProvaFeaturesFile( INPUT_FOLDERS, OUTPUT_FILE );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
