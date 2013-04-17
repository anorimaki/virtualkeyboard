package com.vkb.app;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.analysis.function.Constant;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.vkb.app.util.Environment;
import com.vkb.gui.Application;
import com.vkb.gui.DataConvert;
import com.vkb.io.CapturedDatasParser;
import com.vkb.math.DiscreteFunction;
import com.vkb.model.CapturedData;

public class PrintInput {
	private File inputFolder;
	
	public PrintInput( File inputFile ) {
		this.inputFolder = inputFile;
	}
	
	public void run() throws Exception {
		CapturedDatasParser parser = new CapturedDatasParser();
		List<CapturedData> capturedDatas = parser.parse( inputFolder ) ;
		
		XYPlot tracesPlot = generateTracePlot();
		XYSeriesCollection compleTraces = new XYSeriesCollection();
		int i = 0;
		for( CapturedData capturedData : capturedDatas ) {
			DiscreteFunction positionFunction = capturedData.getTrace().getPositionFunction();
			positionFunction = positionFunction.multiply( new Constant(-1) );
			
			compleTraces.addSeries( DataConvert.getXYSeries( String.valueOf(i++), positionFunction.getPoints() ) );
		}
		tracesPlot.setDataset( 0, compleTraces );
		
		Application application = new Application();
		application.run( "VerifyInterpolation", tracesPlot );
	}
	
	
	private XYPlot generateTracePlot() throws Exception {
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("Y");
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		plot.setRenderer( renderer );
		
		return plot;
	}

	
	public static void main(String[] args) {
		try {
			File inputFile = new File( Environment.RESOURCES_DIR, "user7" );
			PrintInput prueba = new PrintInput( inputFile );
			prueba.run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
