package org.homework.mcep.extractor.regexp

import org.homework.mcep.extractor.AbstractExtractor;
import org.homework.mcep.extractor.DependOnToken;

class RegExpExtractor extends AbstractExtractor {
	
	String eventName
	String exp 
	List<String> tokens = []
	
	public Map<String,String> extract(String line) {
		def collect = [:]
		def matcher = (line =~ exp)
		if(matcher.find()) {
			def cpt = 0
			while(cpt < matcher.groupCount() && cpt < tokens.size()) {
				if(tokens[cpt] != '') {
					collect[tokens[cpt]] = matcher.group(cpt+1)
				}
				cpt++
			}
		}
		return collect
	}
		
	// ------------
	// BUILDER PART
	// ------------
	private RegExpExtractor(){}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private String eventName
		private String exp
		private List<String> tokens = []
		private List<DependOnToken> dependOnTokens = []

		public Builder produceEvent(String eventName) {
			this.eventName = eventName
			return this
		}
		public Builder useExpression(String expression) {
			this.exp = expression
			return this
		}
		public Builder extractTokens(String tokens) {
			this.tokens = tokens.split(',').collect { it.trim()}
			return this
		}
		public Builder dependOnToken(DependOnToken dot) {
			dependOnTokens << dot
			return this
		}
				
		public RegExpExtractor build() {
			RegExpExtractor extractor = new RegExpExtractor()
			extractor.eventName = this.eventName
			extractor.dependOnTokens = this.dependOnTokens
			extractor.exp = this.exp
			extractor.tokens = this.tokens
			return extractor
		}
	}
}
