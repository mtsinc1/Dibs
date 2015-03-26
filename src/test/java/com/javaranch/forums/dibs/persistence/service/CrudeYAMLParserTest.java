package com.javaranch.forums.dibs.persistence.service;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class CrudeYAMLParserTest {

	@Test
	public void test1() {
		String line;
		String[] line1;
		
		line = " - Fred Smith";
		line1 = CrudeYAMLParser.extractItem(line);
		assertEquals("Fred Smith", line1[0]);
		assertEquals((String)null, line1[1]);
		
		line = " - John Doe: moderator";
		line1 = CrudeYAMLParser.extractItem(line);
		assertEquals("John Doe", line1[0]);
		assertEquals("moderator", line1[1]);

	}

}
