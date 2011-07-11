package org.homework.mcep.extractor;

import java.util.HashMap;

import org.homework.mcep.dsl.builder.GroovyBuilder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.extractor.dsl.DependOnTokenBuilder;
import org.homework.mcep.extractor.dsl.InsertTimeBuilder;
import org.homework.mcep.extractor.dsl.RegexpExtractorBuilder;

public class GParserEngineBuilder extends GroovyBuilder<ParserEngine> {

	@Override
	public void init() {
		builders = new HashMap<String, GroovySupportingBuilder<?>>();
		builders.put("engine", new org.homework.mcep.extractor.dsl.ParserEngineBuilder());
		builders.put("regExpExtractor", new RegexpExtractorBuilder());
		builders.put("dependOnToken", new DependOnTokenBuilder());
		builders.put("insertTime", new InsertTimeBuilder());
	}
}
