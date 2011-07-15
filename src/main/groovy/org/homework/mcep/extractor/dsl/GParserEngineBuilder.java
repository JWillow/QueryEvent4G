package org.homework.mcep.extractor.dsl;

import java.util.HashMap;

import org.homework.mcep.dsl.builder.GroovyBuilder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.extractor.Extractor;
import org.homework.mcep.extractor.ParserEngine;

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
	 * @see org.homework.mcep.dsl.builder.GroovyBuilder#init()
	 */
	@Override
	public void init() {
		builders = new HashMap<String, GroovySupportingBuilder<?>>();
		builders.put("engine",
				new org.homework.mcep.extractor.dsl.ParserEngineBuilder());
		builders.put("regExpExtractor", new RegexpExtractorBuilder());
		builders.put("dependOnToken", new DependOnTokenBuilder());
		builders.put("insertTime", new InsertTimeBuilder());
	}
}
