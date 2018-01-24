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

public class IOReadError extends PError {

	private String folder;
	private String cause;

	public IOReadError(String newFolder, String newCause) {
		folder = newFolder;
		cause = newCause;
	}

	@Override
	public String toString() {
		return "Error: accessing folder '" + folder + "' cause:" + cause;
	}

	@Override
	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof IOReadError)) {
			return false;
		}
		IOReadError otherError = (IOReadError) other;
		if (!folder.equals(otherError.folder)) {
			return false;
		}
		if (!cause.equals(otherError.cause)) {
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

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

}
