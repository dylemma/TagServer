package com.dylemma.tagserver.fileinfo

import java.io.File
import org.apache.commons.io.FilenameUtils

/** Provides a central access point for convenience methods,
  * used for storing information about Files in a database
  */
object DBFile {
	def nameFor(file: File) = file.getName
	def pathFor(file: File) = FilenameUtils.separatorsToUnix(file.getCanonicalPath)
	def md5For(file: File) = MD5(file)
}