package webserver;

import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class TomcatServer {

	public static final int TOMCAT_PORT = 8080;
	public TomcatServer() {
	}

	public void start() throws ServletException, LifecycleException {
		// JAX-RS (Jersey) configuration
		ResourceConfig config = new ResourceConfig();
		// Packages where Jersey looks for web service classes
		// Do not forget to add webserver package with Gson provider
		config.packages("bot","webserver");
		// Enable logging for debugging purposes
		// Comment out next line to turn off logging
		config.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, "INFO");
		// Tomcat configuration
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(TOMCAT_PORT);
		// Add web application
		Context context = tomcat.addWebapp("/api", new File("./WebContent").getAbsolutePath());
		// Declare Jersey as a servlet
		Tomcat.addServlet(context, "jersey", new ServletContainer(config));
		// Map certain URLs to Jersey
		context.addServletMappingDecoded("/rest/*", "jersey");
		// Start server
		tomcat.start();
		tomcat.getServer().await();
	}

	public static void main(String[] args) throws ServletException, LifecycleException {
		new TomcatServer().start();
	}
}
