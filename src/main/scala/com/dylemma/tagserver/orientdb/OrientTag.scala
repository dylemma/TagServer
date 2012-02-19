package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm._
import com.tinkerpop.blueprints.pgm.{ Vertex, Graph }

class OrientTag(vertex: Vertex, graph: Graph) extends VertexProxy {
	def this(graph: Graph) = this(graph.addVertex(), graph)

	def asVertex: Vertex = vertex

	//hard-wire the `type` to "tag"
	vertex.setProperty("type", "tag")

	private val _name = new VertexProperty(vertex, "tagName", "<unnamed tag>")
	def name = _name()
	def name_=(n: String) = _name() = n

	private val _taggedFiles = new VertexRelations(vertex, graph, "tag", outgoing = false)(v => new OrientFile(v, graph))
	def taggedFiles = _taggedFiles()
}