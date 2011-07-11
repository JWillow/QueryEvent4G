package org.homework.mcep.extractor

import spock.lang.Specification;

class AbstractExtractorTest extends Specification {

	Extractor extractor = new AbstractExtractor() {
		public List<DependOnToken> getDepends() {
			return dependOnTokens;
		}
		public Map<String,Object> applyExtraction(String line) {
			return null;
		}
		public String getEventName() {
			return null;
		}
	};

	def "satisfiedDependency : si les d�pendances sont satisfaites par les attributs d�j� collect� alors on retourne true"() {
		setup:
		DependOnToken dot = new DependOnToken(id:'at',value:"value")
		DependOnToken dot1 = new DependOnToken(id:'at1',value:"value1")
		extractor.getDepends() << dot << dot1
		
		expect:
		extractor.satisfiedDependency([re:'mmp',at1:'value1',at:'value']) == true
	}
	def "satisfiedDependency : si les d�pendances ne sont pas satisfaites par les attributs d�j� collect� alors on retourne false"() {
		setup:
		DependOnToken dot = new DependOnToken(id:'at',value:"value")
		DependOnToken dot1 = new DependOnToken(id:'at1',value:"value1")
		extractor.getDepends() << dot << dot1
		
		expect:
		extractor.satisfiedDependency([re:'mmp',at1:'val',at:'value4']) == false
	}
	def "satisfiedDependency : si les d�pendances ne sont pas satisfaites par les attributs d�j� collect� alors on retourne false, sachant que la liste des attributs collect�e est plus petite que la liste des d�pendances"() {
		setup:
		DependOnToken dot = new DependOnToken(id:'at',value:"value")
		DependOnToken dot1 = new DependOnToken(id:'at1',value:"value1")
		extractor.getDepends() << dot << dot1
		
		expect:
		extractor.satisfiedDependency([at1:'value1']) == false
	}
}
