package com.dylemma.tagserver.orientdb

import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import com.tinkerpop.blueprints.pgm.TransactionalGraph
import TransactionalGraph.Conclusion._
import net.liftweb.util.Helpers._
import scala.collection.JavaConverters._
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx

package object orm {
	/** Obtains an OrientGraph for the given `uri`, and executes the `body`
	  * with it. At the end (even on failure), the graph is shut down.
	  */
	def withGraph[A](uri: String)(body: OrientGraph => A): A = {
		var graph: OrientGraph = null
		try {
			graph = new OrientGraph(uri)
			body(graph)
		} finally {
			if (graph != null) {
				graph.getRawGraph.close
				graph.shutdown
				println("Graph has been shut down")
			}
		}
	}

	implicit def upgradeTransactional(graph: TransactionalGraph) = new {
		/** Wraps the `body` in a transaction on the graph. */
		def withTransaction[A](body: => A): A = {
			graph.startTransaction()
			try {
				val result = body
				graph.stopTransaction(SUCCESS)
				result
			} finally {
				graph.stopTransaction(FAILURE)
			}
		}
	}

	implicit def dbWrapper(db: ODatabaseDocumentTx) = new {
		def queryBySql[T](sql: String, params: AnyRef*): List[T] = {
			val params4java = params.toArray
			val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params4java: _*)
			results.asScala.toList
		}
	}

	implicit object StringValueConverter extends ValueConverter[String] {
		def apply(value: String) = value
		def unapply(raw: String) = Some(raw)
	}

	implicit object IntValueConverter extends ValueConverter[Int] {
		def apply(value: Int) = value.toString
		def unapply(raw: String) = AsInt.unapply(raw)
	}

}