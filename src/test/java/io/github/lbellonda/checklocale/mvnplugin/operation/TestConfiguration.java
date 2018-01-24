/*
 * Copyright 2018 Luca Bellonda.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;

import io.github.lbellonda.checklocale.mvnplugin.CPMojo;
import io.github.lbellonda.checklocale.mvnplugin.operation.classes.TestCPMojoConfig;
import junit.framework.TestCase;

public class TestConfiguration extends TestCase {

	private TestCPMojoConfig doSetUp() {
		TestCPMojoConfig mojo = new TestCPMojoConfig();
		mojo.setupDefaultParameters(".");
		return mojo;
	}

	private void compareCollections(Collection<String> a, Collection<String> b) {
		if ((a == null) && (b == null)) {
			return;
		}
		assertTrue((a != null) && (b != null));
		assertEquals(a.size(), b.size());
		Object[] ao = a.toArray();
		Object[] bo = b.toArray();
		for (int i = 0; i < a.size(); i++) {
			assertEquals(ao[i], bo[i]);
		}
	}

	private List<String> makeList(String[] values) {
		List<String> list = new ArrayList<String>();
		if (null != values) {
			for (String value : values) {
				list.add(value);
			}
		}
		return list;
	}

	public void testDefaultParameters() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		Configuration configuration = mojo.execSetup();
		assertEquals(true, configuration.isErrors());
		assertEquals("UTF-8", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(false, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("", configuration.getBaseLocale());
		assertEquals(false, configuration.isPreventOutput());
		assertEquals(false, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

	public void testEncoding() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgEncoding();
		Configuration configuration = mojo.execSetup();
		assertEquals(true, configuration.isErrors());
		assertEquals("ISO-8859-1", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(false, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("", configuration.getBaseLocale());
		assertEquals(false, configuration.isPreventOutput());
		assertEquals(false, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

	public void testBaseDir() {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgBaseDir();
		try {
			mojo.execSetup();
			fail();
		} catch (MojoFailureException e) {
			if (!"Invalid project directory".equals(e.getMessage())) {
				fail();
			}
		}
	}

	public void testDirectories() throws Exception {
		{
			TestCPMojoConfig mojo = doSetUp();
			mojo.setupCfgDirs1();
			Configuration configuration = mojo.execSetup();
			assertEquals(true, configuration.isErrors());
			assertEquals("UTF-8", configuration.getEncoding());
			assertEquals(".", configuration.getBaseDir());
			assertEquals(false, configuration.isFileNameContainsLocaleCode());
			assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
			assertEquals("", configuration.getBaseLocale());
			assertEquals(false, configuration.isPreventOutput());
			assertEquals(false, configuration.isStrict());
			compareCollections(configuration.getDirectories(),
					makeList(new String[] { "locale1", "locale2", "locale3" }));
		}
		{
			TestCPMojoConfig mojo = doSetUp();
			mojo.setupCfgDirs2();
			try {
				mojo.execSetup();
				fail();
			} catch (MojoFailureException e) {
				if (!"no folders to check".equals(e.getMessage())) {
					fail();
				}
			}
		}
	}

	public void testPreventOutput() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgPreventOutput();
		Configuration configuration = mojo.execSetup();
		assertEquals(true, configuration.isErrors());
		assertEquals("UTF-8", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(false, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("", configuration.getBaseLocale());
		assertEquals(true, configuration.isPreventOutput());
		assertEquals(false, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

	public void testFileNameContainsLocaleCode() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgFileNameContainsLocaleCode();
		Configuration configuration = mojo.execSetup();
		assertEquals(true, configuration.isErrors());
		assertEquals("UTF-8", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(true, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("", configuration.getBaseLocale());
		assertEquals(false, configuration.isPreventOutput());
		assertEquals(false, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

	public void testBaseLocale() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgBaseLocale();
		Configuration configuration = mojo.execSetup();
		assertEquals(true, configuration.isErrors());
		assertEquals("UTF-8", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(false, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("es_ES", configuration.getBaseLocale());
		assertEquals(false, configuration.isPreventOutput());
		assertEquals(false, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

	public void testErrors() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgErrors();
		Configuration configuration = mojo.execSetup();
		assertEquals(false, configuration.isErrors());
		assertEquals("UTF-8", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(false, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("", configuration.getBaseLocale());
		assertEquals(false, configuration.isPreventOutput());
		assertEquals(false, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

	public void testOutputFolder() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgOutputDir();
		try {
			mojo.execSetup();
			fail();
		} catch (MojoFailureException e) {
			if (!"Invalid build directory".equals(e.getMessage())) {
				fail();
			}
		}
	}
	
	public void testStrict() throws Exception {
		TestCPMojoConfig mojo = doSetUp();
		mojo.setupCfgStrict();
		Configuration configuration = mojo.execSetup();
		assertEquals(true, configuration.isErrors());
		assertEquals("UTF-8", configuration.getEncoding());
		assertEquals(".", configuration.getBaseDir());
		assertEquals(false, configuration.isFileNameContainsLocaleCode());
		assertEquals("./target/" + CPMojo.DEFAULT_FOLDER, configuration.getOutputFolder());
		assertEquals("", configuration.getBaseLocale());
		assertEquals(false, configuration.isPreventOutput());
		assertEquals(true, configuration.isStrict());
		compareCollections(configuration.getDirectories(), makeList(new String[] { "locale" }));
	}

}
