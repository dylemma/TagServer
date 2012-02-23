package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm.QueryableVertexGroup
import com.dylemma.tagserver.orientdb.orm.PropertyIsQueryCondition
import com.dylemma.tagserver.orientdb.orm.VertexProxy
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import com.tinkerpop.blueprints.pgm.Vertex
import orm.VertexRelations
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientVertex

class OrientFileRoot(vertex: Vertex, graph: OrientGraph) extends VertexProxy {
	def this(graph: OrientGraph) = this(graph.addVertex(), graph)

	def asVertex = vertex

	vertex.setProperty("type", "fileroot")

	private val _members = new VertexRelations(vertex, graph, "member", outgoing = true)(v => new OrientFile(v, graph))
	def members = _members()
}

object OrientFileRoot {
	def apply()(implicit graph: OrientGraph) = synchronized {
		val gRoot = Option(graph.getRawGraph.getRoot("files"))

		val root = gRoot match {
			case Some(r) => r
			case None => //need to add the new root
				val root = graph.addVertex().asInstanceOf[OrientVertex].getRawElement
				graph.getRawGraph.setRoot("files", root)
				root
		}
		val vert = graph.getVertex(root.getIdentity)
		new OrientFileRoot(vert, graph)
	}
}