package org.homework.mcep.extractor.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.extractor.Extractor;
import org.homework.mcep.extractor.ParserEngine;

public class ParserEngineBuilder implements
		GroovySupportingBuilder<ParserEngine> {

	private ParserEngine.Builder internalBuilder = ParserEngine.builder();

	public ParserEngine build() {
		ParserEngine extractor = internalBuilder.build();
		internalBuilder = ParserEngine.builder();
		return extractor;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof Extractor) {
			internalBuilder.withExtractor((Extractor) object);
		} else {
			throw new IllegalArgumentException("Unexpected object " + object
					+ " !");
		}
		return this;
	}

}
