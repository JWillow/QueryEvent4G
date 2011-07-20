package org.homework.mcep.extractor;

import java.util.Map;

import org.homework.mcep.extractor.postprocess.InsertTime;

/**
 * Used to include, calculate new token in addition to {@link Extractor}
 * process, see {@link InsertTime} as example.
 * 
 * @author Willow
 * 
 */
public interface PostProcess {

	/**
	 * @param tokens
	 * @return a new {@link Map} of tokens who include tokens provide in parameter and
	 *         additionnal/modified token depending of the implementation.
	 */
	Map<String, Object> process(Map<String, Object> tokens);
}
