package com.vkb.alg;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.vkb.io.CapturedDatasParser;
import com.vkb.model.CapturedData;


public class SignatureValidatorTest {
	private static final File INPUT_FOLDER1 = new File( "src/resources/jig" );
	private static final File INPUT_FOLDER2 = new File( "src/resources/sara" );
	
	@Test
	void testValidate() throws Exception {
		CapturedDatasParser parser = new CapturedDatasParser();
		List<CapturedData> datas1 = parser.parse( INPUT_FOLDER1 ) ;
		List<CapturedData> datas2 = parser.parse( INPUT_FOLDER2 ) ;
		
		GenericSignatureValidator validationAlgorithm = new GenericSignatureValidator( datas1 );
		assertTrue( validationAlgorithm.check(datas1.get(0)) );
		assertFalse( validationAlgorithm.check(datas2.get(0)) );
	}
}
