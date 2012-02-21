package com.dylemma.tagserver.fileinfo

import java.io.File
import com.dylemma.tagserver.orientdb.OrientFile

/** An instance of this class can naively analyze the differences between the current
  * state of a directory, and the stored state of the corresponding directory in the database.
  */
class DirectoryChangeFinder {

	private var statuses: List[FileChangeObservation] = Nil

	/** Recursively compares the given `rootDir` and the `oRootDir` for structural changes.
	  * @param rootDir the File object that can be used to get the current state of the directory
	  * @param oRootDir the OrientDB representation of the directory, corresponding to the `rootDir`
	  * @return a list of the observed changes
	  */
	def getChanges(rootDir: File, oRootDir: OrientFile): List[FileChangeObservation] = {
		statuses = Nil
		compareDirContents(rootDir, oRootDir)
		val result = statuses
		statuses = Nil
		result
	}

	/** Compare the contents of the current `dir` with the stored `oDir`, to find new or removed files.
	  * This method assumes the `dir` and `oDir` both represent the same directory.
	  */
	private def compareDirContents(dir: File, oDir: OrientFile): Unit = {
		val dirContents = Option(dir.listFiles).map(_.toList).getOrElse(Nil)
		val oDirContents = oDir.children.toList

		//for anything that currently exists, it is either new, changed, or unchanged
		for (currentFile <- dirContents) {
			//check to see if it is new (non-existing in the oDir)
			val oMatch = oDirContents.find(_.name == currentFile.getName)
			oMatch match {
				case None => handleNew(currentFile)
				case Some(m) => {
					//if there is a match, compare the MD5 values (if it's a plain file)
					if (currentFile isFile) {
						val currentMD5 = MD5(currentFile)
						val oldMD5 = m.md5
						if (oldMD5 != currentMD5) handleModified(currentFile, currentMD5, oldMD5)
					}

					if (currentFile isDirectory) {
						compareDirContents(currentFile, m)
					}
				}
			}
		}

		//for anything that exists in the DB but not anymore, it is deleted
		for (oFile <- oDirContents) {
			val currentFile = dirContents.find(_.getName == oFile.name)
			currentFile match {
				case None => handleMissing(oFile)
				case Some(c) => //don't care, since we checked for changes earlier
			}
		}

	}

	private def handleNew(file: File): Unit = {
		//generate a "new" status for the file
		//if the file is a directory, also generate "new"
		//statuses recursively for each child
		if (file.isFile) {
			val status = NewFile(file, MD5(file))
			statuses = status :: statuses
		}

		if (file.isDirectory) {
			val status = NewDir(file)
			statuses = status :: statuses

			for {
				c <- Option(file.listFiles)
				f <- c
			} handleNew(f)
		}
	}

	private def handleMissing(oFile: OrientFile): Unit = {
		//generate a "deleted" status for the file
		//if the file is a directory, also generate "deleted"
		//statuses recursively for each child
		val status = Missing(oFile)
		statuses = status :: statuses

		for (child <- oFile.children) {
			handleMissing(child)
		}
	}

	private def handleModified(file: File, newHash: String, oldHash: String) = {
		val status = Modified(file, newHash, oldHash)
		statuses = status :: statuses
	}

}