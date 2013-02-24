package com.vkb.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class CapturedDataFilesHelper {
	private static final Pattern traceMatcher = Pattern.compile( "A_.*\\.json" );
	
	public static File[] getTraceFiles( File folder ) {
		return folder.listFiles( new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return traceMatcher.matcher(name).matches();
			}
			
		});
	}
	
	public static File getInterpolatedFile( File traceFile, String axisName ) {
		File parent = traceFile.getParentFile();
		String name = axisName + "_" + traceFile.getName();
		if ( parent != null ) {
			return new File( parent, name );
		}
		return new File( name );
	}
}
