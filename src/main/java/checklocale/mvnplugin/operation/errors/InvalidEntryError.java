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

public class InvalidEntryError extends PError {

	private int lineNo;
	private String fileName;
	private String locale;
	private String value;

	public InvalidEntryError(String argLocale, String argFileName, String argValue, int argLineNo) {
		locale = argLocale;
		value = argValue;
		lineNo = argLineNo;
		fileName = argFileName;
	}

	public void setFileName(String argFileName) {
		fileName = argFileName;
	}

	@Override
	public String toString() {
		return "Error: invalid entry file: '" + fileName + "' locale: " + locale + " line:" + lineNo + " value:"
				+ value;
	}

	@Override
	public boolean compareTo(PError other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof InvalidEntryError)) {
			return false;
		}
		InvalidEntryError otherError = (InvalidEntryError) other;
		if (!fileName.equals(otherError.fileName)) {
			return false;
		}
		if (lineNo != otherError.lineNo) {
			return false;
		}
		return true;
	}

}
