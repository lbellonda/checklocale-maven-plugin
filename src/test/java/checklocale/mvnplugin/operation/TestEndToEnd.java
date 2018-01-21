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

package checklocale.mvnplugin.operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import checklocale.mvnplugin.operation.classes.TestCPMojo;
import checklocale.mvnplugin.operation.errors.PError;
import junit.framework.TestCase;

public class TestEndToEnd extends TestCase {

	public final static String BASE_OUTPUT = "target/test/endtoend/";
	public final static String BASE_SRC = "src/test/resources/endToEnd/src/";
	public final static String BASE_REFERENCE = "src/test/resources/endToEnd/reference/";

	private void compareDirectoriesRecursive(File dirReference, File dirCandidate) throws IOException {
		if (null == dirReference) { // git...

		}
		File[] filesReference = dirReference.listFiles();
		File[] filesCandidate = dirCandidate.listFiles();
		if (null == filesReference) { // git...
			filesReference = new File[] {};
		}
		assertEquals("children", filesReference.length, filesCandidate.length);
		for (File fileReference : filesReference) {
			boolean found = false;
			for (File fileCandidate : filesCandidate) {
				if (fileReference.getName().equals(fileCandidate.getName())) {
					found = true;
					assertEquals(fileReference.isDirectory(), fileCandidate.isDirectory());
					if (fileReference.isDirectory()) {
						compareDirectoriesRecursive(fileReference, fileCandidate);
					} else {
						String stringReference = FileUtils.readFileToString(fileReference, "UTF-8");
						String stringCandidate = FileUtils.readFileToString(fileCandidate, "UTF-8");
						assertEquals(fileReference.getName(), stringCandidate, stringReference);
					}
					break;
				}
			}
			assertTrue("found candidate for" + fileReference.getName(), found);
		}
	}

	private void runCase(final String caseNo, boolean expectedErrors) throws Exception {
		final String sourceFolder = "case" + caseNo;
		final String destinationFolder = "case" + caseNo;
		final String fullDestinationFolder = BASE_OUTPUT + "case" + caseNo;
		final String referenceFolder = BASE_REFERENCE + "case" + caseNo;
		File file = new File(fullDestinationFolder);
		file.mkdirs();
		TestCPMojo mojo = new TestCPMojo();
		mojo.setPreMethodEndToEnd(caseNo);
		mojo.setFolders(sourceFolder, destinationFolder);
		List<PError> errors = new ArrayList<PError>();
		errors = mojo.testExecute();
		assertEquals(expectedErrors, !errors.isEmpty());
		compareDirectoriesRecursive(new File(referenceFolder), new File(fullDestinationFolder));
	}

	// regular
	public void testCase0() throws Exception {
		runCase("0", false);
	}

	// no output
	public void testCase1() throws Exception {
		runCase("1", false);
	}

	// encoding
	public void testCase2() throws Exception {
		runCase("2", false);
	}

	// duplicates
	public void testCase3() throws Exception {
		runCase("3", true);
	}

	// missing
	public void testCase4() throws Exception {
		runCase("4", true);
	}

	// duplicates+missing
	public void testCase5() throws Exception {
		runCase("5", true);
	}

	// file name pattern
	public void testCase6() throws Exception {
		runCase("6", false);
	}

	// no localizations
	public void testCase7() throws Exception {
		runCase("7", true);
	}
}
