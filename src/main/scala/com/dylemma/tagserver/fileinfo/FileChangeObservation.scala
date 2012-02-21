package com.dylemma.tagserver.fileinfo

import java.io.File
import com.dylemma.tagserver.orientdb.OrientFile

/** A naive observation about a change of a file */
sealed trait FileChangeObservation

/** Represents the fact that the `file` has changed.
  * @param file The file that changed
  * @param newMD5 the file's new MD5 value
  * @param oldMD5 the file's old MD5 value
  */
case class Modified(file: File, newMD5: String, oldMD5: String) extends FileChangeObservation

/** Represents the fact that the `file` appears to be new
  * @param file The new file
  * @param md5 the file's MD5 value
  */
case class NewFile(file: File, md5: String) extends FileChangeObservation

/** Represents the fact that the `dir`ectory appears to be new
  * @param dir the new directory
  */
case class NewDir(dir: File) extends FileChangeObservation

/** Represents the fact that a file appears to be missing.
  * @param oFile the OrientDB representation of the file
  */
case class Missing(oFile: OrientFile) extends FileChangeObservation
