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

package checklocale.mvnplugin.operation.errors;

import java.util.ArrayList;
import java.util.List;

public class DuplicateKeyError extends PError {

	private String folderName = "";
	private String fileName = "";
	private String key = "";
	private int firstDefinition;
	private List<Integer> redefinitions = new ArrayList<Integer>();

	public DuplicateKeyError(String pFolderName, String pFileName, String pKey, int pFirstDefinition) {
		folderName = pFolderName;
		fileName = pFileName;
		key = pKey;
		firstDefinition = pFirstDefinition;
	}

	public void addRefefinitions(List<Integer> pRedefinitions) {
		redefinitions.addAll(pRedefinitions);
	}

	public String toString() {
		return "Error: duplicate key '" + key + "' in file " + fileName + ", folder " + folderName
				+ " first seen at line " + firstDefinition + " redefined at line(s):" + definitions();
	}

	private String definitions() {
		StringBuilder sb = new StringBuilder();
		for (int redefinition : redefinitions) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(redefinition);
		}
		return sb.toString();
	}

	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof DuplicateKeyError)) {
			return false;
		}
		DuplicateKeyError otherError = (DuplicateKeyError) other;
		if (!folderName.equals(otherError.folderName)) {
			return false;
		}
		if (!fileName.equals(otherError.fileName)) {
			return false;
		}
		if (!key.equals(otherError.key)) {
			return false;
		}
		if (firstDefinition != otherError.firstDefinition) {
			return false;
		}
		return true;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getFirstDefinition() {
		return firstDefinition;
	}

	public void setFirstDefinition(int firstDefinition) {
		this.firstDefinition = firstDefinition;
	}

	public List<Integer> getRedefinitions() {
		return redefinitions;
	}

	public void setRedefinitions(List<Integer> redefinitions) {
		this.redefinitions = redefinitions;
	}

	public void addRedefinition(int redefinition) {
		redefinitions.add(redefinition);
	}

}
