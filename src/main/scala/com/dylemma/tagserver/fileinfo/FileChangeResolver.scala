package com.dylemma.tagserver.fileinfo

import scala.annotation.tailrec

class FileChangeResolver {

	private var changes: List[FileChangeStatus] = Nil

	private def addChange(change: FileChangeStatus) = changes = change :: changes

	/** Resolve the given list of naive `statuses` into a list of interpreted changes.
	  * @param statuses a list of file change observations
	  * @return a list of "smarter" file change events. For example, if a new file and
	  * missing file that both have the same md5 hash value, the pair is interpreted as
	  * a "moved" event.
	  */
	def resolve(statuses: List[FileChangeObservation]) = {
		changes = Nil
		interpret(statuses)
		val results = changes
		changes = Nil
		results
	}

	@tailrec
	private def interpret(statuses: List[FileChangeObservation]): Unit = statuses match {
		case Nil =>
		case status :: theRest =>
			status match {
				case m @ Missing(_) => interpret(handleMissing(m, theRest))
				case n @ NewFile(_, _) => interpret(handleNewFile(n, theRest))
				case d @ NewDir(_) => interpret(handleNewDir(d, theRest))
				case m @ Modified(_, _, _) => interpret(handleModified(m, theRest))
			}

	}
	private def handleMissing(m: Missing, statuses: List[FileChangeObservation]) = {
		//a file is missing. see if it was moved somewhere (the md5 of the new file will match m's)
		val missingHash = m.oFile.md5
		statuses.collectFirst {
			case n @ NewFile(_, md5) if (md5 == missingHash) => n
			//case m @ Modified(file, newHash, oldHash) if (oldHash == missingHash) => m
		} match {
			case None =>
				//the missing file is missing because it was simply removed
				addChange(Deleted(m.oFile))
				statuses
			case Some(s) =>
				//the missing file was moved/renamed to `s.file`
				addChange(Moved(m.oFile, s.file))
				//pass along the `statuses` list, without the `NewFile` we found
				statuses.filterNot(_ == s)
		}
	}

	private def handleNewFile(n: NewFile, statuses: List[FileChangeObservation]) = {
		//a file has been created. see if it was moved from somewhere else
		val hash = MD5(n.file)
		statuses.collectFirst {
			case m @ Missing(oFile) if (oFile.md5 == hash) => m
		} match {
			case None =>
				//there is no missing file with a matching md5, so it's just new
				addChange(Created(n.file))
				statuses
			case Some(m) =>
				//found a missing file with the same hash. it was probably moved
				addChange(Moved(m.oFile, n.file))
				//pass along the `statuses` list, without the `Missing` we found
				statuses.filterNot(_ == m)
		}
	}

	private def handleNewDir(m: NewDir, statuses: List[FileChangeObservation]) = {
		addChange(Created(m.dir))
		statuses
	}

	private def handleModified(m: Modified, statuses: List[FileChangeObservation]) = {
		addChange(Changed(m.file))
		statuses
	}

}