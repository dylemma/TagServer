package code.lib

trait TreeTraversal {
	def traverse[A](root: A, ops: LikeTreeOps[A], visit: A => Unit, maxDepth: Option[Int]): Unit
}

object PreOrderTreeTraversal extends TreeTraversal {
	def traverse[A](root: A, ops: LikeTreeOps[A], visit: A => Unit, maxDepth: Option[Int]): Unit = {
		def isWithinDepthLimit(depth: Int) = maxDepth match {
			case Some(max) => depth < max
			case None => true
		}
		def innerTraverse(root: A, depth: Int): Unit = {
			visit(root)
			if (isWithinDepthLimit(depth)) {
				for (child <- ops.childrenOf(root)) innerTraverse(child, depth + 1)
			}
		}
		innerTraverse(root, 0)
	}
}