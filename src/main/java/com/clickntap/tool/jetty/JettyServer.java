package com.clickntap.tool.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

	private int port;

	public JettyServer(int port) {
		this.port = port;
	}

	public void start(String contextPath, String war) throws Exception {
		Server server = new Server(port);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(contextPath);
		webapp.setWar(war);
		server.setHandler(webapp);

		server.start();
		server.join();
	}
}
