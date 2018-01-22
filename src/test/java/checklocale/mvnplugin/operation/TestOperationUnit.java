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

package checklocale.mvnplugin.operation;

import checklocale.mvnplugin.operation.classes.TestExecution;
import junit.framework.TestCase;

public class TestOperationUnit extends TestCase {

	public void testParsing() {
		TestExecution op = new TestExecution();
		assertEquals(null, op.testExtractKey(null));
		assertEquals(null, op.testExtractKey(""));
		assertEquals(null, op.testExtractKey("="));
		assertEquals("1", op.testExtractKey("1="));
		assertEquals("1", op.testExtractKey("1=2"));
		assertEquals("abc.gg", op.testExtractKey("abc.gg = 2jdjd"));
		assertEquals("abc.gg", op.testExtractKey(" abc.gg = 2jdjd"));
		assertEquals(null, op.testExtractKey("#"));
		assertEquals(null, op.testExtractKey("!"));
		assertEquals(null, op.testExtractKey("# asda=as"));
		assertEquals(null, op.testExtractKey("! asda=as"));
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
