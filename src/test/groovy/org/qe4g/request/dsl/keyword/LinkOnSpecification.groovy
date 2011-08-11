package org.qe4g.request.dsl.keyword

import org.qe4g.Event;
import org.w3c.dom.stylesheets.LinkStyle;

import spock.lang.Specification;

class LinkOnSpecification extends Specification{
	def otherEvent = new Event(names:["A"],attributes:[userId:"valueA",otherAttTest:"valueA",extension:"435667"])
	def currentEvent = new Event(names:["B"],attributes:[userId:"valueA",otherAttTest:"valueB",extension:"435667"])
	// linkOn:[1:['and':["acdId","userId"],'or':['toto','titi']],2:"extension",3:"extension<->phoneExtension"]

	/**
	* <pre>linkOn:[1:['userId','userId<->otherAttTest'],2:'extension']</pre>
	* @return
	*/
   def "Multiple link are defined between the first event and the current event, and between the third event and the current event. Complex case. Positive evaluation"() {
	   setup:
	   Map<Integer,Closure<?>> links = LinkOn.get("linkOn", [1:['userId','userId <->otherAttTest'],3:'extension']);
	   assert links.containsKey(1)
	   assert links.containsKey(3)
	   expect:
	   assert links[1].doCall(currentEvent,otherEvent)
	   assert links[3].doCall(currentEvent,otherEvent)
   }

   /**
   * <pre>linkOn:[1:['userId','otherAttTest'],3:'extension']</pre>
   * @return
   */
  def "Multiple link are defined between the first event and the current event, and between the third event and the current event. Complex case. Mixed evaluation"() {
	  setup:
	  Map<Integer,Closure<?>> links = LinkOn.get("linkOn", [1:['userId','otherAttTest'],3:'extension']);
	  assert links.containsKey(1)
	  assert links.containsKey(3)
	  expect:
	  assert links[1].doCall(currentEvent,otherEvent) == false
	  assert links[3].doCall(currentEvent,otherEvent)
  }
  
  /**
  * <pre>linkOn:[1:['userId','otherAttTest'],3:'extension']</pre>
  * @return
  */
 def "Multiple link are defined between the first event and the current event, and between the third event and the current event. Complex case with or condition. Positive evaluation"() {
	 setup:
	 Map<Integer,Closure<?>> links = LinkOn.get("linkOn", [1:[or:['userId','otherAttTest']],3:'extension']);
	 assert links.containsKey(1)
	 assert links.containsKey(3)
	 expect:
	 assert links[1].doCall(currentEvent,otherEvent)
	 assert links[3].doCall(currentEvent,otherEvent)
 }
   	
	/**
	* <pre>linkOn:[1:'extension',2:'userId']</pre>
	* @return
	*/
   def "Multiple link are defined between the first event and the current event, and between the second event and the current event. Simple case. Positive evaluation"() {
	   setup:
	   Map<Integer,Closure<?>> links = LinkOn.get("linkOn", [1:'extension',2:'userId']);
	   assert links.containsKey(1)
	   assert links.containsKey(2)
	   expect:
	   assert links[1].doCall(currentEvent,otherEvent)
	   assert links[2].doCall(currentEvent,otherEvent)
   }

   /**
   * <pre>linkOn:[1:'extension',2:'userId']</pre>
   * @return
   */
  def "Multiple link are defined between the first event and the current event, and between the second event and the current event. Simple case. Mixed evaluation"() {
	  setup:
	  Map<Integer,Closure<?>> links = LinkOn.get("linkOn", [1:'extension',2:'otherAttTest']);
	  assert links.containsKey(1)
	  assert links.containsKey(2)
	  expect:
	  assert links[1].doCall(currentEvent,otherEvent)
	  assert links[2].doCall(currentEvent,otherEvent) == false
  }
   	
