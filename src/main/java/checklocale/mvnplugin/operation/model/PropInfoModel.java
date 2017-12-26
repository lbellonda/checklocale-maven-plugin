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

import java.util.ArrayList;
import java.util.List;

public class PropInfoModel {

	private String key;
	private int lineDefined;
	private List<Integer> redefinitions = new ArrayList<Integer>();

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

	public List<Integer> getRedefinitions() {
		return redefinitions;
	}

	public void setRedefinitions(List<Integer> redefinitions) {
		this.redefinitions = redefinitions;
	}
}
