package com.vkb.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.vkb.alg.GenericSignatureValidator;
import com.vkb.alg.CreatePatterns;
import com.vkb.app.util.DefaultSignatureBuilder;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FeaturesStatistics;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.FeatureId;
import com.vkb.model.Signature;
import com.vkb.alg.determine.PatternsStatistics;

public class AppEvaluation {
	private static boolean FILE_OUT = true;
	private static final String OUTPUT_FILE = "src/main/resources/quality.txt";
	
	private static final double MAX_LIMIT_TH = 1.0;
	private static final double INC_TH = 0.1;
	private static final double AUTHENTICATED_USERS = 7.0;
	private static final double FALSE_USERS = (7.0 * 7.0)-AUTHENTICATED_USERS;
	
		
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user1" ),
		new File( Environment.RESOURCES_DIR, "user2" ),
		new File( Environment.RESOURCES_DIR, "user3" ),
		new File( Environment.RESOURCES_DIR, "user4" ),
		new File( Environment.RESOURCES_DIR, "user5" ),
		new File( Environment.RESOURCES_DIR, "user6" ),
		new File( Environment.RESOURCES_DIR, "user7" ) };
	
	private static final File CHECK_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "CapturaUser1" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser2" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser3" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser4" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser5" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser6" ),
		new File( Environment.RESOURCES_DIR, "CapturaUser7" ) };
	
	private File[] inputFolders;
	private File[] checkFolders;
	private FileWriter fw = null;
	private BufferedWriter bw= null;
	
	
	
	private Vector<boolean[][]> results = new Vector<boolean[][]>();
	private Vector<PatternsStatistics> patternsVector = new Vector<PatternsStatistics>();
		
	public AppEvaluation( File[] inputFolders, File[] checkFolders ) {
		this.inputFolders = inputFolders;
		this.checkFolders = checkFolders;
	}

	// La idea es generar una matriu on cada usuari es validi contra 
	// totes les signatures. Per cada usuari suposarem un intent que sera
	// el primer fitxer del seu directori de signatures...
	private void run() throws Exception {
		boolean acceptMatrix[][];
		long startedTime = System.currentTimeMillis();
		
		double Th=0.0;
		int index=0;

		File inputFolder;
		File checkFolder;
		List<CapturedData> inputData=null;
		List<CapturedData> checkData=null;
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		GenericSignatureValidator gsv;
		String user;
		
		if(FILE_OUT)
		{
			fw = new FileWriter(OUTPUT_FILE);
			bw = new BufferedWriter(fw);
		}
	
		/* ********************************************** */
		/* PER DESACTIVAR LES COMPARATIVES DE FUNCIONS	  */
		/* determine.OutlierFeatureSignaturePattern		  */
		/* determine.PatternsStatistics					  */
		/* ********************************************** */
		
		// Creem un vector amb el patro de cada usuari
		CreatePatterns cP = new CreatePatterns();
		
		for (int k=0;k<inputFolders.length;k++){
			user = inputFolders[k].getName();
			inputFolder=inputFolders[k];
			inputData = inputDataParser.parse(inputFolder);
			
			System.out.println("Creant patrons de l'usuari: "+user);
		    PatternsStatistics pS = cP.createPatterns(inputData);	
		    patternsVector.add(k, pS);
		}
		
		System.out.println("Inici calcul FAR/FRR");
		System.out.println("---------------------");
		
		while (Th<MAX_LIMIT_TH){
			acceptMatrix = new boolean[checkFolders.length][inputFolders.length];
			gsv = new GenericSignatureValidator(Th);
			
			// Recorregut per tots els directoris per escollir la signatura a fer el check (0)	
			for (int i=0;i<checkFolders.length;i++){
				checkData = inputDataParser.parse(checkFolders[i]);
				// Recorregut per la BD que formen les mostres de tots els directoris -> Vector pSVector (no pot ser hash per ordre)
				
				for(int k=0;k<patternsVector.size();k++){
					System.out.println("\nUsuari "+checkFolders[i].getName()+" vs. "+inputFolders[k].getName()+" amb Th:"+Th);
					System.out.println("---------------------------------------");
			
					// Agafem la primera signatura de cada directori
					acceptMatrix[i][k] = gsv.check(checkData.get(0),patternsVector.elementAt(k));
				}
			}
		
			results.add(index, acceptMatrix);
			Th = Th+INC_TH;
			index++;
		}
		
		calculateFarFrr();
		
		if(FILE_OUT){
			bw.flush();
			bw.close();
			fw.close();
		}
		
		long executionTime = System.currentTimeMillis() - startedTime;
		System.out.println("Tiempo de ejecición: "+executionTime);
	}
	
	
	private void matrixDisplay(boolean[][] acceptMatrix, double Th, double far, double frr) throws Exception {
		// Matriu quadrada
		int N=acceptMatrix.length;
		
		if(FILE_OUT){
			bw.write("\nMatriu identificacions Th:"+Th+"\n");
			bw.write("-------------------------------\n");
		}else{
			System.out.println("\nMatriu identificacions Th:"+Th);
			System.out.println("-------------------------------");
		}
		
		for (int i=0;i<N;i++){
			for (int k=0;k<N;k++){
				if(acceptMatrix[i][k]){
					if(FILE_OUT)
						bw.write("1  ");
					else
					    System.out.print("1  ");
				}
				else{
					if(FILE_OUT)
						bw.write("0  ");
					else
						System.out.print("0  ");
				}
			}
			if(FILE_OUT)
				bw.write("\n");
			else
				System.out.print("\n");
		}
		
		if(FILE_OUT){
			bw.write("-> FRR:"+frr+"   FAR:"+far+"\n");
		}else{
			System.out.println("-> FRR:"+frr+"   FAR:"+far);
		}
		
	}
	
	private void calculateFarFrr() throws Exception{
		boolean[][] matrix;
		double Th=0.0;
		double far, frr;
		
		for(int i=0;i<results.size();i++){
			matrix = results.elementAt(i);
			far=calculateFar(matrix);
			frr=calculateFrr(matrix);
			matrixDisplay(matrix, Th, far, frr);
									
			Th=Th+INC_TH;
		}
		
	}
	
	private double calculateFar(boolean[][] matrix){
		double far=0.0;
		
		// Evitem la diagonal ja que son usuaris autentics -> (i!=k)
		for(int i=0;i<matrix.length;i++){
			for (int k=0;k<matrix.length;k++){
				if (i!=k && matrix[i][k]){
					far++;
				}
			}
		}
		// Matriu triangular
		far=far/FALSE_USERS;
		
		return far;
	}
	
	private double calculateFrr(boolean[][] matrix){
		double frr=0.0;
		
		// Simplement mirem si en alguna posicio de la diagonal no s'ha identificat
		// un usuari correcte
		for (int i=0;i<matrix.length;i++)
			if(!matrix[i][i])
				frr++;
		
		frr=frr/AUTHENTICATED_USERS;
		
		return frr;
	}
	
		
	public static void main(String[] args) {
		try {
			AppEvaluation prueba = new AppEvaluation( INPUT_FOLDERS, CHECK_FOLDERS);
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
