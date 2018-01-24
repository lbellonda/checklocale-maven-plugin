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

package io.github.lbellonda.checklocale.mvnplugin.operation.errors;

public class MalformedFileNameError extends PError {
	private String dirName;

	private String fileName;

	public MalformedFileNameError(final String newDirName, final String newFileName) {
		dirName = newDirName;
		fileName = newFileName;
	}

	@Override
	public String toString() {
		return "Error: malformed filename (expected locale code) " + fileName + " in folder: " + dirName;
	}

	@Override
	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof MalformedFileNameError)) {
			return false;
		}
		MalformedFileNameError otherError = (MalformedFileNameError) other;
		if (!dirName.equals(otherError.dirName)) {
			return false;
		}
		if (!fileName.equals(otherError.fileName)) {
			return false;
		}
		return true;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

}
