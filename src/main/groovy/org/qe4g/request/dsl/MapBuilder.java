package org.qe4g.request.dsl;

import java.util.HashMap;
import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;

public class MapBuilder implements GroovySupportingBuilder<Map<String, Object>> {

	Map<String, Object> attributes;

	public Map<String, Object> build() {
		return new HashMap<String, Object>(attributes);
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new IllegalArgumentException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}

	public GroovySupportingBuilder withBuilder(String childName, Builder builder) {
		throw new IllegalArgumentException();
	}

}
