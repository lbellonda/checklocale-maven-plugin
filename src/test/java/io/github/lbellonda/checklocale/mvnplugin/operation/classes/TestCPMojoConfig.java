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

package io.github.lbellonda.checklocale.mvnplugin.operation.classes;

public class TestCPMojoConfig extends TestCPMojo {

	public void setupCfgEncoding() {
		encoding = "ISO-8859-1";
	}

	public void setupCfgBaseDir() {
		projectBaseDir = null;
	}

	public void setupCfgDirs1() {
		directories = new String[] { "locale1", "locale2", "locale3" };
	}

	public void setupCfgDirs2() {
		directories = null;
	}

	public void setupCfgPreventOutput() {
		preventOutput = true;
	}

	public void setupCfgFileNameContainsLocaleCode() {
		fileNameContainsLocaleCode = true;
	}

	public void setupCfgBaseLocale() {
		baseLocale = "es_ES";
	}

	public void setupCfgErrors() {
		errors = false;
	}

	public void setupCfgOutputDir() {
		targetBaseDir = null;
	}

	public void setupCfgStrict() {
		strict = true;
	}

	public void setupCfgSkipComments() {
		skipComments = true;
	}
}
