package org.qe4g.request.evaluation

import org.qe4g.request.dsl.keyword.LinkOn;

class EventIdentitiesBuilder {
	private List<EventIdentity> eventIdentities = [];
	private EventIdentitiesBuilder() {
	}

	public static EventIdentitiesBuilder builder() {
		return new EventIdentitiesBuilder();
	}

	public void extractAttributeUsed(Evaluator evaluator) {
		evaluator.getDefinitions().each {EvaluatorDefinition evaluatorDefinition ->
		}
	}

	public Collection<EventIdentity> giveMeEventIdentities() {
		return eventIdentities;
	}
}
