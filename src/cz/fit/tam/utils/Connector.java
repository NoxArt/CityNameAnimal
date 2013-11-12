package cz.fit.tam.utils;

import java.io.IOException;
import java.util.Map;

public interface Connector {
	
	public String post(Map<String, String> parameters) throws IOException;
	
}
