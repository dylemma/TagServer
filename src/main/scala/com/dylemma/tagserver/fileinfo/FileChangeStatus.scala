package com.dylemma.tagserver.fileinfo

import java.io.File
import com.dylemma.tagserver.orientdb.OrientFile

/** Describes the change in state of a file */
trait FileChangeStatus

/** Represents the fact that a file has been newly created
  * @param file the new file
  */
case class Created(file: File) extends FileChangeStatus

/** Represents the fact that a file has been deleted
  * @param oFile the OrientDB representation of the file before it had been removed
  */
case class Deleted(oFile: OrientFile) extends FileChangeStatus

/** Represents the fact that a file has been moved
  * @param source the OrientDB representation of the file before it was moved
  * @param target the file's new location
  */
case class Moved(source: OrientFile, target: File) extends FileChangeStatus

/** Represents the fact that a file has been modified
  * @param file the file that was modified
  */
case class Changed(file: File) extends FileChangeStatus