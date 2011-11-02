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
	long triggeredTime

	/**
	 * @return <code>true</code> if event contains no names else return <code>false</code>
	 */
	boolean isInconsistent() {
		return names.size() == 0
	}

	@Override
	public String toString() {
		"Event[names:$names;attributes:$attributes]"
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((names == null) ? 0 : names.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (names == null) {
			if (other.names != null)
				return false;
		} else if (!names.equals(other.names))
			return false;
		return true;
	}
	
	
}
