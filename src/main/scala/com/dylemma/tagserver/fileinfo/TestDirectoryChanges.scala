package com.dylemma.tagserver.fileinfo

import com.dylemma.tagserver.orientdb._
import java.io.File
import org.apache.commons.io.{ FilenameUtils, FileUtils }
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import org.apache.commons.io.monitor.FileAlterationObserver
import org.apache.commons.io.monitor.FileAlterationMonitor

object TestDirectoryChanges {

	def addNewRootDir(file: File)(implicit graph: OrientGraph) = {
		val root = OrientFileRoot()
		val ofile = new OrientFile(graph)
		ofile.name = DBFile.nameFor(file)
		ofile.path = DBFile.pathFor(file)
		root.members += ofile
	}

	def prettyPrint(file: OrientFile, indent: String = ""): Unit = {
		println(indent + file)
		for (child <- file.children) {
			prettyPrint(child, "  " + indent)
		}
	}

	def main(args: Array[String]): Unit = {
		val test = new File("D:/Users/Dylan/Desktop/test")
		try {
			OrientDB.withGraph { implicit graph =>
				//				graph.clear
				//
				//				addNewRootDir(test)
				//				addNewRootDir(new File(test.getParentFile, "test2"))

				val fileRoot = OrientFileRoot()

				val observers = for (oRoot <- fileRoot.members) yield {
					val rootFile = new File(oRoot.path)

					val statuses = DirectoryChangeFinder.getChanges(rootFile, oRoot)
					val changes = FileChangeResolver.resolve(statuses)

					for (s <- changes) println(s)

					val changeHandler = new DirectoryChangeHandler
					changeHandler.handle(changes)

					//					println("-=-=-=-=-=-")
					//					prettyPrint(oRoot)
					//					println("-=-=-=-=-=-")

					val observer = new FileAlterationObserver(oRoot.path)
					observer.addListener(new FileChangeListener)
					println("[Create an observer for changes in " + oRoot + "]")
					observer
				}

				println("Offline changes have been handled. Switching to online mode.")

				val monitor = new FileAlterationMonitor(5000, observers.toList: _*)
				monitor.start

				println("(monitor started. enter some text to stop)")
				readLine
				monitor.stop
				println("Goodbye.")

			}
		} finally {
			OrientDB.unload
		}
	}

}