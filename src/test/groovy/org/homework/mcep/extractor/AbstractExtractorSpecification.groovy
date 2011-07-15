package org.homework.mcep.extractor

import java.util.Map;

import spock.lang.Specification;

class AbstractExtractorSpecification extends Specification {


	private AbstractExtractor getExtractor(final def tokens) {
		return new AbstractExtractor() {
			protected Map<String,String> applyExtraction(String line) {
				return tokens
			}
			public List<DependOnToken> getDepends() {
				return dependOnTokens;
			}
			public String getEventName() {
				return null;
			}
		}
	}

	def "If no tokens are extract the PostProcess are not called"() {
		setup :
		PostProcess pp = Mock(PostProcess)
		AbstractExtractor extractor = getExtractor([:])
		extractor.postProcesses << pp
		when:
		extractor.extract("my ligne")
		then :
		0 * pp.process(_)
	}

	def "If tokens are extract the PostProcess are called"() {
		setup :
		PostProcess pp = Mock(PostProcess)
		AbstractExtractor extractor = getExtractor([test:'value'])
		extractor.postProcesses << pp
		when:
		extractor.extract("my ligne")
		then :
		1 * pp.process([test:'value'])
	}
	
	def "If all dependencies are satisfied return true"() {
		setup:
		DependOnToken dot = new DependOnToken(id:'at',value:"value")
		DependOnToken dot1 = new DependOnToken(id:'at1',value:"value1")
		AbstractExtractor extractor = getExtractor([:])
		extractor.getDepends() << dot << dot1
		
		expect:
		extractor.satisfiedDependency([re:'mmp',at1:'value1',at:'value']) == true
	}
	
	def "If a dependency is not satisfied return false"() {
		setup:
		DependOnToken dot = new DependOnToken(id:'at',value:"value")
		DependOnToken dot1 = new DependOnToken(id:'at1',value:"value1")
		AbstractExtractor extractor = getExtractor([:])
		extractor.getDepends() << dot << dot1
		
		expect:
		extractor.satisfiedDependency([re:'mmp',at1:'val',at:'value4']) == false
	}
}
