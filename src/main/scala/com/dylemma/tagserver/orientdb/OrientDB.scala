package com.dylemma.tagserver.orientdb

import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import orm._
import net.liftweb.common.Loggable
import com.orientechnologies.orient.core.record.impl.ODocument

object OrientDB extends Loggable {

	private val graphURI = "local:db/"

	private var _graph: OrientGraph = null

	def init = {
		if (_graph == null) {
			logger.debug("Initializing OrientDB Graph")
			_graph = new OrientGraph(graphURI)
		}
		true
	}

	def graph = if (init) _graph else throw new NullPointerException("graph is null because OrientDB could not be initialized")

	def withGraph[A](body: OrientGraph => A) = body(graph)

	def unload: Unit = if (_graph != null) {
		println("Shutting down OrientDB Graph")
		logger.debug("Shutting down OrientDB Graph")
		_graph.getRawGraph.close
		_graph.shutdown
		_graph = null
	}
}

object TestThing extends App {
	println("startup")

	OrientDB.withGraph { implicit graph =>

		for (f <- OrientFiles) println(f)
	}

	OrientDB.unload
	println("shutdown")
}