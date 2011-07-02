package org.homework.mcep.extractor

import org.homework.mcep.Event;

import spock.lang.Specification;

class ParserEngineTest extends Specification {

	ParserEngine parserEngine;
	def line = "une ligne pour le test"

	def "2 extracteurs sont utilisés dès leur première sollicitation, le troisième n'est jamais sollicité"() {
		setup:"Configuration des 3 extracteurs"
		Extractor extractor = Mock()
		Extractor extractor1 = Mock()
		Extractor extractor2 = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor2).withExtractor(extractor1).withExtractor(extractor).build()
		and : "Cet extractor n'est pas utilisé"
		2 * extractor.satisfiedDependency(_) >> false
		and : "Cet extractor est utilisé dès la première sollicitation"
		1 * extractor1.satisfiedDependency(_) >> true
		and : "Cet extracteur est utilisé dès la première sollicitation"
		1 * extractor2.satisfiedDependency(_) >> true

		and:"Fin du paramètrage des extracteurs"
		1 * extractor1.extract(line) >> [et1Name:'name']
		1 * extractor2.extract(line) >> [et2Name:'name2']

		1 * extractor1.getEventName() >> 'et1'
		1 * extractor2.getEventName() >> 'et2'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 2
		assert event.names.size() == 2
		assert event.names.contains('et1')
		assert event.names.contains('et2')
		assert event.attributes.et2Name == 'name2'
		assert event.attributes.et1Name == 'name'
	}

	def "3 extractors sont paramètrés pour permettre d'extraire un événement, seulement 2 sont utilisées"() {
		setup:"Configuration des 3 extracteurs"
		Extractor extractor = Mock()
		Extractor extractor1 = Mock()
		Extractor extractor2 = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor2).withExtractor(extractor1).withExtractor(extractor).build()
		and : "Cette extractor simule une dépendance, car lors de la première solicitation il répond false"
		2 * extractor.satisfiedDependency(_) >>> [false, true]
		and : "Cette extractor n'est jamais utilisé"
		3 * extractor1.satisfiedDependency(_) >> false
		and : "Cette extracteur est utilisé dès la première sollicitation puis ne sera plus appelé"
		1 * extractor2.satisfiedDependency(_) >> true

		and:"Fin du paramètrage des extracteurs"
		1 * extractor.extract(line) >> [et1Name:'name']
		1 * extractor2.extract(line) >> [et2Name:'name2']

		1 * extractor.getEventName() >> 'et1'
		1 * extractor2.getEventName() >> 'et2'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 2
		assert event.names.size() == 2
		assert event.names.contains('et1')
		assert event.names.contains('et2')
		assert event.attributes.et2Name == 'name2'
		assert event.attributes.et1Name == 'name'
	}

	def "Aucun extracteur n'a pu extraire d'information"() {
		setup:
		Extractor extractor = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor).build()
		1 * extractor.satisfiedDependency(_) >> true
		1 * extractor.extract(line) >> [:]

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.isInconsistent()
	}
}
