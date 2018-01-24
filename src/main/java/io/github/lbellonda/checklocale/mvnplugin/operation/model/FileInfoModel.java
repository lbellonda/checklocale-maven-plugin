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

package io.github.lbellonda.checklocale.mvnplugin.operation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.lbellonda.checklocale.mvnplugin.operation.DirInfo;
import io.github.lbellonda.checklocale.mvnplugin.operation.PropInfo;

public class FileInfoModel {

	protected DirInfo dirInfo;
	protected String name;
	protected String token;
	protected Map<String, PropInfo> properties = new HashMap<String, PropInfo>();
	protected List<PropInfo> itemsSorted = new ArrayList<PropInfo>();
	protected Map<String, PropInfo> missingItems = new HashMap<String, PropInfo>();

	public FileInfoModel(DirInfo newDirInfo) {
		dirInfo = newDirInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, PropInfo> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropInfo> properties) {
		this.properties = properties;
	}

	public String getFolderName() {
		return dirInfo.getName();
	}

	public DirInfoModel getDirInfo() {
		return dirInfo;
	}

	public Map<String, PropInfo> getMissingItems() {
		return missingItems;
	}

	public void setMissingItems(Map<String, PropInfo> missingItems) {
		this.missingItems = missingItems;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void addMissingItem(PropInfo propInfo) {
		missingItems.put(propInfo.getKey(), propInfo);
	}

	public List<PropInfo> getItemsSorted() {
		return itemsSorted;
	}

	public void setItemsSorted(List<PropInfo> itemsSorted) {
		this.itemsSorted = itemsSorted;
	}

}
