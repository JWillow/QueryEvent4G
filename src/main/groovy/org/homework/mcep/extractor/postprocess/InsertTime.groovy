package org.homework.mcep.extractor.postprocess

import java.text.SimpleDateFormat;
import java.util.Map;

import org.homework.mcep.extractor.PostProcess;

/**
 * Permet d'insérer dans la liste des tokens un attribut 'time' au format long qui correspond à la date de l'événement
 * @author Willow
 *
 */
class InsertTime implements PostProcess {

	String tokenToConvert
	SimpleDateFormat sdf

	public Map<String, Object> process(Map<String, Object> tokens) {
		String value = tokens[tokenToConvert]
		if(!value) {
			throw new IllegalArgumentException("On tokens " + tokens);
		}
		long time = sdf.parse(value).time;
		tokens['time'] = time;
		return tokens
	}

	// ------------
	// BUILDER PART
	// ------------
	private InsertTime() {}

	public static Builder builder() {
		return new Builder()
	}

	public static class Builder {
		private String token;
		private String format;

		public Builder onToken(String token) {
			this.token = token
			return this
		}

		public Builder withFormat(String format) {
			this.format = format
			return this
		}

		public InsertTime build() {
			InsertTime dpp = new InsertTime()
			dpp.tokenToConvert = this.token
			dpp.sdf = new SimpleDateFormat(this.format)
			return dpp
		}

	}
}
