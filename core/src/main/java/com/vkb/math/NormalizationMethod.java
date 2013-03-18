package com.vkb.math;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public interface NormalizationMethod {

	public double normalize( StatisticalSummary statisticalSummary, double value );

}
