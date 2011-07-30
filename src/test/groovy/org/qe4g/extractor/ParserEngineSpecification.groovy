package org.qe4g.extractor

import org.qe4g.Event;

import spock.lang.Specification;

class ParserEngineSpecification extends Specification {

	ParserEngine parserEngine;
	def line = "One line for test"

	def "If an extractor satisfied not the dependencies it is not used and the Event produce by the Parser is inconsistent"() {
		setup :
		Extractor extractor = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor).build()
		1 * extractor.satisfiedDependency(_) >> false

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.isInconsistent()
		0 * extractor.extract(line)
	}

	def "If an extractor satisfied the dependencies it is used and the Event produce has name and attribute provide by extractor"() {
		setup:
		Extractor extractor = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor).build()
		and:"Prepare extractor behaviour"
		1 * extractor.satisfiedDependency(_) >> true
		1 * extractor.extract(line) >> [attr:'myAttr']
		1 * extractor.getEventName() >> 'ExtractorEvent'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 1
		assert event.names.size() == 1
		assert event.names.contains('ExtractorEvent')
		assert event.attributes.attr == 'myAttr'
	}

	def "If two extractors satisfied the dependencies they are used. The attributes and event name provide by extractor are concat to create an Event"() {
		setup:
		Extractor extractor = Mock()
		Extractor otherExtractor = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor).withExtractor(otherExtractor).build()
		and:"Prepare extractor behaviour"
		1 * extractor.satisfiedDependency(_) >> true
		1 * extractor.extract(line) >> [attr:'myAttr']
		1 * extractor.getEventName() >> 'ExtractorEvent'
		and:"Prepare other extractor behaviour"
		1 * otherExtractor.satisfiedDependency(_) >> true
		1 * otherExtractor.extract(line) >> [otherAttr:'otherMyAttr']
		1 * otherExtractor.getEventName() >> 'OtherExtractorEvent'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 2
		assert event.names.size() == 2
		assert event.names.contains('ExtractorEvent')
		assert event.names.contains('OtherExtractorEvent')
		assert event.attributes.attr == 'myAttr'
		assert event.attributes.otherAttr == 'otherMyAttr'
	}

	def "Two extractors are registred. One with dependency constraint and an other with no dependency. During the first iteration the extractor with no dependencies is not used, but during the second iteration the dependencies constraint are satisfied and it is used. The extractor with no dependencies is used only during the first iteration, only one time. Finally the two are used"() {
		setup:
		Extractor extractorWithADependency = Mock()
		Extractor extractorWithNoDependency = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractorWithADependency).withExtractor(extractorWithNoDependency).build()
		and:"Prepare extractor with dependency behaviour. It is tested during two iteration."
		2 * extractorWithADependency.satisfiedDependency(_) >>> [false, true]
		1 * extractorWithADependency.extract(line) >> [attr:'myAttr']
		1 * extractorWithADependency.getEventName() >> 'ExtractorEvent'
		and:"Prepare other extractor behaviour"
		1 * extractorWithNoDependency.satisfiedDependency(_) >> true
		1 * extractorWithNoDependency.extract(line) >> [otherAttr:'otherMyAttr']
		1 * extractorWithNoDependency.getEventName() >> 'OtherExtractorEvent'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 2
		assert event.names.size() == 2
		assert event.names.contains('ExtractorEvent')
		assert event.names.contains('OtherExtractorEvent')
		assert event.attributes.attr == 'myAttr'
		assert event.attributes.otherAttr == 'otherMyAttr'
	}

}
