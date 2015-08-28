package com.diviner.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.diviner.core.exception.DivinerException;
import com.diviner.core.util.ProcessUtil;
import com.google.common.collect.ImmutableList;

public class FileConfigurationLoader extends ConfigurationLoader {

	public static final String ENV_LUMIFY_DIR = "LUMIFY_DIR";
	public static final String DEFAULT_WINDOWS_LOCATION = "c:/opt/lumify/";
	public static final String DEFAULT_UNIX_LOCATION = "/opt/lumify/";
	public FileConfigurationLoader(Map initParameters) {
		super(initParameters);
	}

	@Override
	public Configuration createConfiguration() {
		// TODO Auto-generated method stub
		final Map<String, String> properties = new HashMap<>();
		List<File> configDirectories = getDivinerDirectoriesFromLeastPriority("config");
		if (configDirectories.size() == 0) {
			throw new DivinerException(
					"Could not find any valid config directories.");
		}
		for (File directory : configDirectories) {
			Map<String, String> directoryProperties = loadDirectory(directory);
			properties.putAll(directoryProperties);
		}
		return new Configuration(this, properties);
	}

	private static Map<String, String> loadDirectory(File configDirectory) {
		// TODO Auto-generated method stub
		if (!configDirectory.exists()) {
			throw new DivinerException("Could not find config directory: "
					+ configDirectory);
		}
		File[] files = configDirectory.listFiles();
		if (files == null) {
			throw new DivinerException("Could not parse directory name: "
					+ configDirectory);
		}
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		Map<String, String> properties = new HashMap<>();
		for (File f : files) {
			if (!f.getAbsolutePath().endsWith(".properties")) {
				continue;
			}
			try {
				Map<String, String> fileProperties = loadFile(f
						.getAbsolutePath());
				for (Map.Entry<String, String> filePropertyEntry : fileProperties
						.entrySet()) {
					properties.put(filePropertyEntry.getKey(),
							filePropertyEntry.getValue());
				}
			} catch (IOException ex) {
				throw new DivinerException("Could not load config file: "
						+ f.getAbsolutePath(), ex);
			}
		}
		return properties;
	}

	private static Map<String, String> loadFile(final String fileName) throws IOException{
		// TODO Auto-generated method stub
		Map<String, String> results = new HashMap<>();
		try (FileInputStream in = new FileInputStream(fileName)) {
			Properties properties = new Properties();
			properties.load(in);
			for (Map.Entry<Object, Object> prop : properties.entrySet()) {
				String key = prop.getKey().toString();
				String value = prop.getValue().toString();
				results.put(key, value);
			}
		}catch (Exception e) {
			System.out.println("Could not load configuration file:"+fileName);
		}
		return results;
	}

	public static List<File> getDivinerDirectoriesFromLeastPriority(
			String subDirectory) {
		List<File> results = new ArrayList<>();
		 if (ProcessUtil.isWindows()) {
			 addDivinerSubDirectory(results, DEFAULT_WINDOWS_LOCATION,
						subDirectory);
		 } else {
			 addDivinerSubDirectory(results, DEFAULT_UNIX_LOCATION, subDirectory);
			}
		 String appData = System.getProperty("appdata");
		 if (appData != null && appData.length() > 0) {
			 addDivinerSubDirectory(results,
						new File(new File(appData), "Lumify").getAbsolutePath(),
						subDirectory);
		 }
		 String userHome = System.getProperty("user.home");
		 if (userHome != null && userHome.length() > 0) {
			 addDivinerSubDirectory(results, new File(new File(userHome),
						".lumify").getAbsolutePath(), subDirectory);
		 }
		 addDivinerSubDirectory(results, System.getenv(ENV_LUMIFY_DIR),
					subDirectory);

		 return ImmutableList.copyOf(results);
	}

	private static void addDivinerSubDirectory(List<File> results,
			String location, String subDirectory) {
		// TODO Auto-generated method stub
		if (location == null || location.trim().length() == 0) {
			return;
		}
		location = location.trim();
		if (location.startsWith("file://")) {
			location = location.substring("file://".length());
		}
		File dir = new File(new File(location), subDirectory);
		if (!dir.exists()) {
			return;
		}
		results.add(dir);
	}
}
