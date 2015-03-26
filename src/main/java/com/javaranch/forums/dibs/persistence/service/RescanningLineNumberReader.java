package com.javaranch.forums.dibs.persistence.service;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LineNumberReader with the following properties:
 * 
 * <ol>
 * <li>Rescan function to cause re-read of last line read.</li>
 * <li>Unix-style Comments removed from text read.</li>
 * <li>Blank/comment lines are ignored and not returned.</li>
 * </ol>
 * @author timh
 * @since Mar 13, 2015
 * @TestWith RescanningLineNumberReaderTest
 */
public class RescanningLineNumberReader extends LineNumberReader {

	private String rescanLine;
	private boolean doRescan = false;

	final static Pattern COMMENT_PAT = Pattern
			.compile("([^#]*)\\#?.*");
	private boolean atEOF = false;

	public RescanningLineNumberReader(Reader in) {
		super(in);
	}

	/**
	 * Read line with possible rescan. If not rescan, read and
	 *  hold line, else use held line and reset rescan.
	 *  
	 *  Blank/all-comment lines are ignored.
	 *  
	 *  @return line read/rescanned or <code>null</code> if EOF.
	 */
	@Override
	public String readLine() throws IOException {
		if (!doRescan) {
			String ln = scanLine();
			if ( ln == null ) {
				this.atEOF = true;
				return null;
			} else {
				this.rescanLine = ln;
			}
		}
		this.doRescan = false;
		return this.rescanLine;
	}

	/**
	 * Read line, discarding comments.
	 * 
	 * @return
	 * @throws IOException
	 */
	String scanLine() throws IOException {
		String line;
		while ((line = super.readLine()) != null) {
			
			//if ( log.isDebugEnabled() ) {
			System.out.println(">>> "+line);
		//}
			
			line = trimComent(line);
			if ( !line.trim().isEmpty()) {
				return line;
			}
		}
		return null;
	}

	/**
	 * Remove Unix-style comment from line, if present.
	 * 
	 * @param line line to process
	 * @return line without comment part.
	 */
	static String trimComent(String string) {
		Matcher m = COMMENT_PAT.matcher(string);
		@SuppressWarnings("unused")
		boolean ok = m.matches();
//		System.out.println("MATCH=" + ok + " >" + string + "<");
		String s = m.group(1);
//		System.out.println("  >"+s+"<");		
		return s;
	}

	public void rescan() {
		if ( ! this.atEOF ) {
			this.doRescan = true;
		}
	}
}
