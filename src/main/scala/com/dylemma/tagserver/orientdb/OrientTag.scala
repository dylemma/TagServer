package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm._
import com.tinkerpop.blueprints.pgm.{ Vertex, Graph }

class OrientTag(vertex: Vertex, graph: Graph) extends VertexProxy {
	def this(graph: Graph) = this(graph.addVertex(), graph)

	def asVertex: Vertex = vertex

	//hard-wire the `type` to "tag"
	vertex.setProperty("type", "tag")

	val name = new VertexProperty(vertex, "tagName", "<unnamed tag>")

	val taggedFiles = new VertexRelations(vertex, graph, "tag", outgoing = false)(v => new OrientFile(v, graph))
}