/*
 * Copyright 2017 Luca Bellonda.
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

import checklocale.mvnplugin.operation.DirInfo;
import checklocale.mvnplugin.operation.Execution;
import checklocale.mvnplugin.operation.errors.PError;

public class TestExecution extends Execution {

	public List<PError> testCheckFileExsistence(List<DirInfo> dirs) {
		return checkFileExsistence(dirs);
	}

	public String testExtractKey(final String input) {
		return extractKey(input);
	}

	public List<PError> testDuplicate(List<DirInfo> dirs) {
		return checkDuplicateItems(dirs);
	}

	public List<PError> testMissingKey(List<DirInfo> dirs) {
		return checkMissingItems(dirs);
	}
}
