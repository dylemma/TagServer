//package code.script
//
//import java.io.{ File => JFile }
//import code.model.db._
//import code.lib.FileOps
//import net.liftweb.common.Loggable
//import code.model.mongo.MongoConfig
//import code.model.mongo.MongoFile
//import net.liftweb.json.JsonDSL._
//import org.bson.types.ObjectId
//
//object IndexSharedMedia extends Loggable {
//
//	def main2(args: Array[String]): Unit = database.withTransaction {
//		val sharedRoot = Files.findOrAddOrphan("shared media")
//		val sharedDir = new JFile("F:/home/shared")
//
//		logger.debug("Starting indexing of " + sharedDir)
//		val allPaths = FileOps.getPathsBelow(sharedDir)
//		val counter = Iterator.from(0)
//		val numPaths = allPaths.size
//		logger.debug("Need to ensure " + numPaths + " paths in the database")
//		for (path <- allPaths) {
//			val entry = Files.findOrAddPath(sharedRoot, path)
//			logger.debug("Progress: %6d/%6d\t\tEntry: %s".format(counter.next, numPaths, entry))
//		}
//		logger.debug("Indexing complete")
//		logger.debug("Indexed %d paths".format(allPaths.size))
//	}
//
//	def main(args: Array[String]): Unit = {
//		MongoConfig.init
//
//		val rootDir = new JFile("F:/home/shared")
//		val root = MongoFile.find(MongoFile.name.is("shared media") ~ MongoFile.parentId.is(None)) match {
//			case Some(file) => file
//			case None => {
//				val id = ObjectId.get
//				val file = MongoFile(id, "shared media")
//				file.save
//				file
//			}
//		}
//
//		println("Root file: " + root)
//
//		doThingsWithFile(rootDir, root)
//	}
//
//	private def doThingsWithFile(file: JFile, record: MongoFile): Unit = {
//		import MongoFile._
//		val children = for (child <- FileOps.childrenOf(file)) yield {
//			val childRecord = find((name is child.getName) ~ (parentId is record._id)) match {
//				case Some(rec) => rec
//				case None => {
//					println("Create " + child.getName)
//					val id = ObjectId.get
//					val rec = MongoFile(id, child.getName, Some(record._id))
//					rec.save
//					rec
//				}
//			}
//			doThingsWithFile(child, childRecord)
//			childRecord._id
//		}
//		if (!children.isEmpty) {
//			println(children)
//		}
//		val updated = record.copy(childIds = children.toList)
//		update(id is record._id, updated)
//	}
//
//}