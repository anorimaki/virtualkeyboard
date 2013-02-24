package com.vkb.alg;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.vkb.RawTrace;
import com.vkb.alg.FeatureMeansAlgorithm;
import com.vkb.io.TracesParser;


public class FeatureMeansAlgorithmTest {
	private static final File INPUT_FOLDER1 = new File( "src/resources/jig" );
	private static final File INPUT_FOLDER2 = new File( "src/resources/sara" );
	
	@Test
	void testValidate() throws Exception {
		TracesParser parser = new TracesParser();
		List<RawTrace> traces1 = parser.parse( INPUT_FOLDER1 ) ;
		List<RawTrace> traces2 = parser.parse( INPUT_FOLDER2 ) ;
		
		FeatureMeansAlgorithm validationAlgorithm = new FeatureMeansAlgorithm( traces1, 80.0d );
		assertTrue( validationAlgorithm.check(traces1.get(0)) );
		assertFalse( validationAlgorithm.check(traces2.get(0)) );
	}
}
