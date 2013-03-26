package com.vkb.gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Application {
	private class Window implements Runnable {
		private Iterable<XYPlot> plots;
		private String title;
		
		public Window( String title, Iterable<XYPlot> plots ) {
			this.title = title ;
			this.plots = plots ;
		}
		

		@Override
		public void run() {
			ApplicationFrame mainFrame = new ApplicationFrame(title);
			
			GridLayout layout = new GridLayout(0,1);
			mainFrame.setLayout(layout);
			
			for( XYPlot plot : plots ) {
				JFreeChart chart =
						new JFreeChart( "demo", JFreeChart.DEFAULT_TITLE_FONT,
										plot, true );
			
				JPanel chartPanel = new ChartPanel(chart);
				mainFrame.add( chartPanel );
			}
				
			mainFrame.pack();
		    RefineryUtilities.centerFrameOnScreen(mainFrame);
		    mainFrame.setVisible(true);
		}
	}
	
	
	private List<Thread> windowThreads = new ArrayList<Thread>();
	
	
	public void start( String title, Iterable<XYPlot> plots ) {
		Thread newThread = new Thread( new Window(title, plots) );
		newThread.start();
		windowThreads.add( newThread );
	}
	
	
	public void join() throws InterruptedException {
		for( Thread th : windowThreads ) {
			th.join();
		}
	}
	
		
	public void run( String title, XYPlot plot ) {
		run( title, Arrays.asList(plot) );
	}
	
	public void run( String title, Iterable<XYPlot> plots ) {
		new Window( title, plots ).run();
	}
}
