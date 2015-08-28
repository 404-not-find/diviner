package com.diviner.core.config;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.diviner.core.exception.DivinerException;

public abstract class ConfigurationLoader {
	public static final String ENV_CONFIGURATION_LOADER = "LUMIFY_CONFIGURATION_LOADER";
	private final Map initParameters;
	protected static ConfigurationLoader configurationLoader;

	protected ConfigurationLoader(Map initParameters) {
		this.initParameters = initParameters;
	}

	public static Configuration load(String configLoaderName,
			Map<String, String> initParameters) {
		Class configLoader;
		if (configLoaderName == null) {
			configLoader = getConfigurationLoaderClass();
		} else {
			configLoader = getConfigurationLoaderByName(configLoaderName);
		}
		return load(configLoader, initParameters);
	}

	public static Configuration load(Class configLoader,
			Map<String, String> initParameters) {
		ConfigurationLoader configurationLoader = getOrCreateConfigurationLoader(
				configLoader, initParameters);
		return configurationLoader.createConfiguration();
	}

	public abstract Configuration createConfiguration();

	private static ConfigurationLoader getOrCreateConfigurationLoader(
			Class configLoaderClass, Map<String, String> initParameters) {
		if (configurationLoader != null) {
			return configurationLoader;
		}
		if (configLoaderClass == null) {
			configLoaderClass = getConfigurationLoaderClass();
		}
		if (initParameters == null) {
			initParameters = new HashMap<String, String>();
		}
		try {
			Constructor constructor = configLoaderClass
					.getConstructor(Map.class);
			configurationLoader = (ConfigurationLoader) constructor
					.newInstance(initParameters);
		} catch (Exception e) {
			throw new DivinerException("Could not load configuration class: "
					+ configLoaderClass.getName(), e);
		}
		return configurationLoader;
	}

	public static Class getConfigurationLoaderClass() {
		String configLoaderName = System.getenv(ENV_CONFIGURATION_LOADER);
		if (configLoaderName == null) {
			configLoaderName = System.getProperty(ENV_CONFIGURATION_LOADER);
		}
		if (configLoaderName != null) {
			return getConfigurationLoaderByName(configLoaderName);
		}
		return FileConfigurationLoader.class;
	}

	public static Class getConfigurationLoaderByName(String configLoaderName) {
		Class configLoader;
		try {
			configLoader = Class.forName(configLoaderName);
		} catch (ClassNotFoundException e) {
			throw new DivinerException("Could not load class "
					+ configLoaderName, e);
		}
		return configLoader;
	}

}
