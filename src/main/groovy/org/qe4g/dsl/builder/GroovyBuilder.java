package org.qe4g.dsl.builder;

import groovy.util.BuilderSupport;

import java.util.Map;

import org.qe4g.request.Counter;

/**
 * Abstract class to extend by to plug Java builders to Groovy builder mechanism
 * 
 * @author Willow
 * 
 * @param <T>
 */
public abstract class GroovyBuilder<T> extends BuilderSupport implements
		Builder<T> {

	/**
	 * Java builder to plug to keyword in Groovy builder mechanism
	 */
	protected Map<String, GroovySupportingBuilder<?>> builders;

	private GroovySupportingBuilder<?> rootBuilder;

	public GroovyBuilder() {
		init();
	}

	/**
	 * Must be provided to register builder {@link GroovySupportingBuilder} au
	 * sein de la map <code>builders</code>
	 */
	public abstract void init();

	private GroovySupportingBuilder<?> getBuilder(Object name) {
		GroovySupportingBuilder<?> builder = builders.get((String) name);
		if (rootBuilder == null) {
			rootBuilder = builder;
		}
		return builder;
	}

	private String childName;
	
	protected void setParent(Object parent, Object child) {
	}

	protected Object createNode(Object name) {
		Counter.start();
		childName = (String) name;
		return getBuilder(name);
	}

	protected Object createNode(Object name, Object value) {
		Counter.start();
		childName = (String) name;
		return getBuilder(name).withData(value);
	}

	protected Object createNode(Object name, Map attributes) {
		Counter.start();
		childName = (String) name;
		return getBuilder(name).withAttributes(attributes);
	}

	protected Object createNode(Object name, Map attributes, Object value) {
		Counter.start();
		childName = (String) name;
		return getBuilder(name).withAttributes(attributes).withData(value);
	}

	public T build() {
		Counter.reset();
		return (T) rootBuilder.build();
	}

	@Override
	protected Object postNodeCompletion(Object parent, Object node) {
		if (parent == null) {
			return rootBuilder.build();
		}
		return node;
	}

	protected void nodeCompleted(Object parent, Object child) {
		if (parent == null) {
			return;
		}
		GroovySupportingBuilder pBuilder = (GroovySupportingBuilder) parent;
		pBuilder.withBuilder(childName,(Builder) child);
		Counter.stop();
	}
}
