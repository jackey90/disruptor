package org.jackey.emailservice;

import java.util.Map;
import java.util.TreeMap;

public class PriorityConfig {
	
	
	private static Map<Integer, Integer> config = new TreeMap<Integer, Integer>();

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
