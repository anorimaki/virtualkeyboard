package com.vkb.math;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public class MinMaxNormalizationMethod implements NormalizationMethod {
	public static final MinMaxNormalizationMethod INSTANCE = new MinMaxNormalizationMethod();

	private MinMaxNormalizationMethod() {}
	
	@Override
	public double normalize( StatisticalSummary statisticalSummary, double value ) {
		return (value - statisticalSummary.getMin()) / 
					(statisticalSummary.getMax() - statisticalSummary.getMin());
	}

}
