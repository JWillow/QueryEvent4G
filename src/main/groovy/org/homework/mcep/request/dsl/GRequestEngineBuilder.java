package org.homework.mcep.request.dsl;

import java.util.HashMap;

import org.homework.mcep.dsl.builder.GroovyBuilder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Request;

public class GRequestEngineBuilder extends GroovyBuilder<Request> { 

	public void init() {
		builders = new HashMap<String, GroovySupportingBuilder<?>>();
		builders.put("request", new RequestDefinitionBuilder());
		builders.put("event", new EventDefinitionBuilder());
		builders.put("engine", new EngineBuilder());
		builders.put("pattern", new SimpleListBuilder("pattern"));
		builders.put("functions", new SimpleListBuilder("functions"));
		builders.put("function", new FunctionBuilder());
		builders.put("scheduledNotification", new ScheduledNotificationBuilder());
	}

}
