package org.qe4g.request.dsl;

import java.util.List;
import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.request.EventProcessedListener;
import org.qe4g.request.Function;
import org.qe4g.request.Functions;
import org.qe4g.request.Pattern;
import org.qe4g.request.Request;

public class RequestBuilder implements GroovySupportingBuilder<Request> {

	Request.Builder internalBuilder = Request.builder();

	public Request build() {
		Request request = internalBuilder.build();
		internalBuilder = Request.builder();
		return request;
	}

	public GroovySupportingBuilder<Request> withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder<Request> withAttributes(
			Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("description")) {
				internalBuilder.withDescription((String) entry.getValue());
			} else {
				throw new IllegalArgumentException("Attribut " + attribut
						+ " is not expected !");
			}
		}
		return this;
	}

	public GroovySupportingBuilder<Request> withBuilder(String childName, Builder builder) {
		Object object = builder.build();
		if (object instanceof Function) {
			internalBuilder.addFunction((Function) object);
		}
		if (object instanceof Functions) {
			setFunctions(((Functions) object).getFunctions());
		}
		if(object instanceof Pattern) {
			internalBuilder.workAroundPattern((Pattern) object);
		}
		if (object instanceof EventProcessedListener) {
			internalBuilder.addEventListener((EventProcessedListener) object);
		}
		if (builder instanceof SimpleListBuilder) {
			SimpleListBuilder slb = (SimpleListBuilder) builder;
			if (slb.getType().equals("functions")) {
				setFunctions((List<Function>) object);
			}
		}
		return this;
	}

	private void setFunctions(List<Function> list) {
		for (Object object : list) {
			internalBuilder.addFunction((Function) object);
		}
	}
}
