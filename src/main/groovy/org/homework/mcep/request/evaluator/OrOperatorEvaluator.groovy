package org.homework.mcep.request.evaluator
import java.util.List;
import org.homework.mcep.Event;
import org.homework.mcep.request.Evaluator;

class OrOperatorEvaluator implements Evaluator {
	List<Evaluator> evaluators = []

	boolean evaluate(List<Event> events) {
		return evaluators.any {Evaluator ev ->
			ev.evaluate(events)
		}
	}
	
	// ------------
	// Builder part
	// ------------
	private OrOperatorEvaluator() {}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private List<Evaluator> evaluators = [];
		
		public Builder onEvaluator(Evaluator ev) {
			evaluators << ev
			return this
		}
		
		public OrOperatorEvaluator build() {
			OrOperatorEvaluator ooe = new OrOperatorEvaluator();
			ooe.evaluators = this.evaluators;
			return ooe;
		}	
	}	
}
