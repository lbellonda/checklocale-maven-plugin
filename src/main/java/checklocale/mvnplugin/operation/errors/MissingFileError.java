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

package checklocale.mvnplugin.operation.errors;

public class MissingFileError extends PError {

	private String referenceName = "";
	private String candidateName = "";
	private String fileName = "";

	public MissingFileError(final String pReferenceName, final String pCandidateName, final String pFileName) {

		referenceName = pReferenceName;
		candidateName = pCandidateName;
		fileName = pFileName;
	}

	public String toString() {
		return "Error: " + fileName + " present in " + referenceName + ", can't be found in " + candidateName;
	}

	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof MissingFileError)) {
			return false;
		}
		MissingFileError otherError = (MissingFileError) other;
		if (!referenceName.equals(otherError.referenceName)) {
			return false;
		}
		if (!candidateName.equals(otherError.candidateName)) {
			return false;
		}
		if (!fileName.equals(otherError.fileName)) {
			return false;
		}
		return true;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
