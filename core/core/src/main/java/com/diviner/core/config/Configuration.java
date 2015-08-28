package com.diviner.core.config;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

	private final ConfigurationLoader configurationLoader;
	private final DivinerResourceBundleManager DivinerResourceBundleManager;
	private Map<String, String> config = new HashMap<>();

	public Configuration(final ConfigurationLoader configurationLoader,
			final Map<?, ?> config) {
		this.configurationLoader = configurationLoader;
		this.DivinerResourceBundleManager = new DivinerResourceBundleManager();
		for (Map.Entry<?, ?> entry : config.entrySet()) {
			if (entry.getKey() != null) {
				set(entry.getKey().toString(), entry.getValue());
			}
		}
	}

	private void set(String propertyKey, Object value) {
		if (value == null) {
			config.remove(propertyKey);
		} else {
			config.put(propertyKey, value.toString());
		}
	}

}
