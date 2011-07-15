package org.homework.mcep.extractor;

import java.util.Map;

/**
 * 
 * @author Willow
 *
 */
public interface PostProcess {
	Map<String,Object> process(Map<String,Object> tokens);
}
