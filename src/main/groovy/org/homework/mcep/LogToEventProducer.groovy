package org.homework.mcep

import org.homework.mcep.extractor.ParserEngine;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;


class LogToEventProducer {


	public static void main(String[] args) {
		def config = new ConfigSlurper().parse(new File('Config.groovy').toURL())
		def engine = ParserEngine.create(config)
		Configuration configuration = new Configuration();
		engine.eventDefinitions.each {
			key, value ->
			configuration.addEventType(key,value)
		}
	}
}
