package com.dylemma.tagserver.orientdb.orm

import com.tinkerpop.blueprints.pgm.Vertex

/** @author dylanh
  *
  */
class VertexProperty[T](v: Vertex, prop: String, default: T = null)(implicit vc: ValueConverter[T]) {
	def apply(): T = v.getProperty(prop) match {
		case `vc`(value) => value
		case _ => default
	}

	def update(newValue: T): Unit = v.setProperty(prop, vc(newValue))
}