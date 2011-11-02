package org.qe4g.request.function;

import java.util.Map;

import org.qe4g.request.graph.Path;

public interface Core {
	void onPatternDetection(Map<Object,Object> context, Path path);
}
