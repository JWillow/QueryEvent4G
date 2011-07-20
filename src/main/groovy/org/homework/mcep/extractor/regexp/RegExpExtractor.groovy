package org.homework.mcep.extractor.regexp

import java.util.Map;
import java.util.regex.Pattern;

import org.homework.mcep.extractor.AbstractExtractor;
import org.homework.mcep.extractor.DependOnToken;
import org.homework.mcep.extractor.PostProcess;

/**
 * Extractor based on a regular expression to extract token from a {@link String}.
 *  
 * @author Willow
 * @see RegExpExtractorSpecification
 */
class RegExpExtractor extends AbstractExtractor {

	/**
	 * Event name identifier to attach attributes
	 */
	String eventName


	/**
	 * Regular expression apply for the extraction
	 */
	Pattern exp

	/**
	 * <i>Token</i> produce by the extraction. A positionnal link exist between token name and group who matche inside the Regular expression.
	 */
	List<String> tokens = []

	/**
	 * <p>Apply {@link #exp} to the parameter, and create a new {@link Map} linking regExp group and token position inside the properties.
	 * <p>If inside the token list they are blank value, then the regExp group corresponding to the position of the blank value is ignored.
	 * @see org.homework.mcep.extractor.AbstractExtractor#applyExtraction(java.lang.String)
	 */
	public Map<String,String> applyExtraction(String line) {
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
		private List<PostProcess> postProcesses = []

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

		public Builder addPostProcess(PostProcess arg) {
			postProcesses << arg
			return this
		}

		public RegExpExtractor build() {
			RegExpExtractor extractor = new RegExpExtractor()
			extractor.eventName = this.eventName
			extractor.dependOnTokens = this.dependOnTokens
			extractor.exp = Pattern.compile(this.exp)
			extractor.tokens = this.tokens
			extractor.postProcesses = this.postProcesses
			return extractor
		}
	}
}
