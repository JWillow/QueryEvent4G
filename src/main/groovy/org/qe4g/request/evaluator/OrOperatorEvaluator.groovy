package org.qe4g.request.evaluator
import java.util.List;
import org.qe4g.Event;
import org.qe4g.request.Evaluator;
import org.qe4g.request.Evaluator.Response;

class OrOperatorEvaluator implements Evaluator {
	List<Evaluator> evaluators = []
	String id

	public Response evaluate(Map<String,Object> context, List<Event> events) {
		boolean responseToContinue = false;
		boolean responseToKeep = false;
		for(Evaluator evaluator:evaluators) {
			Response response = evaluator.evaluate(event)
			if(response == Response.OK) {
				return Response.OK
				
			} else if (response == Response.CONTINUE_WITH_NEXT_EVALUATOR) {
				responseToContinue = true;
			} else if(response == Response.OK_BUT_KEEP_ME) {
				responseToKeep = true
			}
		}
		if(responseToContinue) {
			return Response.CONTINUE_WITH_NEXT_EVALUATOR
		}
		if(responseToKeep) {
			return Response.OK_BUT_KEEP_ME
		}
		return Response.KO
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
		private String id
		
		public Builder affectId(String id) {
			this.id = id
			return this
		}
		public Builder onEvaluator(Evaluator ev) {
			evaluators << ev
			return this
		}

		public OrOperatorEvaluator build() {
			OrOperatorEvaluator ooe = new OrOperatorEvaluator();
			ooe.evaluators = this.evaluators;
			ooe.id = this.id
			return ooe;
		}
	}
}
