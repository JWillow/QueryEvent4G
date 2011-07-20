package org.homework.mcep.extractor.regexp

import spock.lang.Specification;

class RegExpExtractorSpecification extends Specification {

	RegExpExtractor regExpExtractor = null;

	def "if the regular expression match then we obtain a list of key/value. The keys represent the tokens define inside the Extractor and the value to the group inside the regExp Matcher " () {
		setup:
		regExpExtractor = RegExpExtractor.builder().extractTokens("test,test1").produceEvent('testEvent')
		.useExpression('(voici).*?(test)').build();

		when:
		Map<String,Object> attributes = regExpExtractor.extract("voici un test")

		then:
		assert attributes.size() == 2
		assert attributes.test == 'voici'
		assert attributes.test1 == 'test'
	}

	def "if inside the token list define inside the Extractor contains blank value then the corresponding RegExp group are ignored to create the attributes list" () {
		setup:
		regExpExtractor = RegExpExtractor.builder().extractTokens("test,,test1, ").produceEvent('testEvent')
		.useExpression('(voici) (un) (test)').build();
		when:
		Map<String,String> attributes = regExpExtractor.extract("voici un test")

		then:
		assert attributes.size() == 2
		assert attributes.test == 'voici'
		assert attributes.test1 == 'test'
	}

	def "The regExp doesn't match then we return an empty Map of attributes" () {
		setup:
		regExpExtractor = RegExpExtractor.builder().extractTokens("test,,test1, ").produceEvent('testEvent')
		.useExpression('\\d+').build();
		when:
		Map<String,String> attributes = regExpExtractor.extract("voici un test")

		then:
		assert attributes.size() == 0
	}
}
