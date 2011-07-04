package org.homework.mcep.request.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Request;

public class SimpleListBuilder implements GroovySupportingBuilder<List<Object>> {

	private String type;
	private List<Object> list = new ArrayList<Object>();
	
	public SimpleListBuilder(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public List<Object> build() {
		return list;
	}

	public GroovySupportingBuilder<Request> withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder<Request> withAttributes(
			Map<String, Object> attributes) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder<List<Object>> withBuilder(Builder builder) {
		list.add(builder.build());
		return this;
	}

}