package com.dylemma.tagserver.orientdb.orm

trait QueryCondition {
	def toQueryString: String
	def and(r: QueryCondition) = AndQueryCondition(this, r)
	def or(r: QueryCondition) = OrQueryCondition(this, r)
}

final case class AndQueryCondition(left: QueryCondition, right: QueryCondition) extends QueryCondition {
	def toQueryString = "(%s and %s)".format(left.toQueryString, right.toQueryString)
}

final case class OrQueryCondition(left: QueryCondition, right: QueryCondition) extends QueryCondition {
	def toQueryString = "(%s or %s)".format(left.toQueryString, right.toQueryString)
}