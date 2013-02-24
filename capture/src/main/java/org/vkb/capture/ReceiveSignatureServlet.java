package org.vkb.capture;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
 

@SuppressWarnings("serial")
@WebServlet("/append")
public class ReceiveSignatureServlet extends HttpServlet {
	private File outputFolder;
	
	public ReceiveSignatureServlet() {
		outputFolder = new File( "data" );
		if ( !outputFolder.exists() )
			outputFolder.mkdirs();
	}
	
	@Override
	public void doPost( HttpServletRequest req, HttpServletResponse res )
							throws ServletException, IOException
	{
		Map<String,String[]> parametersMap = req.getParameterMap();
		Set<Map.Entry<String,String[]> > parameters = parametersMap.entrySet();
		
		Iterator<Map.Entry<String,String[]> > parametersIt = parameters.iterator();
		if ( !parametersIt.hasNext() ) {
			return;
		}
		String data = parametersIt.next().getKey();
		
		String outputFileName = buildFileName( req.getRemoteAddr() );
		File outputFile = new File( outputFolder, outputFileName );
		
		PrintWriter outWriter = new PrintWriter(outputFile);
		outWriter.append( data );
		outWriter.close();
	}

	private String buildFileName(String remoteAddr) {
		remoteAddr = remoteAddr.replace(':', '.');
		
		return "A_" +  remoteAddr + "_" + new Date().getTime() + ".json";
	}
}