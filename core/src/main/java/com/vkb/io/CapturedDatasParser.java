package com.vkb.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vkb.model.CapturedData;

public class CapturedDatasParser {
	public List<CapturedData> parse( File inputFolder ) throws Exception {
		File[] traceFiles = CapturedDataFilesHelper.getTraceFiles( inputFolder );
		
		CapturedDataParser parser = new CapturedDataParser();
		List<CapturedData> inputDatas = new ArrayList<CapturedData>();
		for ( File traceFile : traceFiles ) {
			inputDatas.add( parser.parse( traceFile ) );
		}
		
		return inputDatas;
	}
}
