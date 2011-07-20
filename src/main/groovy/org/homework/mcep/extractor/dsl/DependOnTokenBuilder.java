package org.homework.mcep.extractor.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.extractor.DependOnToken;

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

	public GroovySupportingBuilder withBuilder(Builder builder) {
		throw new UnsupportedOperationException();
	}

}
