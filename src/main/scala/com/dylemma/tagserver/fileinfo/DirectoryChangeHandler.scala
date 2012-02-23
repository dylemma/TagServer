package com.dylemma.tagserver.fileinfo

import org.apache.commons.io.FilenameUtils
import java.io.File
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph
import com.dylemma.tagserver.orientdb.OrientFile
import com.dylemma.tagserver.orientdb.OrientFiles
import DBFile._

/** Handles changes to a directory structure and persists them to the OrientDB Graph.
  */
class DirectoryChangeHandler(implicit graph: OrientGraph) {

	/** Given a list of changes to files (additions, deletions, modifications, and moves),
	  * perform the necessary actions to persist the state to the database.
	  */
	def handle(changes: List[FileChangeStatus]): Unit = {
		handleDeletions(changes)
		handleAdditions(changes)
		handleMoves(changes)
		handleModifications(changes)
	}

	private def handleDeletions(changes: List[FileChangeStatus]): Unit = {
		changes.collect {
			case d @ Deleted(oFile) => oFile.delete
		}
	}

	private def handleAdditions(changes: List[FileChangeStatus]): Unit = {
		val additions = changes.collect { case a @ Created(_) => a }

		// Create DB entries for each addition
		val addedEntriesByPath = additions.map { add =>
			val oFile = new OrientFile(graph)
			oFile.name = nameFor(add.file)
			oFile.path = pathFor(add.file)
			if (add.file isFile)
				oFile.md5 = MD5(add.file)
			oFile.path -> oFile
		}.toMap

		//assign parent links for all of the new additions
		for ((path, entry) <- addedEntriesByPath) {
			val parentPath = pathFor(new File(entry.path).getParentFile)
			val parentEntry = addedEntriesByPath.get(parentPath)
			parentEntry match {
				case Some(parent) => entry.parent = parent
				case None =>
					// check if the db has the right entry, otherwise just let it be
					for (f <- OrientFiles if f.path is parentPath) {
						entry.parent = f
					}
			}
		}

		addedEntriesByPath foreach println _
	}

	private def handleMoves(changes: List[FileChangeStatus]): Unit = {
		changes.collect {
			case Moved(source, target) =>
				val path = pathFor(target)
				//assume that the MD5 has not changed
				val parentPath = pathFor(new File(path).getParentFile)
				val parent = OrientFiles.withFilter(_.path is parentPath).headOption
				source.parent = parent
				source.path = path
				source.name = nameFor(target)
		}
	}

	private def handleModifications(changes: List[FileChangeStatus]): Unit = {
		changes.collect {
			case Changed(f) =>
				val path = pathFor(f)
				val md5 = md5For(f)
				for (f <- OrientFiles if f.path is path) f.md5 = md5
		}
	}
}