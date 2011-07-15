package org.homework.mcep.dsl.builder;

import java.util.Map;

/**
 * Must be implements to register builder to {@link GroovyBuilder}
 * @author Willow
 *
 * @param <T>
 */
public interface GroovySupportingBuilder<T> extends Builder<T> {

	/**
	 * Call when in Groovy builder syntax you have : 
	 * <pre>
	 * 	title("data") {
	 * 	}
	 * </pre>
	 * @param value
	 * @return
	 */
	GroovySupportingBuilder withData(Object value);

	/**
	 * Call when in Groovy builder syntax you have :
	 * <pre>
	 * title(display:'myApp',font:'Arial') {
	 * }
	 * </pre>
	 * @param attributes
	 * @return
	 */
	GroovySupportingBuilder withAttributes(Map<String,Object> attributes);

	/**
	 * Call when in Groovy builder syntax you have :
	 * <pre>
	 * frame() {
	 * 	title() {
	 * 	}
	 * }
	 * </pre>
	 * @param builder
	 * @return
	 */
	GroovySupportingBuilder withBuilder(Builder builder);

}
