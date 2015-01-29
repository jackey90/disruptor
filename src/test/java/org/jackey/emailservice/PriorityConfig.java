package org.jackey.emailservice;

import java.util.HashMap;
import java.util.Map;

public class PriorityConfig {
	
	
	private static Map<Integer, Integer> config = new HashMap<Integer, Integer>();

	public static Map<Integer, Integer> getConfig() {
		return config;
	}

	private static PriorityConfig instance = new PriorityConfig();
	
	private PriorityConfig(){
		config.put(1, 2 << 11);
		config.put(2, 2 << 13);
		config.put(3, 2 << 15);
	}
	
	public static PriorityConfig getInstance(){
		return instance;
	}
}
