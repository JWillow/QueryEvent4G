package org.homework.mcep.extractor;

import java.util.Map;

public interface PostProcess {
	Map<String,Object> process(Map<String,Object> tokens);
}
