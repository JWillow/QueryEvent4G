package org.homework.mcep.request.evaluator

import java.util.List

import org.homework.mcep.Event
import org.homework.mcep.request.Evaluator
import org.homework.mcep.request.Evaluator.Response

class NotOperatorEvaluator implements Evaluator {

	Evaluator evaluator;

	String id;

	public Response evaluate(Map<String,Object> context, List<Event> events) {
		Response response = evaluator.evaluate(events);
		if(response == Response.OK) {
			return Response.KO
		} else if(response == Response.KO) {
			return Response.OK
		} else {
			throw new IllegalStateException("We don't handle Response.CONTINUE_TO_NEXT_EVALUATOR")
		}
	}

	// ------------
	// Builder part
	// ------------
	private NotOperatorEvaluator() {}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Evaluator evaluator = null;
		private String id = null;

		public Builder onEvaluator(Evaluator ev) {
			evaluator =  ev
			return this
		}

		public NotOperatorEvaluator build() {
			NotOperatorEvaluator noe = new NotOperatorEvaluator();
			noe.evaluator = this.evaluator;
			noe.id = this.id
			return noe;
		}

		public Builder affectId(String id) {
			this.id = id
			return this
		}
	}


}
