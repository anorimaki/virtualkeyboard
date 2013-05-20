package com.vkb.app;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.chart.plot.XYPlot;

import com.vkb.alg.outlierfeature.ConfigurableOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.DefaultOutlierFeatureAlgorithmTraits;
import com.vkb.alg.outlierfeature.OutlierFeatureAlgorithm;
import com.vkb.app.util.Environment;
import com.vkb.app.util.FARFRRPlotter;
import com.vkb.app.util.PreComputedDataUserLoader;
import com.vkb.gui.Application;
import com.vkb.model.User;
import com.vkb.quality.farfrr.ERRCalculator;
import com.vkb.quality.farfrr.FARFRRCalculator;
import com.vkb.quality.farfrr.ui.FARFRRPrinter;

public class OutlierFeatureFeatureAlgorithmERR {
	private static final File INPUT_FOLDERS[] = { 
		new File( Environment.RESOURCES_DIR, "user_a" ),
		new File( Environment.RESOURCES_DIR, "user_doh" ),
		new File( Environment.RESOURCES_DIR, "user_egg" ),
		new File( Environment.RESOURCES_DIR, "user_fj" ),
		new File( Environment.RESOURCES_DIR, "user_jig" ),
		new File( Environment.RESOURCES_DIR, "user_ma" ),
		new File( Environment.RESOURCES_DIR, "user_xf" ) };
	private static int NTHREADS = INPUT_FOLDERS.length;
	
	private static double PATTERN_THRESHOLD = 0.4d;
	private List<User<OutlierFeatureAlgorithm>> users;
	private ExecutorService executor;
	private static double ThresholdsToCheck[] = { 0.05d, 0.1d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.45d,
		0.5d, 0.55d, 0.60d, 0.65d, 0.7d, 0.75d, 0.8d, 0.85d, 0.9d, 0.95d };
	
	public OutlierFeatureFeatureAlgorithmERR( File[] inputFolders ) throws Exception {
		executor = Executors.newFixedThreadPool( NTHREADS );
		
		ConfigurableOutlierFeatureAlgorithmTraits algorithmTraits =
				new ConfigurableOutlierFeatureAlgorithmTraits( DefaultOutlierFeatureAlgorithmTraits.getInstance() );
		
		algorithmTraits.setThreshold( PATTERN_THRESHOLD );
		
		long startTime = System.currentTimeMillis();
		
		PreComputedDataUserLoader preComputeFunctionDistances = new PreComputedDataUserLoader( executor );
		this.users = preComputeFunctionDistances.load( inputFolders, algorithmTraits );
		
		long loadTime = System.currentTimeMillis() - startTime;
		System.out.println( "Users loaded in " + loadTime/1000 + " seconds." );
	}
	
	
	public void run() throws Exception {
		FARFRRCalculator errCalculator = new FARFRRCalculator( executor );
		
		long startTime = System.currentTimeMillis();
		
		List<FARFRRCalculator.Result> result = errCalculator.execute( users, ThresholdsToCheck );
		
		FARFRRPrinter printer = new FARFRRPrinter();
		printer.print( ThresholdsToCheck, result );
		
		long endTime = System.currentTimeMillis() - startTime;
		
		ERRCalculator.Result errResult = ERRCalculator.calculate( result, ThresholdsToCheck );
		System.out.println( "***************************************************************" );
		System.out.println( "Time to resolve: " + endTime/1000 + " seconds." );
		System.out.println( "ERR " + errResult.getValue() + " on threshold " + errResult.getThreshold() );

		FARFRRPlotter plotter = new FARFRRPlotter();
		Application application = new Application();
		XYPlot tracesPlot = plotter.plot( ThresholdsToCheck, result );
		application.run( "FAR/FRR Graphics", tracesPlot );
	}

	
	public static void main(String[] args) {
		try {
			OutlierFeatureFeatureAlgorithmERR app = new OutlierFeatureFeatureAlgorithmERR( INPUT_FOLDERS );
			app.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
