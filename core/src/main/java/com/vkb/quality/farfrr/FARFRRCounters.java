package com.vkb.quality.farfrr;

class FARFRRCounters {
	int passedOwnSignatures = 0;
	int totalOwnSignatures = 0;
	int passedOtherSignatures = 0;
	int totalOtherSignatures = 0;

	public void addFRR(int passed, int total) {
		passedOwnSignatures += passed;
		totalOwnSignatures += total;
	}

	public void addFAR(int passed, int total) {
		passedOtherSignatures += passed;
		totalOtherSignatures += total;
	}
	
	public double getFAR() {
		return (double)passedOtherSignatures/(double)totalOtherSignatures;
	}
	
	public double getFRR() {
		return (double)(totalOwnSignatures-passedOwnSignatures)/(double)totalOwnSignatures;
	}
}
