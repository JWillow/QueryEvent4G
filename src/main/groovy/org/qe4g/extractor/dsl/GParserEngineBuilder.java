package org.qe4g.extractor.dsl;

import java.util.HashMap;

import org.qe4g.dsl.builder.GroovyBuilder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.extractor.Extractor;
import org.qe4g.extractor.ParserEngine;

/**
 * Core groovy builder for the {@link Extractor} part.
 * 
 * @author Willow
 * 
 */
public class GParserEngineBuilder extends GroovyBuilder<ParserEngine> {

	/**
	 * Register keywords:
	 * <ul>
	 * <li><i>engine</i>, see {@link ParserEngineBuilder}</li>
	 * <li><i>regExpExtractor</i>, see {@link RegexpExtractorBuilder}</li>
	 * <li><i>dependOnToken</i>, see {@link DependOnTokenBuilder}</li>
	 * <li><i>insertTime</i>, see {@link InsertTimeBuilder}</li>
	 * </ul>
	 * @see org.qe4g.dsl.builder.GroovyBuilder#init()
	 */
	@Override
	public void init() {
		builders = new HashMap<String, GroovySupportingBuilder<?>>();
		builders.put("engine",
				new org.qe4g.extractor.dsl.ParserEngineBuilder());
		builders.put("regExpExtractor", new RegexpExtractorBuilder());
		builders.put("dependOnToken", new DependOnTokenBuilder());
		builders.put("insertTime", new InsertTimeBuilder());
	}
}
