package com.dylemma.tagserver.orientdb.orm

/** Instances of this class may be converted to a QueryCondition by calling any of its methods */
case class QueryableProperty[T](prop: String)(implicit cv: ValueConverter[T]) {
	/** Creates a query condition that checks if this particular `prop` is equal to the given `value` */
	def is(value: T) = PropertyIsQueryCondition(prop, cv(value))
}