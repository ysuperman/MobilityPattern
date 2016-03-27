package Config;

import java.util.HashMap;
import java.util.Map;

public class Config {
	public static final String CONFIG_DATE = "date";
	private static final String[] configKeys = {
		CONFIG_DATE
	};
	private static Map<String, String> configs = new HashMap<String, String>();
}
