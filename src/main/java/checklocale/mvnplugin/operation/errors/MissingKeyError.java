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

public class MissingKeyError extends PError {

	private String folder;
	private String file;
	private String key;
	private int definedAt;
	private String referenceFolder;

	public MissingKeyError() {
		// nothing
	}

	public MissingKeyError(String theFolder, String theFile, String theKey, int firstDefined,
			String theReferenceFolder) {
		folder = theFolder;
		file = theFile;
		key = theKey;
		definedAt = firstDefined;
		referenceFolder = theReferenceFolder;
	}

	@Override
	public String toString() {
		return "Error missing key '" + key + "' in file " + folder + "/" + file + " defined in file " + referenceFolder
				+ "/" + file + " at line " + definedAt;
	}

	@Override
	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof MissingKeyError)) {
			return false;
		}
		MissingKeyError otherError = (MissingKeyError) other;
		if (!folder.equals(otherError.folder)) {
			return false;
		}
		if (!file.equals(otherError.file)) {
			return false;
		}
		if (!key.equals(otherError.key)) {
			return false;
		}
		if (!referenceFolder.equals(otherError.referenceFolder)) {
			return false;
		}
		if (definedAt != otherError.definedAt) {
			return false;
		}
		return true;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getDefinedAt() {
		return definedAt;
	}

	public void setDefinedAt(int definedAt) {
		this.definedAt = definedAt;
	}

	public String getReferenceFolder() {
		return referenceFolder;
	}

	public void setReferenceFolder(String referenceFolder) {
		this.referenceFolder = referenceFolder;
	}

}
