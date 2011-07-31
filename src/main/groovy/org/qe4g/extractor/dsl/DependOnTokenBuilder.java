package org.qe4g.extractor.dsl;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.extractor.DependOnToken;

/**
 * Support below syntax to create a new instance of {@link DependOnToken}.
 * <pre>
 * dependOnToken(id:'categorie',value:'Alert')
 * </pre>
 * 
 * @author Willow
 */
public class DependOnTokenBuilder implements
		GroovySupportingBuilder<DependOnToken> {

	private DependOnToken.Builder internalBuilder = DependOnToken.builder();

	public DependOnToken build() {
		DependOnToken dependOnToken = internalBuilder.build();
		internalBuilder = DependOnToken.builder();
		return dependOnToken;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("id")) {
				internalBuilder.identifiedBy((String) entry.getValue());
			} else if (attribut.equals("value")) {
				internalBuilder.matchValue((String) entry.getValue());
			} else {
				throw new IllegalArgumentException(String.format(
						"Field [%s] unknown for EventDefinitionBuilder !",
						attribut));
			}
		}
		return this;

	}

	public GroovySupportingBuilder withBuilder(String childName, Builder builder) {
		throw new UnsupportedOperationException();
	}

}
