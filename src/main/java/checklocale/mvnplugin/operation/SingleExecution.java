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

package checklocale.mvnplugin.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import checklocale.mvnplugin.operation.errors.PError;

public class SingleExecution {
	private boolean hasData = false;
	private String folderName;
	private String key;
	List<PError> errors = new ArrayList<PError>();
	private final Configuration configuration;
	List<DirInfo> dirs = new ArrayList<DirInfo>();

	public SingleExecution(Configuration newConfiguration) {
		key = UUID.randomUUID().toString();
		configuration = newConfiguration;
	}

	public void setKey(String value) {
		key = value;
	}

	public DirInfo getBaseLocaleExcludingCurrentIfLocale(String target) {
		if (configuration.hasBaseLocale()) {
			if (configuration.getBaseLocale().equals(target)) {
				return null;
			}
			for (DirInfo dirInfo : dirs) {
				if (dirInfo.getLocale().equals(configuration.getBaseLocale())) {
					return dirInfo;
				}
			}
		}
		return null;
	}

	public FileInfo getBaseLocaleFileInfo(DirInfo dirInfo, String fileToken) {
		if ((null == fileToken) || (null == dirInfo)) {
			return null;
		}
		for (FileInfo fileInfo : dirInfo.getFiles()) {
			if (fileInfo.getToken().equals(fileToken)) {
				return fileInfo;
			}
		}
		return null;
	}

	public void addError(PError error) {
		errors.add(error);
	}

	public void addErrors(Collection<PError> newErrors) {
		errors.addAll(newErrors);
	}

	public boolean isHasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public List<PError> getErrors() {
		return errors;
	}

	public void setErrors(List<PError> errors) {
		this.errors = errors;
	}

	public String getKey() {
		return key;
	}

	public List<DirInfo> getDirs() {
		return dirs;
	}

	public void setDirs(List<DirInfo> dirs) {
		this.dirs = dirs;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

}
