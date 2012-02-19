package com.dylemma.tagserver.filestuff

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.io.monitor._
import java.io.File
import collection.JavaConversions._

object FileStuff {

	def main(args: Array[String]): Unit = {
		val test = new File("D:/Users/Dylan/Desktop/test")
		//		val itr = FileUtils.iterateFiles(media, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
		//
		//		for (file <- itr) {
		//			println(file)
		//		}

		val dirObserver = new FileAlterationObserver(test)
		val monitor = new FileAlterationMonitor(1000, dirObserver)

		object dirListener extends FileAlterationListener {
			def onDirectoryChange(dir: File) = println("dirChange: " + dir)
			def onDirectoryCreate(dir: File) = println("dirCreate: " + dir)
			def onDirectoryDelete(dir: File) = println("dirDelete: " + dir)

			def onFileChange(file: File) = println("fileChange: " + file)
			def onFileCreate(file: File) = println("fileCreate: " + file)
			def onFileDelete(file: File) = println("fileDelete: " + file)

			def onStart(obs: FileAlterationObserver) = Unit //println("onStart: " + obs)
			def onStop(obs: FileAlterationObserver) = Unit //println("onStop: " + obs)
		}

		dirObserver.addListener(dirListener)

		monitor.start
		println("Started monitor")

		val end = readLine
		monitor.stop
		println("Ended because you typed " + end)
	}

}