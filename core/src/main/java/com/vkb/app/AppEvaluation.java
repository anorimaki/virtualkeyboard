package com.vkb.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.alg.FeaturesExtractor;
import com.vkb.alg.OutlierFeatureSignatureValidator;
import com.vkb.alg.Preprocessor;
import com.vkb.alg.SignatureBuilder;
import com.vkb.alg.extract.DefaultFeaturesExtractor;
import com.vkb.alg.preprocess.EmptyPreprocessor;
import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;
import com.vkb.model.Signature;

public class AppEvaluation {
	private static boolean FILE_OUT = true;
	private static final String OUTPUT_FILE = "src/main/resources/quality.txt";
	
	private static final double MAX_LIMIT_TH = 1.0;
	private static final double INC_TH = 0.05;
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
	private double farVector[];
	private double frrVector[];
	
	private SignatureBuilder signatureBuilder;
		
	public AppEvaluation( File[] inputFolders, File[] checkFolders ) {
		this.inputFolders = inputFolders;
		this.checkFolders = checkFolders;
		
		Preprocessor preprocessor = new EmptyPreprocessor();
		FeaturesExtractor featuresExtractor = new DefaultFeaturesExtractor();
		signatureBuilder = new SignatureBuilder( preprocessor, featuresExtractor );
		
	}

	// La idea es generar una matriu on cada usuari es validi contra 
	// totes les signatures. Per cada usuari suposarem un intent que sera
	// el primer fitxer del seu directori de signatures...
	private void run() throws Exception {
		long startedTime = System.currentTimeMillis();
		
		if( FILE_OUT ) {
			fw = new FileWriter(OUTPUT_FILE);
			bw = new BufferedWriter(fw);
		}
	
		/* ********************************************** */
		/* PER DESACTIVAR LES COMPARATIVES DE FUNCIONS	  */
		/* determine.OutlierFeatureSignaturePattern		  */
		/* determine.PatternsStatistics					  */
		/* ********************************************** */
		
		List<OutlierFeatureSignatureValidator> validators = generateValidators();
		List<Signature> signaturesToCheck = generateSignaturesToCheck();
		
		
		System.out.println("Inici calcul FAR/FRR");
		System.out.println("---------------------");
		
		int index = 0;
		double threshold = 0.0;
		while ( threshold < MAX_LIMIT_TH ){
			boolean acceptMatrix[][] = new boolean[checkFolders.length][inputFolders.length];

			for ( int i=0; i<signaturesToCheck.size(); i++ ){
				
				Signature signatureToCheck = signaturesToCheck.get(i);
				
				for( int k=0; k<validators.size(); k++){
					System.out.println( "\nUsuari " + checkFolders[i].getName() + " vs. "+ 
									inputFolders[k].getName() + " amb Th:" + threshold );
					System.out.println("---------------------------------------");
			
					OutlierFeatureSignatureValidator validator = validators.get( k );
					validator.setThreshold(threshold);
					
					// Agafem la primera signatura de cada directori
					acceptMatrix[i][k] = validator.check( signatureToCheck );
				}
			}
		
			results.add( index++, acceptMatrix );
			threshold = threshold+INC_TH;
		}
		
		farVector=new double[results.size()];
		frrVector=new double[results.size()];
		
		calculateFarFrr();
		
		if(FILE_OUT){
			bw.flush();
			bw.close();
			fw.close();
		}
		
		long executionTime = System.currentTimeMillis() - startedTime;
		System.out.println("Tiempo de ejecicion: "+executionTime);
		
		// Generacio de les grafiques
		XYPlot tracesPlot = generatePlot();
		Application application = new Application();
		application.run( "FAR/FRR Graphics", tracesPlot );
		
	}


	private List<OutlierFeatureSignatureValidator> generateValidators() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		
		List<OutlierFeatureSignatureValidator> ret = new ArrayList<OutlierFeatureSignatureValidator>();
		for (int i=0; i<inputFolders.length; i++){
			String user = inputFolders[i].getName();
			File folder=inputFolders[i];
			List<CapturedData> inputData = inputDataParser.parse(folder);
			
			System.out.println("Creant patrons de l'usuari: "+user);
			OutlierFeatureSignatureValidator validator = new OutlierFeatureSignatureValidator(inputData);
		    ret.add( validator );
		}
		return ret;
	}
	
	
	private List<Signature> generateSignaturesToCheck() throws Exception {
		CapturedDatasParser inputDataParser = new CapturedDatasParser();
		
		List<Signature> ret = new ArrayList<Signature>();
		for (int i=0; i<checkFolders.length; i++){
			File folder=checkFolders[i];
			List<CapturedData> checkData = inputDataParser.parse(folder);
			
			Signature signature = signatureBuilder.build( checkData.get(0) );
			ret.add( signature );
		}
		return ret;
	}
	
	
	private void calculateFarFrr() throws Exception{
		boolean[][] matrix;
		double Th=0.0;

		for(int i=0;i<results.size();i++){
			matrix = results.elementAt(i);
			farVector[i]=calculateFar(matrix);
			frrVector[i]=calculateFrr(matrix);

			matrixDisplay(matrix, Th, farVector[i], frrVector[i]);
									
			Th=Th+INC_TH;
		}
		
		if(FILE_OUT){
			Th=0.0;
			bw.write("\n\ngraph=[");
			for(int i=0;i<farVector.length;i++){
				// Format MATLAB
				bw.write(Th+","+farVector[i]+","+frrVector[i]+";");
				Th=Th+INC_TH;
			}
			bw.write("]\n\n");
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
	
	
	private XYPlot generateBasePlot() throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("Y");
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		// Linies i punts visibles
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, true);
		plot.setRenderer( renderer );
		
		return plot;
	}

	
	private XYPlot generatePlot() throws Exception{
		XYPlot tracesPlot = generateBasePlot();
		XYSeriesCollection compleTraces = new XYSeriesCollection();
		// Series no ordenades
		XYSeries xySeriesFar = new XYSeries("FAR", false);
		XYSeries xySeriesFrr = new XYSeries("FRR", false);
		double Th=0.0;
		
		int i=0;
		while (Th<MAX_LIMIT_TH){
			xySeriesFar.add(Th,farVector[i]);
			xySeriesFrr.add(Th,frrVector[i]);
			i++;
			Th = Th+INC_TH;
		}
		
		compleTraces.addSeries(xySeriesFar);
		compleTraces.addSeries(xySeriesFrr);
		tracesPlot.setDataset( 0, compleTraces );
		
		return tracesPlot;
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
