package com.vkb.alg;

import com.vkb.model.CapturedData;
import com.vkb.model.Features;

public interface FeaturesExtractor {
	public Features extract( CapturedData inputData );
}
