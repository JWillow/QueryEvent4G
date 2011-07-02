package org.homework.mcep

class Event {
	List<String> names = []
	Map<String,Object> attributes = [:];
	
	boolean isInconsistent() {
		return attributes.size() == 0
	}
	
	public String toString() {
		"names:$names ; attributes:$attributes"
	}
}
