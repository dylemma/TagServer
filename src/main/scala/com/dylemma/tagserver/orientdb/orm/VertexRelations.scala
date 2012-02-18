package com.dylemma.tagserver.orientdb.orm

import com.tinkerpop.blueprints.pgm.Graph
import com.tinkerpop.blueprints.pgm.Vertex
import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.iterableAsScalaIterable

/** @author dylanh
  *
  */
class VertexRelations[T <: VertexProxy] // T must be a subtype of VertexProxy
(vertex: Vertex, graph: Graph, label: String, outgoing: Boolean = true) //args
(implicit up: Vertex => T) //need an implicit way to upgrade a Vertex to a T instance
{
	def apply(): collection.mutable.Set[T] = {
		if (outgoing) {
			new OutgoingEdgesSet
		} else {
			new IncomingEdgesSet
		}
	}

	/** A set that represents the items that the `vertex` has outgoing edges (labeled `label`) to.
	  */
	private class OutgoingEdgesSet extends collection.mutable.Set[T] {
		def iterator = for (edge <- vertex.getOutEdges(label).iterator) yield up(edge.getInVertex())
		def +=(elem: T) = {
			if (!contains(elem)) {
				val target = elem.asVertex
				graph.addEdge(null, vertex, target, label)
			}
			this
		}
		def -=(elem: T) = {
			val target = elem.asVertex
			//remove any/all edges that go from our `vertex` to the `target` (elem)
			for (edge <- vertex.getOutEdges(label) if edge.getInVertex() == target) {
				graph.removeEdge(edge)
			}
			this
		}
		def contains(elem: T) = {
			val target = elem.asVertex
			vertex.getOutEdges(label).find(_.getInVertex() == target).isDefined
		}
	}

	/** A set that represents the items that the `vertex` has incoming edges (labeled `label`) from. */
	private class IncomingEdgesSet extends collection.mutable.Set[T] {
		def iterator = for (edge <- vertex.getInEdges(label).iterator) yield up(edge.getOutVertex())
		def +=(elem: T) = {
			if (!contains(elem)) {
				val source = elem.asVertex
				graph.addEdge(null, source, vertex, label)
			}
			this
		}
		def -=(elem: T) = {
			val source = elem.asVertex
			//remove any/all edges that go from the `source` (elem) to our `vertex`
			for (edge <- vertex.getInEdges(label) if (edge.getOutVertex() == source)) {
				graph.removeEdge(edge)
			}
			this
		}
		def contains(elem: T) = {
			val source = elem.asVertex
			vertex.getInEdges(label).find(_.getOutVertex() == source).isDefined
		}
	}
}