package com.javaranch.forums.dibs.persistence.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for YAML text
 * 
 * @author timh
 * @since Mar 13, 2015
 * @TestWith CrudeYAMLParserTest
 */
public class CrudeYAMLParser {
	// ===
	final private static Pattern ITEM_LINE_PAT = Pattern
			.compile("\\s*\\-.*");

	final private static Pattern ITEM_PAT = Pattern
			.compile("\\s*\\-?\\s+([^\\:]+)(?:(\\:\\s*)(.*))?");
	
	
	/**
	 * Extract value or name/value from a YAML-style item line.
	 * 
	 * @param string
	 *            line to parse
	 * @return 2-element String array. 2nd element is value if
	 *         present otherwise <code>null</code>.
	 */
	public static String[] extractItem(String string) {
		String s[] = new String[2];
		Matcher m = ITEM_PAT.matcher(string);
		@SuppressWarnings("unused")
		boolean ok = m.matches();
		//System.out.println("MATCH=" + ok);
		try {
			s[0] = m.group(1);
		} catch (Throwable e) {
			throw new RuntimeException("Match failed: >"+ string + "<");
		}
		@SuppressWarnings("unused")
		int gc = m.groupCount();
		//System.out.println("GC=" + gc + " " + string);
		if (m.groupCount() > 2) { // not just 0 and 1
			String s0 = m.group(2); // The ":" part
			//System.out.println("G2=" + s0);
			String s1 = m.group(3);
			//System.out.println("G3=" + s1);

			if (s0 != null) {
				s[1] = s1.trim();
			}
		}
		System.out.println("s0=" + s[0] + ", s1=" + s[1]);
		return s;
	}

	/**
	 * Test line to see if it's a top-level item or a YAML sub-item.
	 * 
	 * @param line Line to test
	 * @return <code>true</code> if this is a top-level item, indicated
	 * by leading "-".
	 */
	public static boolean isItemLine(String line) {
		Matcher m = ITEM_LINE_PAT.matcher(line);
		return m.matches();
	}
}
