package com.vkb.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Application extends ApplicationFrame {
	private static final long serialVersionUID = 1L;


	public Application(String title) {
		super(title);
	}
	
		
	public void run( XYPlot plot ) {
		JFreeChart chart =
				new JFreeChart( "demo", JFreeChart.DEFAULT_TITLE_FONT,
								plot, true );
		
		JPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	    setContentPane(chartPanel);
	    
	    pack();
	    RefineryUtilities.centerFrameOnScreen(this);
	    setVisible(true);
	}
	
	
	public void run( XYPlot... plots ) {
		GridLayout layout = new GridLayout(0,1);
		setLayout(layout);
		
		for( XYPlot plot : plots ) {
			JFreeChart chart =
					new JFreeChart( "demo", JFreeChart.DEFAULT_TITLE_FONT,
									plot, true );
		
			JPanel chartPanel = new ChartPanel(chart);
			add( chartPanel );
		}
			
		pack();
	    RefineryUtilities.centerFrameOnScreen(this);
	    setVisible(true);
	}
}
