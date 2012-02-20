package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm._
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import scala.collection.generic.CanBuildFrom
import scala.collection.GenTraversableOnce
import com.tinkerpop.blueprints.pgm.Vertex

/** This object should serve as the jumping-off point for simple OrientFile-related queries.
  */
object OrientFiles extends QueryableVertexGroup[OrientFile] {

	val name = QueryableProperty[String]("filename")
	val path = QueryableProperty[String]("filepath")
	val md5 = QueryableProperty[String]("md5hash")

	protected def upgradeVertex(v: Vertex, g: OrientGraph) = new OrientFile(v, g)
	protected def baseCondition: Option[QueryCondition] = Some(PropertyIsQueryCondition("type", "file"))
}

