package com.dylemma.tagserver.orientdb.orm

import com.tinkerpop.blueprints.pgm.Vertex
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import com.orientechnologies.orient.core.record.impl.ODocument
import scala.collection.GenTraversableOnce

trait QueryableVertexGroup[T] {
	protected def upgradeVertex(v: Vertex, g: OrientGraph): T
	protected def baseCondition: Option[QueryCondition]

	private def upgradeDoc(graph: OrientGraph)(doc: ODocument): T = {
		val vert = graph.getVertex(doc.getIdentity)
		upgradeVertex(vert, graph)
	}

	private def queryGraph(graph: OrientGraph, query: Option[QueryCondition]): List[T] = {
		val oGraph = graph.getRawGraph
		val vertexType = oGraph.getVertexBaseClass.getName
		val baseQuery = "select from " + vertexType // + " where type = 'file'"

		val qs = (baseCondition, query) match {
			case (Some(l), Some(r)) => " where " + (l and r).toQueryString
			case (Some(l), None) => " where " + l.toQueryString
			case (None, Some(r)) => " where " + r.toQueryString
			case (None, None) => ""
		}

		val queryString = baseQuery + qs
		val oDocs = oGraph.queryBySql[ODocument](queryString)
		oDocs map upgradeDoc(graph)
	}

	def withFilter(p: this.type => QueryCondition)(implicit graph: OrientGraph) = {
		queryGraph(graph, Some(p(this)))
	}

	def map[B, That](f: T => B)(implicit graph: OrientGraph) = {
		queryGraph(graph, None).map(f)
	}

	def flatMap[B, That](f: T => GenTraversableOnce[B])(implicit graph: OrientGraph) = {
		queryGraph(graph, None).flatMap(f)
	}

	def foreach[U](f: T => U)(implicit graph: OrientGraph): Unit = {
		queryGraph(graph, None).foreach(f)
	}
}