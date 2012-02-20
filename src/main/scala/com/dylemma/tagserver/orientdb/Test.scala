package com.dylemma.tagserver.orientdb

import orm._
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientVertex

object Test {

	def makeExampleGraph(graph: OrientGraph): Unit = graph.withTransaction {
		val file1 = new OrientFile(graph)
		file1.name = "file1.txt"
		file1.path = "somefolder/file1.txt"

		val file2 = new OrientFile(graph)
		file2.name = "somefolder"
		file2.path = "somefolder"
		file2.children += file1

		//file2 will be the "root" of the "files" graph
		graph.getRawGraph.setRoot("files", file2.asVertex.asInstanceOf[OrientVertex].getRawElement)

		val file3 = new OrientFile(graph)
		file3.name = "file3.txt"
		file3.path = "somefolder/file3.txt"
		file3.parent = file2

		val tag1 = new OrientTag(graph)
		tag1.name = "Awesome!"

		val tag2 = new OrientTag(graph)
		tag2.name = "Cool"

		val tag3 = new OrientTag(graph)
		tag3.name = "Lame"

		file1.tags += tag1 += tag2

		file3.tags += tag3
	}

	def readTags(graph: OrientGraph) = {
		val rootVertex = graph.getVertex(graph.getRawGraph.getRoot("files").getIdentity)
		val rootFile = new OrientFile(rootVertex, graph)

		def printTags(file: OrientFile, indent: String): Unit = {
			val tags = file.tags.map(_.name).mkString("[", ", ", "]")
			println(indent + file.name + " -> " + tags)
			for (child <- file.children) {
				printTags(child, indent + "  ")
			}
		}

		printTags(rootFile, "")
	}

	def main(argsArray: Array[String]): Unit = {
		val args = List("read") //argsArray.toList

		OrientDB.withGraph { graph =>
			println("Hey, the first graph: " + graph)
			args match {
				case List("create") =>
					println("Clearing graph to start fresh")
					graph.clear
					makeExampleGraph(graph)
				case List("read") =>
					println("Checking out the results")
					readTags(graph)
				case _ => println("Usage: [create|read]")
			}
		}

		withGraph("local:D:/Temp/OrientDBTest") { graph =>
			println("Hey another graph: " + graph)
		}

		println("Done")
	}
}