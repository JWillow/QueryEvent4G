package org.qe4g.request.dsl;

import java.util.HashMap;

import org.qe4g.dsl.builder.GroovyBuilder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.request.Request;

public class GRequestEngineBuilder extends GroovyBuilder<Request> { 

	public void init() {
		builders = new HashMap<String, GroovySupportingBuilder<?>>();
		builders.put("request", new RequestBuilder());
		builders.put("event", new SimpleEventEvaluatorBuilder());
		//builders.put("not", new NotOperatorEvaluatorBuilder());
		//builders.put("or", new OrOperatorEvaluatorBuilder());
		builders.put("engine", new EngineBuilder());
		builders.put("pattern", new PatternBuilder());
		builders.put("onPatternDetection", new SimpleListBuilder("functions"));
		builders.put("function", new FunctionBuilder());
		builders.put("count", new CountFunctionBuilder());
		builders.put("scheduledNotification", new ScheduledNotificationBuilder());
	}

}
