package com.dylemma.tagserver.fileinfo

import java.io.File
import org.apache.commons.io.monitor.{ FileAlterationListener, FileAlterationObserver }
import com.dylemma.tagserver.orientdb.OrientFiles
import com.dylemma.tagserver.orientdb.OrientDB

class FileChangeListener extends FileAlterationListener {

	private var observations: List[FileChangeObservation] = Nil
	private def add(obs: FileChangeObservation) = observations = obs :: observations

	def onStart(arg0: FileAlterationObserver): Unit = {
		observations = Nil
	}

	def onDirectoryCreate(dir: File): Unit = {
		add(NewDir(dir))
	}

	def onDirectoryChange(dir: File): Unit = {}

	def onDirectoryDelete(dir: File): Unit = OrientDB.withGraph { implicit graph =>
		val path = DBFile.pathFor(dir)
		for (oFile <- OrientFiles if oFile.path is path)
			add(Missing(oFile))
	}

	def onFileCreate(file: File): Unit = {
		add(NewFile(file, DBFile.md5For(file)))
	}

	def onFileChange(file: File): Unit = OrientDB.withGraph { implicit graph =>
		val path = DBFile.pathFor(file)
		for (oFile <- OrientFiles if oFile.path is path) {
			val oldHash = oFile.md5
			val newHash = DBFile.md5For(file)
			add(Modified(file, newHash, oldHash))
		}
	}

	def onFileDelete(file: File): Unit = OrientDB.withGraph { implicit graph =>
		val path = DBFile.pathFor(file)
		for (oFile <- OrientFiles if oFile.path is path)
			add(Missing(oFile))
	}

	def onStop(arg0: FileAlterationObserver): Unit = OrientDB.withGraph { implicit graph =>
		val changes = new FileChangeResolver().resolve(observations)
		changes foreach println _
		observations = Nil
		val changeHandler = new DirectoryChangeHandler
		changeHandler.handle(changes)
	}

}