package com.vkb.app.quality.err;

import com.vkb.app.model.User;

public interface ThresholdedSignatureValidatorFactory {
	public ThresholdedSignatureValidator generateSignatureValidator( User user );
}
