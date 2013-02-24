package org.vkb.capture;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class Launcher {
	private static final String webappRoot = "src/main/webapp";
	
	public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
 
        WebAppContext context = new WebAppContext();
        context.setDescriptor( webappRoot+"/WEB-INF/web.xml" );
        context.setResourceBase( webappRoot );
        context.setContextPath( "/" );
        context.setParentLoaderPriority(true);
        
        context.addServlet(new ServletHolder(new ReceiveSignatureServlet()),"/append");
 
        server.setHandler(context);
 
        server.start();
        server.join();
    }
}
