package org.homework.mcep.request.count

import groovy.lang.Closure;

import org.homework.mcep.request.EventDefinition;
import java.text.SimpleDateFormat

class CountRequest {

	String description

	/**
	 * D�finition de l'interval de temps autre chacune des notifications en milliseconde, par d�faut positionn� � 1000 ms, soit 1 seconde
	 */
	def timeNotificationInterval = 1000l

	/**
	 * Closure permettant de d�finir comment la date est obtenue, par d�faut on retourne la date syst�me sous la forme de millisecond.
	 * En param�tre on retrouve un "event"
	 */
	def date = {System.currentTimeMillis()}

	List<EventDefinition> eventDefinitions

	/**
	 * <p>Closure d�finissant la strat�gie d'acceptation des evenements publi�s. Si un �v�nement n'est pas accept� il n'influencera pas l'algorithme de la requ�te
	 * <p>Par d�faut on accepte de traiter tous les �v�nements
	 */
	def accept = {true}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss")
	/**
	 * <p>Closure d�finissant l'action � r�aliser en cas de notification d� au fait que l'interval de temps a �t� atteint. 
	 * <p>Par d�faut on �crit la notification dans la sortie standard
	 */
	def notification = {long time,int count ->
		def date = sdf.format(new Date(time)) 
		println "$date : $count"
	}

	/**
	 * <p>Closure permettant de d�finir l'action capable d'extraire depuis un "event" un identifiant permettant de r�aliser des regroupements sur les �v�nements.
	 * <p>Par d�faut on ne recherche pas � effectuer des regroupements.
	 */
	def groupBy = {"groupBy"}

	private CountRequest() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Closure notification = null;
		private Closure groupBy = null;
		private Closure accept = null;
		private Closure date = null;
		private List<EventDefinition> eventDefinitions = [];
		private int timeNotificationInterval
		private String description

		public Builder withTimeNotificationInterval(int time) {
			timeNotificationInterval = time;
			return this;
		}

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
		public CountRequest build() {
			CountRequest request = new CountRequest();
			if(notification != null) {
				request.notification = this.notification;
			}
			if(date != null) {
				request.date = this.date;
			}
			if(groupBy != null) {
				request.groupBy = this.groupBy;
			}
			if(accept != null) {
				request.accept = this.accept;
			}
			if(timeNotificationInterval != null) {
				request.timeNotificationInterval = timeNotificationInterval;
			}
			request.description = this.description
			request.eventDefinitions = eventDefinitions;
			return request;
		}
	}
}
