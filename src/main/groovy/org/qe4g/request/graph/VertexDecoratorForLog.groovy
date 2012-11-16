package org.qe4g.request.graph

import com.tinkerpop.blueprints.pgm.Vertex;

import static org.qe4g.request.graph.VertexTypes.*;
class VertexDecoratorForLog {
	Vertex vertex;

	
	
	
	public String toString() {
		StringBuffer strBuffer = new StringBuffer(vertex.toString());
		strBuffer.append("contains-(");
		switch(vertex.type) {
			case EVENT :
				strBuffer.append(vertex.event.toString());
				break;
			default:
				return vertex.toString();
		}
		strBuffer.append(")");
		return strBuffer.toString();
	}
}