	/**
	* <pre>linkOn:['or':['userId','otherAttTest'],'and':['extension']</pre>
	* @return
	*/
   def "Link between the current event and the last event. The relation is complexe, including or and and conditions. Positive evaluation"() {
	   setup:
	   Map<Integer,Closure<?>> links = LinkOn.get("linkOn", ['or':['userId','otherAttTest'],'and':['extension']]);
	   assert links.containsKey(0)
	   expect:
	   assert links[0].doCall(currentEvent,otherEvent)
   }

   /**
   * <pre>linkOn:['or':['userId','extension'],'and':['otherAttTest']</pre>
   * @return
   */
  def "Link between the current event and the last event. The relation is complexe, including or and and conditions. Negative evaluation"() {
	  setup:
	  Map<Integer,Closure<?>> links = LinkOn.get("linkOn", ['or':['userId','extension'],'and':['otherAttTest']]);
	  assert links.containsKey(0)
	  expect:
	  assert links[0].doCall(currentEvent,otherEvent) == false
  }
   	
	/**
	 * <pre>linkOn:['or':['userId','otherAttTest']]</pre>
	 * @return
	 */
	def "Link between the current event and the last event. The relation is implicite and it is express with an or condition. Positive evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", ['or':['userId','otherAttTest']]);
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent)
	}

	/**
	* <pre>linkOn:['or':['userId','otherAttTest']]</pre>
	* @return
	*/
   def "Link between the current event and the last event. The relation is implicite and it is express with an or condition. Negative evaluation"() {
	   setup:
	   Map<Integer,Closure<?>> links = LinkOn.get("linkOn", ['or':['userId<->extension','otherAttTest']]);
	   assert links.containsKey(0)
	   expect:
	   assert links[0].doCall(currentEvent,otherEvent) == false
   }
	
	/**
	 * <pre>linkOn:['userId','extension']</pre>
	 * @return
	 */
	def "Link between the current event and the last event. The relation is implicite with two criteria.Positive evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", ['userId', 'extension']);
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent)
	}

	/**
	 * <pre>linkOn:['userId','extension']</pre>
	 * @return
	 */
	def "Link between the current event and the last event. The relation is implicite with two criteria. Negative evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", ['userId', 'otherAttTest']);
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent) == false
	}

	/**
	 * <pre>linkOn:['userId','extension']</pre>
	 * @return
	 */
	def "Link between the current event and the last event. The relation has two criteria, with an explicite declaration. Positive evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", [
			'extension',
			'userId <-> otherAttTest'
		]);
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent)
	}

	/**
	 * <pre>linkOn:"extension<->phoneExtension"</pre>
	 * @return
	 */
	def "Link between the current event and the last event. The relation is an explicit relation definition. Positive evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", "userId<->otherAttTest");
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent)
	}
	/**
	 * <pre>linkOn:"extension<->phoneExtension"</pre>
	 * @return
	 */
	def "Link between the current event and the last event. The relation is an explicit relation definition. Negative evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", "otherAttTest<->userId");
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent) == false
	}


	/**
	 * <pre>linkOn:"userId"</pre>
	 * @return
	 */
	def "Simple link between the current event and the last event on their common attribute acdId. Positive evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", "userId");
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent)
	}

	/**
	 * <pre>linkOn:"otherAttTest"</pre>
	 * @return
	 */
	def "Simple link between the current event and the last event on their common attribute acdId. Negative evaluation"() {
		setup:
		Map<Integer,Closure<?>> links = LinkOn.get("linkOn", "otherAttTest");
		assert links.containsKey(0)
		expect:
		assert links[0].doCall(currentEvent,otherEvent) == false
	}
	
	/**
	* <pre>linkOn:"attThatDoesnotExist<->userId"</pre>
	* @return
	*/
   def "Simple link between the current event and the last event but the attribut exist only on one side"() {
	   setup:
	   Map<Integer,Closure<?>> links = LinkOn.get("linkOn", "attThatDoesnotExist<->userId");
	   assert links.containsKey(0)
	   expect:
	   assert links[0].doCall(currentEvent,otherEvent) == false
   }

}
