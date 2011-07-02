package org.homework.mcep.dsl.builder;

import java.util.Map;

public interface GroovySupportingBuilder<T> extends Builder<T> {

	GroovySupportingBuilder withData(Object value);

	GroovySupportingBuilder withAttributes(Map<String,Object> attributes);

	GroovySupportingBuilder withBuilder(Builder builder);

}
