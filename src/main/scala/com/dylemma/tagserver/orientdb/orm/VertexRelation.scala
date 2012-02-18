package com.dylemma.tagserver.orientdb.orm

import com.tinkerpop.blueprints.pgm.Graph
import com.tinkerpop.blueprints.pgm.Vertex
import scala.collection.JavaConversions.iterableAsScalaIterable

/** Describes a 1-to-1 relationship to/from an instance of T
  * @tparam T any implementation of a VertexProxy
  * @author dylanh
  */
class VertexRelation[T <: VertexProxy] // T must be a subtype of a VertexProperty
(vertex: Vertex, graph: Graph, label: String, outgoing: Boolean = true) //args
(implicit up: Vertex => T) //need an implicit way to upgrade a Vertex to a T instance 
{
	def apply(): Option[T] = {
		if (outgoing) {
			vertex.getOutEdges(label).headOption.map {
				case edge => up(edge.getInVertex())
			}
		} else {
			vertex.getInEdges(label).headOption.map {
				case edge => up(edge.getOutVertex())
			}
		}
	}

	def update(newValue: T): Unit = {
		if (outgoing) {
			for (edge <- vertex.getOutEdges(label)) {
				graph.removeEdge(edge)
			}
			graph.addEdge(null, vertex, newValue.asVertex, label)
		} else {
			for (edge <- vertex.getInEdges(label)) {
				graph.removeEdge(edge)
			}
			graph.addEdge(null, newValue.asVertex, vertex, label)
		}
	}
}