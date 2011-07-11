package org.homework.mcep.extractor;

import java.util.Map;

public interface Extractor {

	String getEventName();
	
	Map<String,Object> extract(String line);
	
	boolean satisfiedDependency(Map<String,String> collectedAttributes);
	
}
