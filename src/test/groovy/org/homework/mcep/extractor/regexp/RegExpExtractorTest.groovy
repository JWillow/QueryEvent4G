package org.homework.mcep.extractor.regexp

import spock.lang.Specification;

class RegExpExtractorTest extends Specification {

	RegExpExtractor regExpExtractor = null;
	
	def "Si le pattern match avec la ligne passée en paramètre alors on obtient la liste des attributs" () {
		setup:
		regExpExtractor = RegExpExtractor.builder().extractTokens("test,test1").produceEvent('testEvent')
			.useExpression('(voici).*?(test)').build();
		when:
		Map<String,String> event = regExpExtractor.extract("voici un test")
		
		then:
		assert event.size() == 2
		assert event.test == 'voici'
		assert event.test1 == 'test'
	}
	
	def "Si des groupes (au sens regexp) correspondent à des tokens 'vide' alors ils sont ignorés" () {
		setup:
		regExpExtractor = RegExpExtractor.builder().extractTokens("test,,test1").produceEvent('testEvent')
			.useExpression('(voici) (un) (test)').build();
		when:
		Map<String,String> event = regExpExtractor.extract("voici un test")
		
		then:
		assert event.size() == 2
		assert event.test == 'voici'
		assert event.test1 == 'test'
	}
}
