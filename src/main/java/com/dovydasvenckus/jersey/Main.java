package com.dovydasvenckus.jersey;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        try {
            // Create Jersey application configuration
            ResourceConfig resourceConfig = new ResourceConfig()
                    .packages("com.dovydasvenckus.jersey.resources") // Automatically scan for resources
                    .register(JacksonJaxbJsonProvider.class); // Register Jackson Feature for JSON support

            // Set up Jetty server
            Server server = new Server(8080);

            // Create the Servlet handler with Jersey
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/*");

            server.setHandler(context);

            // Start server
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
