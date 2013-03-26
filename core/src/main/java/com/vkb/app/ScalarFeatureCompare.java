package com.vkb.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.alg.SignatureBuilder;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FeaturesStatistics;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;
import com.vkb.alg.determine.OutlierFeatureDeterminer;

public class ScalarFeatureCompare {
	private static final File INPUT_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "user1" )};
	private static final File CHECK_FOLDERS[] = { new File( Environment.RESOURCES_DIR, "CapturaUser1" )};
		
	private File[] inputFolders;
	private File[] checkFolders;
	
	public ScalarFeatureCompare( File[] inputFolders, File[] checkFolders ) {
		this.inputFolders = inputFolders;
		this.checkFolders = checkFolders;
	}

	private void run() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		List<List<Signature>> signaturesGroups = new ArrayList<List<Signature>>();
		SignatureBuilder signatureBuilder = new DefaultSignatureBuilder();
		for ( File inputFolder : inputFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(inputFolder);
			signaturesGroups.add( signatureBuilder.build(inputData) );
		}
		
		// Suposem que sols analitzem un directori amb signatures
		List<Signature> patterns = signaturesGroups.get(0);
		OutlierFeatureDeterminer determiner = new OutlierFeatureDeterminer(patterns,0.5d);

		List<List<Signature>> signaturesCheckGroups = new ArrayList<List<Signature>>();
		for ( File checkFolder : checkFolders ) {
			List<CapturedData> inputData = inputDataParser.parse(checkFolder);
			signaturesCheckGroups.add( signatureBuilder.build(inputData) );
		}
		
		// Suposem una unica signature al directori de checking (DANGER!)
		Signature forChecking = signaturesCheckGroups.get(0).get(0);
		
		// De moment desestimem el valor retornat perquè encara no està fet el tema de la
		// comparativa de totes les features.
		boolean provisionalDummy = determiner.check(forChecking);
		
		System.out.println("Patterns: "+patterns.size()+" <- checked: "+signaturesCheckGroups.get(0).size());
	}
	
	
	public static void main(String[] args) {
		try {
			ScalarFeatureCompare prueba = new ScalarFeatureCompare( INPUT_FOLDERS, CHECK_FOLDERS );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
