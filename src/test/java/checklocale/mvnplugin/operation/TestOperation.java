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

import java.util.ArrayList;
import java.util.List;

import checklocale.mvnplugin.operation.classes.TestExecution;
import checklocale.mvnplugin.operation.errors.DuplicateKeyError;
import checklocale.mvnplugin.operation.errors.MissingDirError;
import checklocale.mvnplugin.operation.errors.MissingFileError;
import checklocale.mvnplugin.operation.errors.MissingKeyError;
import checklocale.mvnplugin.operation.errors.PError;
import junit.framework.TestCase;

public class TestOperation extends TestCase {

	private DirInfo makeDirInfo(String name) {
		DirInfo dirInfo = new DirInfo();
		dirInfo.setName(name);
		return dirInfo;
	}

	private FileInfo makeFileInfo(DirInfo dirInfo, String name) {
		FileInfo fileInfo = new FileInfo(dirInfo);
		fileInfo.setName(name);
		fileInfo.setToken(name);
		return fileInfo;
	}

	private FileInfo addFile(DirInfo dirInfo, String name) {
		FileInfo fileInfo = makeFileInfo(dirInfo, name);
		dirInfo.addFile(fileInfo);
		return fileInfo;
	}

	public void testMissingFile() {
		DirInfo d1 = makeDirInfo("en_US");
		DirInfo d2 = makeDirInfo("it_IT");
		addFile(d1, "a");
		addFile(d1, "b");
		addFile(d2, "a");
		addFile(d2, "c");
		List<DirInfo> dirs = new ArrayList<DirInfo>();
		dirs.add(d1);
		dirs.add(d2);
		TestExecution ex = new TestExecution();
		List<PError> candidateErrors = ex.testCheckFileExsistence(dirs);
		MissingFileError e1 = new MissingFileError("en_US", "it_IT", "b");
		MissingFileError e2 = new MissingFileError("it_IT", "en_US", "c");
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e1);
		referenceErrors.add(e2);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);
	}

	private void addItemToFile(FileInfo f1a, String key, int lineNo) {
		PropInfo propInfo = FileInfo.newProperty(key, "y", lineNo);
		f1a.addInfo(propInfo);
	}

	public void testDuplicate() {
		DirInfo d1 = makeDirInfo("en_US");
		DirInfo d2 = makeDirInfo("it_IT");
		List<DirInfo> dirs = new ArrayList<DirInfo>();
		dirs.add(d1);
		dirs.add(d2);
		FileInfo f1a = addFile(d1, "a");
		FileInfo f1b = addFile(d1, "b");
		FileInfo f2a = addFile(d2, "a");
		FileInfo f2b = addFile(d2, "b");
		addItemToFile(f1a, "k", 1);
		addItemToFile(f1a, "p", 2);
		addItemToFile(f1b, "s", 1);
		addItemToFile(f1b, "t", 2);
		//
		addItemToFile(f2a, "k", 1);
		addItemToFile(f2a, "p", 2);
		addItemToFile(f2b, "s", 1);
		addItemToFile(f2b, "s", 6);
		addItemToFile(f2b, "t", 2);
		TestExecution ex = new TestExecution();
		List<PError> candidateErrors = ex.testDuplicate(dirs);
		DuplicateKeyError e1 = new DuplicateKeyError("it_IT", "b", "s", 1);
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e1);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);
	}

	public void testMissingKey() {
		Configuration configuration = new Configuration();
		SingleExecution execution = new SingleExecution(configuration);
		DirInfo d1 = makeDirInfo("en_US");
		DirInfo d2 = makeDirInfo("it_IT");
		List<DirInfo> dirs = new ArrayList<DirInfo>();
		dirs.add(d1);
		dirs.add(d2);
		FileInfo f1a = addFile(d1, "a");
		FileInfo f1b = addFile(d1, "b");
		FileInfo f2a = addFile(d2, "a");
		FileInfo f2b = addFile(d2, "b");
		addItemToFile(f1a, "k", 1);
		addItemToFile(f1a, "p", 2);
		addItemToFile(f1b, "s", 1);
		addItemToFile(f1b, "t", 2);
		//
		addItemToFile(f2a, "k", 1);
		addItemToFile(f2a, "p", 2);
		addItemToFile(f2b, "t", 2);
		TestExecution ex = new TestExecution();
		List<PError> candidateErrors = ex.testMissingKey(execution, dirs);
		MissingKeyError e1 = new MissingKeyError("it_IT", "b", "s", 1, "en_US");
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e1);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);
	}

	public static void compareErrors(String message, List<PError> reference, List<PError> candidate) {
		assertEquals(message, reference.size(), candidate.size());
		for (PError e1 : reference) {
			boolean found = false;
			for (PError e2 : candidate) {
				if (e1.compareTo(e2)) {
					found = true;
					break;
				}
			}
			assertTrue(message + " / " + e1.toString(), found);
		}
	}

	private Configuration createConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setPreventOutput(true);
		return configuration;
	}

	public void testReadNonExDir() {
		final String folder = "src/test/resources/unit/nonexistdir";
		Configuration configuration = createConfiguration();
		configuration.addFolder(folder);
		Execution execution = new Execution();
		List<PError> candidateErrors = execution.execute(configuration);
		MissingDirError e1 = new MissingDirError(folder, "not relevant");
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e1);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);

	}

	public void testDuplicateEntry() {
		final String folder = "src/test/resources/unit/duplicated";
		Configuration configuration = createConfiguration();
		configuration.addFolder(folder);
		Execution execution = new Execution();
		List<PError> candidateErrors = execution.execute(configuration);
		DuplicateKeyError e1 = new DuplicateKeyError("en_US", "b.properties", "b1", 2);
		DuplicateKeyError e2 = new DuplicateKeyError("en_US", "a.properties", "a", 1);
		DuplicateKeyError e3 = new DuplicateKeyError("en_US", "a.properties", "b", 2);
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e1);
		referenceErrors.add(e2);
		referenceErrors.add(e3);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);

	}

	public void testMissingEntry() {
		final String folder = "src/test/resources/unit/missing";
		Configuration configuration = createConfiguration();
		configuration.addFolder(folder);
		Execution execution = new Execution();
		List<PError> candidateErrors = execution.execute(configuration);
		MissingKeyError e1 = new MissingKeyError("en_US", "a.properties", "b", 2, "it_IT");
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e1);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);
	}

	public void testOkDirs() {
		final String folder = "src/test/resources/unit/ok";
		Configuration configuration = createConfiguration();
		configuration.addFolder(folder);
		Execution execution = new Execution();
		List<PError> candidateErrors = execution.execute(configuration);
		List<PError> referenceErrors = new ArrayList<PError>();
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);
	}

	public void testMixedEntry() {
		final String folder1 = "src/test/resources/unit/missing";
		final String folder2 = "src/test/resources/unit/duplicated";
		Configuration configuration = createConfiguration();
		configuration.addFolder(folder1);
		configuration.addFolder(folder2);
		Execution execution = new Execution();
		List<PError> candidateErrors = execution.execute(configuration);
		MissingKeyError e0 = new MissingKeyError("en_US", "a.properties", "b", 2, "it_IT");
		DuplicateKeyError e1 = new DuplicateKeyError("en_US", "b.properties", "b1", 2);
		DuplicateKeyError e2 = new DuplicateKeyError("en_US", "a.properties", "a", 1);
		DuplicateKeyError e3 = new DuplicateKeyError("en_US", "a.properties", "b", 2);
		List<PError> referenceErrors = new ArrayList<PError>();
		referenceErrors.add(e0);
		referenceErrors.add(e1);
		referenceErrors.add(e2);
		referenceErrors.add(e3);
		compareErrors("test", referenceErrors, referenceErrors);
		compareErrors("real", referenceErrors, candidateErrors);
	}
}
