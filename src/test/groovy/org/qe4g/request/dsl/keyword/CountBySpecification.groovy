package org.qe4g.request.dsl.keyword

import org.qe4g.Event;

import spock.lang.Specification;

class CountBySpecification extends Specification {

	def eventA = new Event(names:["A"],attributes:[userId:"valueA",otherAttTest:"valueA",extension:"0494"])
	def eventB = new Event(names:["B"],attributes:[userId:"valueA",otherAttTest:"valueB",extension:"0578"])
	Map<String,Object> context = [:]

	/**
	 * <pre>by:"userId"</pre>
	 * @return
	 */
	def "Count by one attribut on last event processed" (){
		setup:
		Closure closure = CountBy.get("by","userId");
		when: closure.doCall(context,[eventA, eventB])
		then: context['[userId:valueA]'] == 1
	}

	/**
	 * <pre>by:"userId"</pre>
	 * @return
	 */
	def "Count by one attribut on last event processed, increment the cpt" (){
		setup:
		context['[userId:valueA]'] = 10
		Closure closure = CountBy.get("by","userId");
		when: closure.doCall(context,[eventA, eventB])
		then: context['[userId:valueA]'] == 11
	}

	/**
	 * <pre>by:["userId","extension"]</pre>
	 * @return
	 */
	def "Count by two attributs on last event processed" (){
		setup:
		Closure closure = CountBy.get("by",["userId", "extension"]);
		when: closure.doCall(context,[eventA, eventB])
		then: context['[userId:valueA, extension:0578]'] == 1
	}

	/**
	 * <pre>by:[1:"extension",2:"userId"]</pre>
	 * @return
	 */
	def "Count by two attributs on different event" (){
		setup:
		Closure closure = CountBy.get("by",[1:"extension",2:"userId"]);
		when: closure.doCall(context,[eventA, eventB])
		then: context['[extension:0494, userId:valueA]'] == 1
	}

	/**
	 * <pre>by:[1:"extension",2:["userId","otherAttTest"]]</pre>
	 * @return
	 */
	def "Count on different event with different group of criterion" (){
		setup:
		Closure closure = CountBy.get("by",[1:"extension",2:["userId", "otherAttTest"]]);
		when: closure.doCall(context,[eventA, eventB])
		then: context['[extension:0494, userId:valueA, otherAttTest:valueB]'] == 1
	}

	/**
	* <pre>by:{it.attributes['extension'].substring(0,2)}</pre>
	* @return
	*/
   def "Count on event with closure criterion" (){
	   setup:
	   Closure closure = CountBy.get("by",{it.attributes['extension'].substring(0,2)});
	   when: closure.doCall(context,[eventA, eventB])
	   then: context['[05]'] == 1
   }
   
   /**
   * <pre>by:[1:["userId",{it.attributes['extension'].substring(0,2)}],2:"otherAttTest"]</pre>
   * @return
   */
  def "Count on event with closure criterion with multiple event case, with multiple criteria" (){
	  setup:
	  Closure closure = CountBy.get("by",[1:["userId",{it.attributes['extension'].substring(0,2)}],2:"otherAttTest"]);
	  when: closure.doCall(context,[eventA, eventB])
	  then: context['[userId:valueA, 04, otherAttTest:valueB]'] == 1
  }
  
  /**
  * <pre>by:[1:{it.attributes['extension'].substring(0,2)},2:"otherAttTest"]</pre>
  * @return
  */
 def "Count on event with closure criterion with multiple event case" (){
	 setup:
	 Closure closure = CountBy.get("by",[1:{it.attributes['extension'].substring(0,2)},2:"otherAttTest"]);
	 when: closure.doCall(context,[eventA, eventB])
	 then: context['[04, otherAttTest:valueB]'] == 1
 }
}
