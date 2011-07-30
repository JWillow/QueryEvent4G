package org.qe4g.extractor;

import java.util.Map;

/**
 * <p>
 * Standard to implements to create a new {@link Extractor}. An Extractor is an
 * object who is used to extract <b>token</b> from a {@link String}. A
 * <b>token</b> are identified by an unique id.
 * <p>
 * On a {@link String} data source many Extractor can be used to extract a
 * maximum of tokens. Some Extractor can be conditionnal, because it complete
 * other extractors. To response to this need the {@link DependOnToken} have
 * been introduced, see {@link #satisfiedDependency(Map)}.
 * 
 * @author Willow
 * @see AbstractExtractor
 */
public interface Extractor {

	/**
	 * Event name associated to tokens extracted
	 * 
	 * @return
	 */
	String getEventName();

	/**
	 * 
	 * @param line
	 *            - data source
	 * @return must return an empty {@link Map} if no data are extracted, else
	 *         return a {@link Map} of token id with associated value
	 */
	Map<String, Object> extract(String line);

	/**
	 * Used by dependencies management mechanism to know if an {@link Extractor}
	 * can be used by compare {@link DependOnToken} with attributes already
	 * collected.
	 * 
	 * @param collectedAttributes
	 * @return
	 */
	boolean satisfiedDependency(Map<String, String> collectedAttributes);

}
