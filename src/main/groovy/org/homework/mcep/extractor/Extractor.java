package org.homework.mcep.extractor;

import java.util.Map;

public interface Extractor {

	String getEventName();
	
	Map<String,String> extract(String line);
	
	boolean satisfiedDependency(Map<String,String> collectedAttributes);
	
}
