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

package checklocale.mvnplugin.operation.classes;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import checklocale.mvnplugin.CPMojo;
import checklocale.mvnplugin.operation.Configuration;
import checklocale.mvnplugin.operation.Execution;
import checklocale.mvnplugin.operation.TestEndToEnd;
import checklocale.mvnplugin.operation.errors.PError;

public class TestCPMojo extends CPMojo {

	public TestCPMojo() {
		setUp();
	}

	private void setUp() {
		fileNameContainsLocaleCode = false;
		errors = true;
		encoding = DEFAULT_ENCODING;
		baseLocale = "en_US";
		outputFolder = null;
		projectBaseDir = TestEndToEnd.BASE_SRC;
		targetBaseDir = TestEndToEnd.BASE_OUTPUT;
		preventOutput = false;
	}

	public List<PError> testExecute() throws MojoExecutionException, MojoFailureException {
		Configuration configuration = setup();
		Execution execution = newExecution();
		List<PError> errorsList = execution.execute(configuration);
		return errorsList;
	}

	@Override
	protected Execution newExecution() {
		Execution execution = new TestTokenizedExecution();
		return execution;
	}

	public void setFolders(String sourceFolder, String destinationFolder) {
		outputFolder = destinationFolder;
		directories = new String[] { sourceFolder };
	}

	public void setPreMethodEndToEnd(String caseNo) {
		if ("1".equals(caseNo)) {
			preventOutput = true;
		}
		if ("6".equals(caseNo)) {
			fileNameContainsLocaleCode = true;
		}
		if ("2".equals(caseNo)) {
			encoding = "ISO-8859-1";
		}
	}

	public void setupDefaultParameters(final String baseDir) {
		errors = true;
		encoding = null;
		projectBaseDir = baseDir;
		fileNameContainsLocaleCode = false;
		outputFolder = null;
		baseLocale = null;
		targetBaseDir = baseDir + "/target";
		preventOutput = false;
		directories = new String[] { "locale" };
	}

	public Configuration execSetup() throws MojoFailureException {
		return setup();
	}

}
