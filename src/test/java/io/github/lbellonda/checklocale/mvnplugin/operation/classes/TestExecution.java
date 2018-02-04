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

package io.github.lbellonda.checklocale.mvnplugin.operation.classes;

import java.util.List;

import io.github.lbellonda.checklocale.mvnplugin.operation.DirInfo;
import io.github.lbellonda.checklocale.mvnplugin.operation.Execution;
import io.github.lbellonda.checklocale.mvnplugin.operation.PropInfo;
import io.github.lbellonda.checklocale.mvnplugin.operation.ReadInfoResult;
import io.github.lbellonda.checklocale.mvnplugin.operation.SingleExecution;
import io.github.lbellonda.checklocale.mvnplugin.operation.errors.PError;

public class TestExecution extends Execution {

	public List<PError> testCheckFileExsistence(List<DirInfo> dirs) {
		return checkFileExsistence(dirs);
	}

	public String testExtractKeyLax(final String input) {
		return testExtractKey(input, false);
	}

	public String testExtractKeyStrict(final String input) {
		return testExtractKey(input, true);
	}

	public String testExtractKey(final String input, final boolean strict) {
		ReadInfoResult result = extractKey(input, strict);
		if ((null != result) && (null != result.getPropInfo()) && !result.isError()) {
			PropInfo info = result.getPropInfo();
			if (null != info) {
				return info.getKey();
			}
		}
		return null;
	}

	public boolean testExtractKeyStrictError(final String input) {
		ReadInfoResult result = extractKey(input, true);
		if ((null != result) && result.isError()) {
			return true;
		}
		return false;
	}

	public List<PError> testDuplicate(List<DirInfo> dirs) {
		return checkDuplicateItems(dirs);
	}

	public List<PError> testMissingKey(SingleExecution execution, List<DirInfo> dirs) {
		return checkMissingItems(execution, dirs);
	}

	public String testRewriteValues(String input) {
		ReadInfoResult result = extractKey(input, false);
		if ((null != result) && (null != result.getPropInfo()) && !result.isError()) {
			StringBuilder sb = new StringBuilder();
			if(result.getPropInfo().isValue()) {
				result.getPropInfo().writeToString(sb);
				return sb.toString();
			}
		}
		return null;
	}
	
	public String testRewrite(String input) {
		ReadInfoResult result = extractKey(input, false);
		if ((null != result) && (null != result.getPropInfo()) && !result.isError()) {
			StringBuilder sb = new StringBuilder();
			result.getPropInfo().writeToString(sb);
			return sb.toString();
		}
		return null;
	}
	
	public PropInfo testPropInfo(String input) {
		ReadInfoResult result = extractKey(input, false);
		if ((null != result) && (null != result.getPropInfo()) && !result.isError()) {
			return result.getPropInfo();
		}
		return null;
	}
}
