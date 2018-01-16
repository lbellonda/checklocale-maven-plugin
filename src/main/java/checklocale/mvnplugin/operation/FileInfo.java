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

package checklocale.mvnplugin.operation;

import checklocale.mvnplugin.operation.model.FileInfoModel;

public class FileInfo extends FileInfoModel {

	public FileInfo(DirInfo newDirInfo) {
		super(newDirInfo);
	}

	@Deprecated
	public void addKey(String key, String value, int lineNo) {
		PropInfo property = null;
		if (properties.containsKey(key)) {
			property = properties.get(key);
			PropInfo duplicatedProperty = newProperty(key, value, lineNo);
			property.getRedefinitions().add(duplicatedProperty);
		} else {
			property = newProperty(key, value, lineNo);
			properties.put(key, property);
			itemsSorted.add(property);
		}
	}

	public void addInfo(PropInfo newModel) {
		if (properties.containsKey(newModel.getKey())) {
			PropInfo property = properties.get(newModel.getKey());
			property.getRedefinitions().add(newModel);
		} else {
			properties.put(newModel.getKey(), newModel);
			itemsSorted.add(newModel);
		}
	}

	private PropInfo newProperty(String key, String value, int lineNo) {
		PropInfo property = new PropInfo();
		property.setKey(key);
		property.setLineDefined(lineNo);
		property.setValue(value);
		return property;
	}

	public boolean hasMissingItems() {
		return !missingItems.isEmpty();
	}

}
