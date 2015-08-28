package com.diviner.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.diviner.core.config.Configuration;
import com.diviner.core.config.ConfigurationLoader;
import com.diviner.core.exception.DivinerException;

public class ApplicationBootstrap implements ServletContextListener {

	public static final String APP_CONFIG_LOADER = "application.config.loader";

	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext context = sce.getServletContext();
		System.out.println("Servlet context initialized...");
		if (context != null) {
			final Configuration config = ConfigurationLoader.load(
					context.getInitParameter(APP_CONFIG_LOADER),
					getInitParametersAsMap(context));
			 setupInjector(context, config);

		} else {
			throw new DivinerException(
					"Failed to initialize context. Diviner is not running.");
		}
	}

	private void setupInjector(ServletContext context, Configuration config) {
		// TODO Auto-generated method stub
		
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		System.out.println("BEGIN: Servlet context destroyed...");

	}

	private Map<String, String> getInitParametersAsMap(ServletContext context) {
		Map<String, String> initParameters = new HashMap<>();
		Enumeration<String> e = context.getInitParameterNames();
		while (e.hasMoreElements()) {
			String initParameterName = e.nextElement();
			initParameters.put(initParameterName,
					context.getInitParameter(initParameterName));
		}
		return initParameters;
	}
}
