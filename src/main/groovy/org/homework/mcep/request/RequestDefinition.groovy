package org.homework.mcep.request

import groovy.lang.Closure;

import java.text.SimpleDateFormat

class RequestDefinition {

	List<EventListener> eventListeners;
	
	String description

	/**
	 * Closure permettant de définir comment la date est obtenue, par défaut on retourne la date système sous la forme de millisecond.
	 * En paramètre on retrouve un "event"
	 */
	def date = {System.currentTimeMillis()}

	List<EventDefinition> eventDefinitions

	List<Function> functions
	
	/**
	 * <p>Closure définissant la stratégie d'acceptation des evenements publiés. Si un événement n'est pas accepté il n'influencera pas l'algorithme de la requête
	 * <p>Par défaut on accepte de traiter tous les événements
	 */
	def accept = {true}

	/**
	 * <p>Closure permettant de définir l'action capable d'extraire depuis un "event" un identifiant permettant de réaliser des regroupements sur les événements.
	 * <p>Par défaut on ne recherche pas à effectuer des regroupements.
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
