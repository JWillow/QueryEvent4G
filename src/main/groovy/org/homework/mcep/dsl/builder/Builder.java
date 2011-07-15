package org.homework.mcep.dsl.builder;

/**
 * Implemented by {@link GroovyBuilder} to uniform the usage of traditional Java
 * builder inside the Groovy Buider system
 * 
 * @author Willow
 * 
 * @param <T> - Object builded
 */
public interface Builder<T> {
	T build();
}
