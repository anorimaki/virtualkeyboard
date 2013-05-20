package com.vkb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.NoOpUserLoaderTraits;
import com.vkb.io.UserLoader;
import com.vkb.model.FeatureId;
import com.vkb.model.FunctionFeatureData;
import com.vkb.model.Signatures;
import com.vkb.model.User;

public class ShowFunctionFeatures {
	private static FeatureId[] FeaturesToShow = { FeatureId.ACCELERATION_X, 
													FeatureId.ACCELERATION, 
													FeatureId.VELOCITY };
	private File[] inputFolders;
	private ExecutorService executor;
	private static final int MAX_TRACES_PER_PLOT = 5;
	
	public ShowFunctionFeatures( File[] inputFolders ) {
		this.inputFolders = inputFolders;
		this.executor = Executors.newFixedThreadPool( inputFolders.length );
	}
	
	
	public void run() throws Exception {
		List<User<NoOpUserLoaderTraits.Validator>> users = 
					UserLoader.load( executor, new NoOpUserLoaderTraits.Factory(), inputFolders );
		
		Application application = new Application();
		for( FeatureId featureId : FeaturesToShow ) {
			run( application, featureId, users );
		}
		application.join();
	}	
	
	
	private void run( Application application, FeatureId feature, 
						List<User<NoOpUserLoaderTraits.Validator>> users ) throws Exception {	
		List<String> titles = new ArrayList<String>();
		List<XYPlot> plots = new ArrayList<XYPlot>();
		
		for( User<NoOpUserLoaderTraits.Validator> user : users ) {
			List<FunctionFeatureData> allFeatureDatas = 
						Signatures.extractFeatureData( user.getValidator().getPatternSignatures(), feature );
			
			XYPlot plot = generatePlot( 
						allFeatureDatas.subList( 0, Math.min( allFeatureDatas.size(), MAX_TRACES_PER_PLOT ) ) );
			
			plots.add( plot );
			titles.add( user.getId() );
		}
		
		application.start( feature.getName(), titles, plots );
	}
	
	
	private XYPlot generatePlot( List<FunctionFeatureData> featureDatas ) throws Exception {
		XYPlot tracesPlot = generateBasePlot();
		XYDataset dataCollection = DataConvert.getDatasetFromFeatures(featureDatas);
		tracesPlot.setDataset( 0, dataCollection );
		return tracesPlot;
	}
	
	
	private XYPlot generateBasePlot() throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("Y");
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer( true, false );
		plot.setRenderer( renderer );
		
		return plot;
	}

	
	public static void main( String[] args ) {
		try {
			File[] inputFiles = { 
						new File( Environment.RESOURCES_DIR, "user_doh" ),
						new File( Environment.RESOURCES_DIR, "user_jig" ) };
			ShowFunctionFeatures prueba = new ShowFunctionFeatures( inputFiles );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
}
