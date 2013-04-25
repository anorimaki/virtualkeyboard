package com.vkb.app;

import java.io.File;
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
	
	private File[] inputFolders;
	
	private Vector<boolean[][]> results = new Vector<boolean[][]>();
	private Vector<PatternsStatistics> pSVector = new Vector<PatternsStatistics>();
		
	public AppEvaluation( File[] inputFolders ) {
		this.inputFolders = inputFolders;
	}

	// La idea es generar una matriu on cada usuari es validi contra 
	// totes les signatures. Per cada usuari suposarem un intent que sera
	// el primer fitxer del seu directori de signatures...
	private void run() throws Exception {
		boolean acceptMatrix[][];
		
		double Th=0.0;
		int index=0;

		File inputFolder;
		List<CapturedData> inputData=null;
		List<CapturedData> checkData=null;
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		GenericSignatureValidator gsv;
		String user;
	
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
		    pSVector.add(k, pS);
		}
		
		System.out.println("Inici calcul FAR/FRR");
		System.out.println("---------------------");
		
		while (Th<MAX_LIMIT_TH){
			acceptMatrix = new boolean[INPUT_FOLDERS.length][INPUT_FOLDERS.length];
			gsv = new GenericSignatureValidator(Th);
			
			// Recorregut per tots els directoris per escollir la signatura a fer el check (0)	
			for (int i=0;i<inputFolders.length;i++){
				checkData = inputDataParser.parse(inputFolders[i]);
				// Recorregut per la BD que formen les mostres de tots els directoris -> Vector pSVector (no pot ser hash per ordre)
				
				for(int k=0;k<pSVector.size();k++){
					System.out.println("\nUsuari "+inputFolders[i].getName()+" vs. "+inputFolders[k].getName());
					System.out.println("---------------------------------------");
			
					// Agafem la primera signatura de cada directori
					acceptMatrix[i][k] = gsv.check(checkData.get(0),pSVector.elementAt(k));
				}
			}
		
			results.add(index, acceptMatrix);
			Th = Th+INC_TH;
			index++;
		}
		
		calculateFarFrr();
	}
	
	
	private void matrixDisplay(boolean[][] acceptMatrix, double Th){
		// Matriu quadrada
		int N=acceptMatrix.length;
		
		System.out.println("\nMatriu identificacions Th:"+Th);
		System.out.println("-------------------------------");
		for (int i=0;i<N;i++){
			for (int k=0;k<N;k++){
				if(acceptMatrix[i][k])
					System.out.print("1  ");
				else
					System.out.print("0  ");
			}
			System.out.print("\n");
		}
	}
	
	private void calculateFarFrr(){
		boolean[][] matrix;
		double Th=0.0;
		double far, frr;
		
		for(int i=0;i<results.size();i++){
			matrix = results.elementAt(i);
			matrixDisplay(matrix, Th);
			far=calculateFar(matrix);
			frr=calculateFrr(matrix);
						
			System.out.println("-> FRR:"+frr+"   FAR:"+far);
			
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
			AppEvaluation prueba = new AppEvaluation( INPUT_FOLDERS);
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
