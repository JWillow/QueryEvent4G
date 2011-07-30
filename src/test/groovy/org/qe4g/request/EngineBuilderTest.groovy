package org.qe4g.request

import org.qe4g.request.dsl.GRequestEngineBuilder;

import spock.lang.Specification

class EngineBuilderTest extends Specification {

	def engineBuilder = new GRequestEngineBuilder()
	RequestDispatcher engine = null
}
