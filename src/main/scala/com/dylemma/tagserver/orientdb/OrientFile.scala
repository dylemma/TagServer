package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm._
import com.tinkerpop.blueprints.pgm.{ Vertex, Graph }

class OrientFile(vertex: Vertex, graph: Graph) extends VertexProxy {
	def this(graph: Graph) = this(graph.addVertex(), graph)

	def asVertex: Vertex = vertex

	//hard-wire the `type` to "file"
	vertex.setProperty("type", "file")

	private val _name = new VertexProperty[String](vertex, "filename", "<unknown name>")
	def name = _name()
	def name_=(n: String) = _name() = n

	private val _path = new VertexProperty[String](vertex, "filepath", "<unknown path>")
	def path = _path()
	def path_=(p: String) = _path() = p

	private val _md5 = new VertexProperty[String](vertex, "md5hash", "00")
	def md5 = _md5()
	def md5_=(hash: String) = _md5() = hash

	private implicit def upgradeVertex(v: Vertex) = new OrientFile(v, graph)

	private val _parent = new VertexRelation[OrientFile](vertex, graph, "parentFile", outgoing = true)
	def parent = _parent()
	def parent_=(p: OrientFile): Unit = parent = Some(p)
	def parent_=(p: Option[OrientFile]): Unit = p match {
		case None => _parent() = None
		case Some(f) => _parent() = Some(f)
	}

	private val _children = new VertexRelations(vertex, graph, "parentFile", outgoing = false)
	def children = _children()

	//TODO: for the implicit argument here, it would be better if there was a central source for the
	//implementation, as long as it means that it isn't repeated everywhere
	private val _tags = new VertexRelations[OrientTag](vertex, graph, "tag", outgoing = true)(v => new OrientTag(v, graph))
	def tags = _tags()
}

