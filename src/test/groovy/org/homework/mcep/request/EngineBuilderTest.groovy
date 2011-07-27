package org.homework.mcep.request

import org.homework.mcep.request.dsl.GRequestEngineBuilder;

import spock.lang.Specification

class EngineBuilderTest extends Specification {

	def engineBuilder = new GRequestEngineBuilder()
	RequestDispatcher engine = null
}
