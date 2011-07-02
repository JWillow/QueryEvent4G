package org.homework.mcep.request;

import java.util.HashMap;

import org.homework.mcep.dsl.builder.GroovyBuilder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.dsl.EngineBuilder;
import org.homework.mcep.request.dsl.EventDefinitionBuilder;
import org.homework.mcep.request.dsl.count.CountRequestBuilder;

public class GRequestEngineBuilder extends GroovyBuilder<RequestEngine> { 

	public void init() {
		builders = new HashMap<String, GroovySupportingBuilder<?>>();
		builders.put("count", new CountRequestBuilder());
		builders.put("event", new EventDefinitionBuilder());
		builders.put("engine", new EngineBuilder());
	}

}
