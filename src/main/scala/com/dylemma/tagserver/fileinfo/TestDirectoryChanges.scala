package com.dylemma.tagserver.fileinfo

import com.dylemma.tagserver.orientdb._
import java.io.File
import org.apache.commons.io.{ FilenameUtils, FileUtils }
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph

object TestDirectoryChanges {

	def main(args: Array[String]): Unit = {
		val test = new File("D:/Users/Dylan/Desktop/test")
		try {
			OrientDB.withGraph { implicit graph =>
				//				graph.clear
				//				indexFile(test)
				//
				//				for (f <- OrientFiles) println(f)

				val oRoot = OrientFiles.withFilter(_.path is DBFile.pathFor(test))
				//			compareDirContents(test, oRoot.head)

				val indexer = new DirectoryChangeFinder
				val statuses = indexer.getChanges(test, oRoot.head)
				val changes = new FileChangeResolver().resolve(statuses)
				println("\n=========================\n")
				for (s <- changes) println(s)
				val changeHandler = new DirectoryChangeHandler
				changeHandler.handle(changes)
			}
		} finally {
			OrientDB.unload
		}
	}

}