package com.vkb.gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Application {
	private class Window implements Runnable {
		private Iterable plots;
		private String title;
		private String[] subtitles;

		public Window( String title, String[] subtitles, Iterable plots ) {
			this.title = title ;
			this.subtitles = subtitles;
			this.plots = plots ;
		}
		
	

		@Override
		public void run() {
			int i;
			JFreeChart chart;
			ApplicationFrame mainFrame = new ApplicationFrame(title);
			
			GridLayout layout = new GridLayout(0,1);
			mainFrame.setLayout(layout);
			
			i=0;
			for( Plot plot : (Iterable<Plot>)plots ) {
				if(subtitles.length<=0)
					chart = new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT,plot, true );
				else
					chart = new JFreeChart( subtitles[i], JFreeChart.DEFAULT_TITLE_FONT,plot, true );
			
				JPanel chartPanel = new ChartPanel(chart);
				mainFrame.add( chartPanel );
				i++;
			}
				
			mainFrame.pack();
		    RefineryUtilities.centerFrameOnScreen(mainFrame);
		    mainFrame.setVisible(true);
		}
	}
	
	
	private List<Thread> windowThreads = new ArrayList<Thread>();
	
	
	public void start( String title, String[] subtitles, Iterable plots ) {
		Thread newThread = new Thread( new Window(title, subtitles, plots) );
		newThread.start();
		windowThreads.add( newThread );
	}
	
	
	public void join() throws InterruptedException {
		for( Thread th : windowThreads ) {
			th.join();
		}
	}
	
	public void run( String title, Iterable<XYPlot> plots ) {
		String[] strV={};
		new Window( title, strV, plots ).run();
	}
	
	public void run( String title, XYPlot plot ) {
		run( title, Arrays.asList(plot) );
	}
	
	public void run( String title, Iterable<PiePlot> plots, String[] subtitles ) {
		new Window( title, subtitles, plots ).run();
	}
		
}
