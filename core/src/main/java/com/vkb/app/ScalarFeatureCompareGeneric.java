package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.alg.GenericSignatureValidator;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FeaturesStatistics;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;

public class ScalarFeatureCompareGeneric {
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" )};
	private static final File CHECK_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "CapturaUser1" )};
		
	private File[] inputFolders;
	private File[] checkFolders;
	
	public ScalarFeatureCompareGeneric( File[] inputFolders, File[] checkFolders ) {
		this.inputFolders = inputFolders;
		this.checkFolders = checkFolders;
	}

	private void run() throws Exception {
		List<CapturedData> inputData=null;
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
	
		// Suposem que sols analitzem un directori amb signatures
		for ( File inputFolder : inputFolders ) {
			inputData = inputDataParser.parse(inputFolder);
		}
		
		GenericSignatureValidator gsv = new GenericSignatureValidator(inputData);

		// Suposem una unica signature al directori de checking (DANGER!)
		List<List<Signature>> signaturesCheckGroups = new ArrayList<List<Signature>>();
		for ( File checkFolder : checkFolders ) {
			inputData = inputDataParser.parse(checkFolder);
		}

		// De moment desestimem el valor retornat perquè encara no està fet el tema de la
		// comparativa de totes les features.
		boolean provisionalDummy = gsv.check(inputData.get(0));
	}
	
	
	public static void main(String[] args) {
		try {
			ScalarFeatureCompareGeneric prueba = new ScalarFeatureCompareGeneric( INPUT_FOLDERS, CHECK_FOLDERS );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
