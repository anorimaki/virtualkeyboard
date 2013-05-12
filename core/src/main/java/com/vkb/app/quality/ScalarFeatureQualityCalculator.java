package com.vkb.app.quality;

import com.vkb.model.FeatureId;

public interface ScalarFeatureQualityCalculator {
	public double calculate(FeatureId id);
	public void add( String user, ScalarFeaturesStatistics us );
}

