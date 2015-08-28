package org.diviner.jetty.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.diviner.web.WebServer;

public class JettyWebServer extends WebServer {
	public static final String OPT_DONT_JOIN = "dontjoin";

	private Server server;

	public JettyWebServer() {
	}

	public static void main(String[] args) throws Exception {
		int res = new JettyWebServer().run(args);
		if (res != 0) {
			System.exit(res);
		}

	}

	@Override
	protected int run(CommandLine cmd) throws Exception {
		server = new org.eclipse.jetty.server.Server();

		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(super.getHttpsPort());

		ServerConnector httpConnector = new ServerConnector(server,
				new HttpConnectionFactory(http_config));
		httpConnector.setPort(super.getHttpPort());

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(super.getKeyStorePath());
		sslContextFactory.setKeyStorePassword(super.getKeyStorePassword());
		sslContextFactory.setTrustStorePath(super.getTrustStorePath());
		sslContextFactory.setTrustStorePassword(super.getTrustStorePassword());
		sslContextFactory.setNeedClientAuth(super.getRequireClientCert());

		HttpConfiguration https_config = new HttpConfiguration(http_config);
		https_config.addCustomizer(new SecureRequestCustomizer());

		ServerConnector httpsConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory,
						HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(https_config));
		httpsConnector.setPort(super.getHttpsPort());

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath(this.getContextPath());
		webAppContext.setWar(this.getWebAppDir());
		webAppContext.setParentLoaderPriority(true);
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		contexts.setHandlers(new Handler[] { webAppContext });
		server.setConnectors(new Connector[] { httpConnector, httpsConnector });
		server.setHandler(contexts);
		server.start();
		if (!cmd.hasOption(OPT_DONT_JOIN)) {
			server.join();
		}
		System.out.println("diviner server started....");
		return 0;
	}

	protected Options getOptions() {
		Options options = super.getOptions();
		return options;
	}
}
