package org.qe4g

/**
 * Event is a Value Object who have names and attributes. Events are the base of the CEP.
 * 
 * @author Willow
 * @specification {@link EventSpecification}
 */
class Event {

	/**
	 *  An Event can have some names, generally the names are inherits representation.
	 */
	List<String> names = []

	/**
	 * Attributes are key/value information about Event. The keys are String object, and value are Object.
	 */
	Map<String,Object> attributes = [:];

	/**
	 * Warning : This property is not used inside the equals and hashCode methods
	 */
	long triggeredTime = -1;

	long creationTime;

	public Event() {
		creationTime = System.currentTimeMillis();
	}

	public long getTime() {
		if(triggeredTime == -1) {
			return creationTime;
		}
		return triggeredTime;
	}
	
	/**
	 * @return <code>true</code> if event contains no names else return <code>false</code>
	 */
	boolean isInconsistent() {
		return names.size() == 0
	}

	@Override
	public String toString() {
		"Event[names:$names;attributes:$attributes;time:${this.getTime()}]"
	}
}
