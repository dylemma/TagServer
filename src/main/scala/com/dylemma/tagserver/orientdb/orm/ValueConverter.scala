package com.dylemma.tagserver.orientdb.orm

/** Handles conversion of a particular value type, to and from `String`
  * @author dylanh
  * @tparam T the value type
  */
trait ValueConverter[T] {
	def apply(value: T): String
	def unapply(raw: String): Option[T]
}