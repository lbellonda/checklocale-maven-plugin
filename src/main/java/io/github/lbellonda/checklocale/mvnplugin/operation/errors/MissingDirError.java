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

public class MissingDirError extends PError {

	private String dirName;

	private String absolutePath;

	public MissingDirError(final String newDirName, final String newAbsolutePath) {
		dirName = newDirName;
		absolutePath = newAbsolutePath;
	}

	@Override
	public String toString() {
		return "Error: directory " + dirName + " can't be found. Absolute path:" + absolutePath;
	}

	@Override
	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof MissingDirError)) {
			return false;
		}
		MissingDirError otherError = (MissingDirError) other;
		if (!dirName.equals(otherError.dirName)) {
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

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

}
