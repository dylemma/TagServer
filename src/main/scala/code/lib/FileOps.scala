package code.lib

import java.io.{ File => JFile }

object FileOps extends LikeTreeOps[JFile] {

	implicit val preOrderTraversal = PreOrderTreeTraversal

	def parentOf(file: JFile) = file.getParentFile
	def childrenOf(file: JFile) = if (file.isDirectory) file.listFiles.toList else Nil

	def main(args: Array[String]) {
		val root = new JFile("F:/home/shared")
		for (path <- getPathsBelow(root)) println(path)
	}

	def segments(file: JFile, root: JFile): List[String] = {
		def reverseSegments(file: JFile): List[String] = file match {
			case `root` => Nil
			case null => Nil
			case file => file.getName :: reverseSegments(file.getParentFile)
		}
		reverseSegments(file).reverse
	}

	def getPathsBelow(file: JFile, maxDepth: Option[Int] = None) = collect(file, (f: JFile) => segments(f, file), maxDepth)
}