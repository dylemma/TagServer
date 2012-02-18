package com.dylemma.tagserver.orientdb.orm

import com.tinkerpop.blueprints.pgm.Vertex

/** @author dylanh
  *
  */
trait VertexProxy {
	def asVertex: Vertex
}