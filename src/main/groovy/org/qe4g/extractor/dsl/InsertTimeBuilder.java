package org.qe4g.extractor.dsl;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.extractor.postprocess.InsertTime;

/**  
 * Support below syntax to create a new instance of {@link InserTime}.
 * <pre>
 * insertTime(onToken:"date",format:"dd/MM/yy HH:mm:ss:SSS")
 * </pre>
 * 
 * @author Willow
 */
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
						"Field [%s] unknown for " + this + " !",
						attribut));
			}
		}
		return this;
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		throw new IllegalArgumentException();
	}

}
