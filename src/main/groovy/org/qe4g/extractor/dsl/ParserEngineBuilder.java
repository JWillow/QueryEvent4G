package org.qe4g.extractor.dsl;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.extractor.DependOnToken;
import org.qe4g.extractor.Extractor;
import org.qe4g.extractor.ParserEngine;

/**
 * Support below syntax to create a new instance of {@link ParserEngine}.
 * <pre>
 * ParserEngine parserEngine = gParserEngineBuilder.engine {
 * }
 * </pre>
 * 
 * @author Willow
 */
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

	public GroovySupportingBuilder withBuilder(String childName, Builder builder) {
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
