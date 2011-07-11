package org.homework.mcep.extractor.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.extractor.postprocess.InsertTime;

public class InsertTimeBuilder implements
		GroovySupportingBuilder<InsertTime> {

	private InsertTime.Builder internalBuilder = InsertTime.builder();

	public InsertTime build() {
		InsertTime dpp = internalBuilder.build();
		internalBuilder = InsertTime.builder();
		return dpp;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new IllegalArgumentException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("format")) {
				internalBuilder.withFormat((String) entry.getValue());
			} else if (attribut.equals("onToken")) {
				internalBuilder.onToken((String) entry.getValue());
			} else {
				throw new IllegalArgumentException(String.format(
						"Field [%s] unknown for EventDefinitionBuilder !",
						attribut));
			}
		}
		return this;
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		throw new IllegalArgumentException();
	}

}
