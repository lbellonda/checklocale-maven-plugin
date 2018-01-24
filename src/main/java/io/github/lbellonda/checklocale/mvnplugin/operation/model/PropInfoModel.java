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
import java.util.List;

import io.github.lbellonda.checklocale.mvnplugin.operation.PropInfo;

public class PropInfoModel {

	protected String key;
	protected int lineDefined;
	protected List<PropInfo> redefinitions = new ArrayList<PropInfo>();
	protected String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLineDefined() {
		return lineDefined;
	}

	public void setLineDefined(int lineDefined) {
		this.lineDefined = lineDefined;
	}

	public List<PropInfo> getRedefinitions() {
		return redefinitions;
	}

	public void setRedefinitions(List<PropInfo> redefinitions) {
		this.redefinitions = redefinitions;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
