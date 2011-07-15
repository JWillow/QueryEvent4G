package org.homework.mcep.request.evaluator

import java.util.List;

import org.homework.mcep.Event;
import org.homework.mcep.request.Evaluator;
import org.homework.mcep.request.evaluator.OrOperatorEvaluator.Builder;

class NotOperatorEvaluator implements Evaluator {

	Evaluator evaluator;
	
	public boolean evaluate(List<Event> events) {
		return !evaluator.evaluate(events);
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
		
		public Builder onEvaluator(Evaluator ev) {
			evaluator =  ev
			return this
		}
		
		public NotOperatorEvaluator build() {
			NotOperatorEvaluator noe = new NotOperatorEvaluator();
			noe.evaluator = this.evaluator;
			return noe;
		}
	}

	
}
