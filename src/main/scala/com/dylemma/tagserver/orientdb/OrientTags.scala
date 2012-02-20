package com.dylemma.tagserver.orientdb

import com.dylemma.tagserver.orientdb.orm._
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import com.tinkerpop.blueprints.pgm.Vertex

object OrientTags extends QueryableVertexGroup[OrientTag] {

	val name = QueryableProperty[String]("tagName")

	protected def upgradeVertex(v: Vertex, g: OrientGraph) = new OrientTag(v, g)
	protected def baseCondition: Option[QueryCondition] = Some(PropertyIsQueryCondition("type", "tag"))
}