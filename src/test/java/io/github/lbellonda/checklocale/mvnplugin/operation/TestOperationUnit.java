/*
 * Copyright 2017-2018 Luca Bellonda.
 * 
 * Part of the checklocale project
 * See the NOTICE file distributed with this work for additional information 
 * regarding copyright ownership.
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lbellonda.checklocale.mvnplugin.operation;

import io.github.lbellonda.checklocale.mvnplugin.operation.classes.TestExecution;
import junit.framework.TestCase;

public class TestOperationUnit extends TestCase {

	public void testParsingLax() {
		TestExecution op = new TestExecution();
		assertEquals(null, op.testExtractKeyLax(null));
		assertEquals(null, op.testExtractKeyLax(""));
		assertEquals(null, op.testExtractKeyLax("="));
		assertEquals(null, op.testExtractKeyLax("=1"));
		assertEquals("1", op.testExtractKeyLax("1="));
		assertEquals("1", op.testExtractKeyLax("1=2"));
		assertEquals("abc.gg", op.testExtractKeyLax("abc.gg = 2jdjd"));
		assertEquals("abc.gg", op.testExtractKeyLax(" abc.gg = 2jdjd"));
		assertEquals("abc.gg", op.testExtractKeyLax(" abc.gg 2jdjd"));
		assertEquals("abc.gg", op.testExtractKeyLax(" abc.gg :2jdjd"));
		assertEquals(null, op.testExtractKeyLax("#"));
		assertEquals(null, op.testExtractKeyLax("!"));
		assertEquals(null, op.testExtractKeyLax("# asda=as"));
		assertEquals(null, op.testExtractKeyLax("! asda=as"));
	}
	
	public void testParsingStrict() {
		TestExecution op = new TestExecution();
		assertEquals(null, op.testExtractKeyStrict(null));
		assertEquals(null, op.testExtractKeyStrict(""));
		assertEquals(null, op.testExtractKeyStrict("="));
		assertEquals("1", op.testExtractKeyStrict("1="));
		assertEquals("1", op.testExtractKeyStrict("1=2"));
		assertEquals(null, op.testExtractKeyStrict("=1"));
		assertEquals("abc.gg", op.testExtractKeyStrict("abc.gg = 2jdjd"));
		assertEquals("abc.gg", op.testExtractKeyStrict(" abc.gg = 2jdjd"));
		assertEquals(null, op.testExtractKeyStrict(" abc.gg 2jdjd"));
		assertEquals(null, op.testExtractKeyStrict(" abc.gg :2jdjd"));
		assertEquals(null, op.testExtractKeyStrict("#"));
		assertEquals(null, op.testExtractKeyStrict("!"));
		assertEquals(null, op.testExtractKeyStrict("# asda=as"));
		assertEquals(null, op.testExtractKeyStrict("! asda=as"));
	}

	public void testParsingStrictError() {
		TestExecution op = new TestExecution();
		assertEquals(false, op.testExtractKeyStrictError(null));
		assertEquals(false, op.testExtractKeyStrictError(""));
		assertEquals(true, op.testExtractKeyStrictError("="));
		assertEquals(false, op.testExtractKeyStrictError("1="));
		assertEquals(false, op.testExtractKeyStrictError("1=2"));
		assertEquals(true, op.testExtractKeyStrictError("=1"));
		assertEquals(false, op.testExtractKeyStrictError("abc.gg = 2jdjd"));
		assertEquals(false, op.testExtractKeyStrictError(" abc.gg = 2jdjd"));
		assertEquals(true, op.testExtractKeyStrictError(" abc.gg 2jdjd"));
		assertEquals(true, op.testExtractKeyStrictError(" abc.gg :2jdjd"));
		assertEquals(false, op.testExtractKeyStrictError("#"));
		assertEquals(false, op.testExtractKeyStrictError("!"));
		assertEquals(false, op.testExtractKeyStrictError("# asda=as"));
		assertEquals(false, op.testExtractKeyStrictError("! asda=as"));
	}

	public void testRewrite() {
		TestExecution op = new TestExecution();
		assertEquals(null, op.testRewrite(null));
		assertEquals(null, op.testRewrite(""));
		assertEquals(null, op.testRewrite("!"));
		assertEquals(null, op.testRewrite("#"));
		assertEquals(null, op.testRewrite("# abc =12 3"));
		assertEquals(null, op.testRewrite("! abc =12 3"));
		assertEquals("1=", op.testRewrite("1="));
		assertEquals("1=2", op.testRewrite("1=2"));
		assertEquals("abc.gg= 2jdjd", op.testRewrite("abc.gg = 2jdjd"));
		assertEquals("abc.gg= 2jdjd", op.testRewrite(" abc.gg = 2jdjd"));
		assertEquals("abc.gg= 2 jdjd", op.testRewrite("\ufeffabc.gg = 2 jdjd")); // bom
		assertEquals("abc=12 3", op.testRewrite(" abc 12 3"));
		assertEquals("abc=12 3", op.testRewrite(" abc:12 3"));
		assertEquals(null, op.testRewrite(" abc_12_3"));

	}
}
