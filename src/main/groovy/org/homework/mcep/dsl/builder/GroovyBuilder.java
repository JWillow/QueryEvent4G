package org.homework.mcep.dsl.builder;

import groovy.util.BuilderSupport;

import java.util.Map;

public abstract class GroovyBuilder<T> extends BuilderSupport implements
		Builder<T> {

	protected Map<String, GroovySupportingBuilder<?>> builders;

	private GroovySupportingBuilder<?> rootBuilder;

	public GroovyBuilder() {
		init();
	}
	
	/**
	 * Permet l'enregistrement des builder de type
	 * {@link GroovySupportingBuilder} au sein de la map <code>builders</code>
	 */
	public abstract void init();

	private GroovySupportingBuilder<?> getBuilder(Object name) {
		GroovySupportingBuilder<?> builder = builders.get((String) name);
		if (rootBuilder == null) {
			rootBuilder = builder;
		}
		return builder;
	}

	protected void setParent(Object parent, Object child) {
		System.out.println(parent);
	}

	protected Object createNode(Object name) {
		return getBuilder(name);
	}

	protected Object createNode(Object name, Object value) {
		return getBuilder(name).withData(value);
	}

	protected Object createNode(Object name, Map attributes) {
		return getBuilder(name).withAttributes(attributes);
	}

	protected Object createNode(Object name, Map attributes, Object value) {
		return getBuilder(name).withAttributes(attributes).withData(value);
	}

	public T build() {
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
		pBuilder.withBuilder((Builder) child);
	}

}
