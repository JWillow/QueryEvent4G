package org.homework.mcep.request

import groovy.lang.Closure;

import java.text.SimpleDateFormat

class RequestDefinition {

	List<EventListener> eventListeners;
	
	String description

	/**
	 * Closure permettant de d�finir comment la date est obtenue, par d�faut on retourne la date syst�me sous la forme de millisecond.
	 * En param�tre on retrouve un "event"
	 */
	def date = {System.currentTimeMillis()}

	List<EventDefinition> eventDefinitions

	List<Function> functions
	
	/**
	 * <p>Closure d�finissant la strat�gie d'acceptation des evenements publi�s. Si un �v�nement n'est pas accept� il n'influencera pas l'algorithme de la requ�te
	 * <p>Par d�faut on accepte de traiter tous les �v�nements
	 */
	def accept = {true}

	/**
	 * <p>Closure permettant de d�finir l'action capable d'extraire depuis un "event" un identifiant permettant de r�aliser des regroupements sur les �v�nements.
	 * <p>Par d�faut on ne recherche pas � effectuer des regroupements.
	 */
	def groupBy = {"groupBy"}

	private RequestDefinition() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Closure groupBy = null;
		private Closure accept = null;
		private Closure date = null;
		private List<EventListener> eventListeners = []
		private List<Function> functions = [];
		private List<EventDefinition> eventDefinitions = [];
		private String description

		public Builder withDescription(String description) {
			this.description = description
			return this
		}

		public Builder withNotification(Closure closure) {
			notification = closure;
			return this;
		}

		public Builder withGroupBy(Closure closure) {
			groupBy = closure;
			return this;
		}
		
		public Builder addEventListener(EventListener eventListener) {
			eventListeners << eventListener
			return this
		}
		
		public Builder addFunction(Function function) {
			functions << function
			return this
		}
		public Builder addEventDefinition(EventDefinition eventDefinition) {
			eventDefinitions << eventDefinition;
			return this;
		}

		public Builder withDate(Closure date) {
			this.date = date;
			return this;
		}
		public Builder withAccept(Closure accept) {
			this.accept = accept
			return this;
		}
		public RequestDefinition build() {
			RequestDefinition request = new RequestDefinition();
			if(groupBy != null) {
				request.groupBy = this.groupBy;
			}
			if(accept != null) {
				request.accept = this.accept;
			}
			request.eventListeners = this.eventListeners
			request.description = this.description
			request.eventDefinitions = eventDefinitions;
			request.functions = this.functions
			return request;
		}
	}
}
