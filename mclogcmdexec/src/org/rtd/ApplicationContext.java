package org.rtd;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
	private static Map<String, Object> DATA = new HashMap<String, Object>();
	
	public static <T> T getValue(String key) {
		@SuppressWarnings("unchecked")
		T t = (T)DATA.get(key);
		return t;
	}
	
	public static void setValue(String key, Object value) {
		DATA.put(key, value);
	}

	public static void clearValue(String key) {
		DATA.remove( key );
	}
}
