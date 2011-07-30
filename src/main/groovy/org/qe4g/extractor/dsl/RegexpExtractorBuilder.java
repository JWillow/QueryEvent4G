package org.qe4g.extractor.dsl;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.extractor.DependOnToken;
import org.qe4g.extractor.PostProcess;
import org.qe4g.extractor.regexp.RegExpExtractor;

/**
 * Support below syntax to create a new instance of {@link RegExpExtractor}.
 * 
 * <pre>
 * regExpExtractor(event:'RessourceEvent',exp:"(ACDAddr\\w+).*?agentId=(\\d+).*?ACD : (\\d+)",tokens:"state,acdId,skill") {
 * }
 * </pre>
 * 
 * @author Willow
 */
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
