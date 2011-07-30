package org.qe4g.extractor.postprocess

import java.text.SimpleDateFormat;
import java.util.Map;

import org.qe4g.extractor.Extractor;
import org.qe4g.extractor.PostProcess;
import org.qe4g.request.Request;

/**
 * <p>PostProcess used to include a <b>new token</b> to the extraction process by {@link Extractor}. The new token is : <i>time</i>, it is the result of the convertion of a String token (representing a date) to long.
 * <p>When we process a log based Event, this {@link PostProcess} is usefull to use time base {@link Request}. 
 * 
 * @author Willow
 */
class InsertTime implements PostProcess {

	/**
	 * Token id of the token value to convert
	 */
	String tokenToConvert

	/**
	 * To process the convertion between {@link String} to {@link Long}
	 */
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
