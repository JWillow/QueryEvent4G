package org.qe4g.request.evaluation;

import java.awt.font.GraphicAttribute;
import java.util.List;
import org.qe4g.request.graph.MyGraph;
import org.qe4g.request.graph.dsl.GraphCategory;

import com.tinkerpop.blueprints.pgm.Vertex;

import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.MyGraph.*

enum OccurResponse {
	OK,
	CONTINUE_WITH_NEXT_EVALUATOR,
	KO,
	OK_BUT_KEEP_ME,
	KO_BUT_KEEP_ME;
}
