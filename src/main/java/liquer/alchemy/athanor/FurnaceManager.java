package liquer.alchemy.athanor;

import liquer.alchemy.json.Json;

import java.io.IOException;

public class FurnaceManager {
	
	private static final String FURNACE_CONFIG_SYSTEM_PROPERTY = "furnace";
	private static final String FURNACE_CONFIG_FILE = "/furnace.json";
	private static class SingletonHelper {
		private static final FurnaceManager INSTANCE = new FurnaceManager();
	}
	public static FurnaceManager getInstance() {
		return SingletonHelper.INSTANCE;
	}
	private FurnaceConfig config;
	private FurnaceManager() {
	}
	public FurnaceConfig getConfig() {
		if (config == null) {
			String s = System.getProperty(FURNACE_CONFIG_SYSTEM_PROPERTY, FURNACE_CONFIG_FILE);
			FurnaceConfig ret = new FurnaceConfig();
			try {
				config = Json.assign(ret, FurnaceConfig.class.getResourceAsStream(s));
			} catch (IOException e) {
				config = ret;
			}
		}
		return config;
	}

	public FurnaceConfig getConfig(String configFile) {
		if (config == null) {
			System.setProperty(FURNACE_CONFIG_SYSTEM_PROPERTY, configFile);
		}
		return getConfig();
	}

	public FurnaceConfig reloadConfig() {
		return reloadConfig(FURNACE_CONFIG_FILE);
	}

	public FurnaceConfig reloadConfig(String configFile)  {
		System.setProperty(FURNACE_CONFIG_SYSTEM_PROPERTY, configFile);
		config = null;
		return getConfig();
	}
}
