package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm._
import com.tinkerpop.blueprints.pgm.{ Vertex, Graph }

class OrientFile(vertex: Vertex, graph: Graph) extends VertexProxy {
	def this(graph: Graph) = this(graph.addVertex(), graph)

	def asVertex: Vertex = vertex

	//hard-wire the `type` to "file"
	vertex.setProperty("type", "file")

	val name = new VertexProperty[String](vertex, "filename", "<unknown name>")
	val path = new VertexProperty[String](vertex, "filepath", "<unknown path>")

	private implicit def upgradeVertex(v: Vertex) = new OrientFile(v, graph)

	val parent = new VertexRelation[OrientFile](vertex, graph, "parentFile", outgoing = true)
	val children = new VertexRelations(vertex, graph, "parentFile", outgoing = false)

	//TODO: for the implicit argument here, it would be better if there was a central source for the
	//implementation, as long as it means that it isn't repeated everywhere
	val tags = new VertexRelations[OrientTag](vertex, graph, "tag", outgoing = true)(v => new OrientTag(v, graph))
}

