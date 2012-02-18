package code.lib
import scala.collection.mutable.ListBuffer

trait LikeTreeOps[A] {
	def parentOf(node: A): A
	def childrenOf(node: A): Seq[A]

	def traverse(root: A, visit: (A) => Unit)(implicit t: TreeTraversal): Unit = traverse(root, visit, None)
	def traverse(root: A, visit: (A) => Unit, maxDepth: Int)(implicit t: TreeTraversal): Unit = traverse(root, visit, Some(maxDepth))
	def traverse(root: A, visit: (A) => Unit, maxDepth: Option[Int])(implicit t: TreeTraversal): Unit = t.traverse(root, this, visit, maxDepth)

	def collect(root: A, maxDepth: Int)(implicit t: TreeTraversal): List[A] = collect(root, Some(maxDepth))
	def collect(root: A, maxDepth: Option[Int])(implicit t: TreeTraversal): List[A] = collect(root, (a: A) => a, maxDepth)
	def collect[B](root: A, f: (A) => B, maxDepth: Int)(implicit t: TreeTraversal): List[B] = collect(root, f, Some(maxDepth))
	def collect[B](root: A, f: (A) => B, maxDepth: Option[Int] = None)(implicit t: TreeTraversal): List[B] = {
		val lb = new ListBuffer[B]()
		def append(item: A) { lb += f(item) }
		traverse(root, append _, maxDepth)
		lb.toList
	}

	def collectFiltered(root: A, accept: (A) => Boolean, maxDepth: Int)(implicit t: TreeTraversal): List[A] = collectFiltered(root, accept, Some(maxDepth))
	def collectFiltered(root: A, accept: (A) => Boolean, maxDepth: Option[Int] = None)(implicit t: TreeTraversal): List[A] = collectFlat(root, { (a: A) => if (accept(a)) List(a) else Nil }, maxDepth)
	def collectFlat[B](root: A, f: (A) => TraversableOnce[B], maxDepth: Int)(implicit t: TreeTraversal): List[B] = collectFlat(root, f, Some(maxDepth))
	def collectFlat[B](root: A, f: (A) => TraversableOnce[B], maxDepth: Option[Int] = None)(implicit t: TreeTraversal): List[B] = {
		val lb = new ListBuffer[B]()
		def append(item: A) { lb ++= f(item) }
		traverse(root, append _, maxDepth)
		lb.toList
	}
}