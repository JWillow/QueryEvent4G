package org.homework.mcep.extractor.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.extractor.DependOnToken;
import org.homework.mcep.extractor.PostProcess;
import org.homework.mcep.extractor.regexp.RegExpExtractor;

public class RegexpExtractorBuilder implements
		GroovySupportingBuilder<RegExpExtractor> {

	private RegExpExtractor.Builder internalBuilder = RegExpExtractor.builder();

	public RegExpExtractor build() {
		RegExpExtractor extractor = internalBuilder.build();
		internalBuilder = RegExpExtractor.builder();
		return extractor;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("event")) {
				internalBuilder.produceEvent((String) entry.getValue());
			} else if (attribut.equals("exp")) {
				internalBuilder.useExpression((String) entry.getValue());
			} else if (attribut.equals("tokens")) {
				internalBuilder.extractTokens((String) entry.getValue());
			} else {
				throw new IllegalArgumentException(String.format(
						"Field [%s] unknown for EventDefinitionBuilder !",
						attribut));
			}
		}
		return this;

	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof DependOnToken) {
			internalBuilder.dependOnToken((DependOnToken) object);
		} else if (object instanceof PostProcess) {
			internalBuilder.addPostProcess((PostProcess) object);
		} else {
			throw new IllegalArgumentException("Unexpected object " + object
					+ " !");
		}
		return this;
	}

}
