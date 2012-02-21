package com.dylemma.tagserver.fileinfo

import java.io._
import java.security.MessageDigest
import java.security.DigestInputStream
import org.apache.commons.io.input.AutoCloseInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.commons.io.IOUtils

object MD5 {

	/** Calculates the MD5 digest for the given `file`.
	  * @param file The file to be digested. If the `file` is a directory or other non-file, this method will fail
	  * @return the MD5 digest for the given `file`, expressed as a hex string
	  */
	def apply(file: File) = {
		val md = MessageDigest.getInstance("MD5")
		md.reset
		val fin = new FileInputStream(file)

		//this input stream will forward data to the `md`, and close at the end of input
		val in = new AutoCloseInputStream(new DigestInputStream(fin, md))

		//create an output source that doesn't do anything, then copy all bytes to it
		// (this will send all data from `fin` to `md`)
		val out = NullOutputStream.NULL_OUTPUT_STREAM
		IOUtils.copyLarge(in, out)

		md.digest.map("%02x".format(_)).mkString
	}
}