package com.vkb.math;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

public class Statistics {
	public static StatisticalSummary resume( StatisticalSummary other ) {
		return new StatisticalSummaryValues( other.getMean(), other.getVariance(), other.getN(), other.getMax(),
				other.getMin(), other.getSum() );
	}
}
