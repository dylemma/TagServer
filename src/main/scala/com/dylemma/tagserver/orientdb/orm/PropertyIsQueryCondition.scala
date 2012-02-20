package com.dylemma.tagserver.orientdb.orm

case class PropertyIsQueryCondition(prop: String, value: String) extends QueryCondition {
	def toQueryString = "%s = '%s'".format(prop, value)
}