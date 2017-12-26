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

package checklocale.mvnplugin.operation.model;

import java.util.HashMap;
import java.util.Map;

public class FileInfoModel {

	protected DirInfoModel dirInfo;
	protected String name;
	protected Map<String, PropInfoModel> properties = new HashMap<String, PropInfoModel>();

	public FileInfoModel(DirInfoModel newDirInfo) {
		dirInfo = newDirInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, PropInfoModel> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropInfoModel> properties) {
		this.properties = properties;
	}

	public String getFolderName() {
		return dirInfo.getName();
	}

}
