package com.vkb.gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Application {
	private class Window implements Runnable {
		private Iterable<? extends Plot> plots;
		private String title;
		private Iterable<String> subtitles;

		public Window( String title, Iterable<String> subtitles, Iterable<? extends Plot> plots ) {
			this.title = title ;
			this.subtitles = subtitles;
			this.plots = plots ;
		}
	

		@Override
		public void run() {
			JFreeChart chart;
			ApplicationFrame mainFrame = new ApplicationFrame(title);
			
			GridLayout layout = new GridLayout( 0, 1 );
			mainFrame.setLayout(layout);
			
			Iterator<String> subtitleIt = subtitles.iterator();
			for( Plot plot : plots ) {
				if( !subtitleIt.hasNext() )
					chart = new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT, plot, true );
				else
					chart = new JFreeChart( subtitleIt.next(), JFreeChart.DEFAULT_TITLE_FONT, plot, true );
			
				JPanel chartPanel = new ChartPanel(chart);
				mainFrame.add( chartPanel );
			}
				
			mainFrame.pack();
		    RefineryUtilities.centerFrameOnScreen(mainFrame);
		    mainFrame.setVisible(true);
		}
	}
	
	
	private List<Thread> windowThreads = new ArrayList<Thread>();
	
	
	public void start( String title, Iterable<String> subtitles, Iterable<? extends Plot> plots ) {
		Thread newThread = new Thread( new Window(title, subtitles, plots) );
		newThread.start();
		windowThreads.add( newThread );
	}
	
	public void start( String title, Iterable<? extends Plot> plots ) {
		List<String> subtitles = Collections.emptyList();
		start( title, subtitles, plots );
	}
	
	
	public void join() throws InterruptedException {
		for( Thread th : windowThreads ) {
			th.join();
		}
	}
	
	public void run( String title, Iterable<? extends Plot> plots ) {
		List<String> subtitles = Collections.emptyList();
		run( title, subtitles, plots );
	}
	
	public void run( String title, XYPlot plot ) {
		run( title, Arrays.asList(plot) );
	}
	
	public void run( String title, Iterable<String> subtitles, Iterable<? extends Plot> plots ) {
		new Window( title, subtitles, plots ).run();
	}
	
}
