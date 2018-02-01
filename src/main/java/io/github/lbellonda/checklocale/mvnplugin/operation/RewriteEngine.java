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

package io.github.lbellonda.checklocale.mvnplugin.operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.github.lbellonda.checklocale.mvnplugin.operation.errors.DirectoryWritingError;
import io.github.lbellonda.checklocale.mvnplugin.operation.errors.FileWritingError;

public class RewriteEngine {

	public void writeOutInfo(SingleExecution execution) {
		List<DirInfo> dirInfos = execution.getDirs();
		Configuration configuration = execution.getConfiguration();
		DirInfo baseLocaleDirInfo = execution.getBaseLocaleExcludingCurrentIfLocale(null);

		File outputFolder = new File(configuration.getOutputFolder());
		outputFolder.mkdirs();
		if (!outputFolder.exists()) {
			execution.getErrors().add(new DirectoryWritingError(configuration.getOutputFolder()));
		}
		File executionTokenFolder = new File(outputFolder, execution.getKey());
		executionTokenFolder.mkdirs();
		if (!executionTokenFolder.exists()) {
			execution.getErrors().add(new DirectoryWritingError(executionTokenFolder.getName()));
		}
		writeInfo(execution, executionTokenFolder);
		for (DirInfo dirInfo : dirInfos) {
			File localeFolder = new File(executionTokenFolder, dirInfo.getName());
			localeFolder.mkdirs();
			if (!localeFolder.exists()) {
				execution.addError(new DirectoryWritingError(localeFolder.getName()));
			}
			for (FileInfo fileInfo : dirInfo.getFiles()) {
				// baseLocaleDirInfo is null for the base locale
				FileInfo baseLocaleFileInfo = execution.getBaseLocaleFileInfo(baseLocaleDirInfo, fileInfo.getToken());
				writeSingleLocale(execution, localeFolder, fileInfo, baseLocaleFileInfo);
			}
		}
	}

	private void writeInfo(SingleExecution execution, File folder) {
		StringBuilder sb = new StringBuilder();
		sb.append("Execution for folder:" + execution.getFolderName());
		File outputFile = new File(folder, "info");
		try {
			FileUtils.writeStringToFile(outputFile, sb.toString(), execution.getConfiguration().getEncoding());
		} catch (IOException ex) {
			execution.addError(new FileWritingError(outputFile.getAbsolutePath(), ex.getMessage()));
		}
	}

	private void writeSingleLocale(SingleExecution execution, File folder, FileInfo fileInfo, FileInfo baseLocale) {
		StringBuilder sb = new StringBuilder();
		writeSingleLocale(execution.getConfiguration().getEncoding(), fileInfo, baseLocale, sb,
				execution.getConfiguration().isSkipComments());
		File outputFile = new File(folder, fileInfo.getName());
		try {
			FileUtils.writeStringToFile(outputFile, sb.toString(), execution.getConfiguration().getEncoding());
		} catch (IOException ex) {
			execution.addError(new FileWritingError(outputFile.getAbsolutePath(), ex.getMessage()));
		}
	}

	private void writeSingleLocale(final String encoding, FileInfo fileInfo, FileInfo baseLocale, StringBuilder sb,
			final boolean isSkipComments) {
		sb.append("# automatically written, encoding:" + encoding + ", baseLocale:"
				+ (null == baseLocale ? "unspecified" : baseLocale.getDirInfo().getLocale()) + "\n");
		sb.append("# ****existing items\n");
		for (PropInfo propInfo : fileInfo.getItemsSorted()) {
			writeSingleItem(propInfo, fileInfo, baseLocale, sb, isSkipComments);
		}
		if (fileInfo.hasMissingItems()) {
			sb.append("# ****missing items\n");
			for (PropInfo missingInfo : fileInfo.getMissingItems().values()) {
				writeSingleItem(missingInfo, fileInfo, null, sb, false);
			}
		}

	} // writeSingleLocale()

	private void writeSingleItem(PropInfo propInfo, FileInfo fileInfo, FileInfo baseLocale, StringBuilder sb,
			final boolean isSkipComments) {
		if (!propInfo.isValue() && isSkipComments) {
			return;
		}

		PropInfo baseItem = null;
		if (null != baseLocale) {
			baseItem = baseLocale.getProperties().get(propInfo.getKey());
		}
		if (propInfo.hasDuplicates()) {
			ArrayList<PropInfo> values = new ArrayList<PropInfo>();
			for (PropInfo duplicated : propInfo.getRedefinitions()) {
				values.add(0, duplicated);
			}
			values.add(propInfo);
			PropInfo selected = null;
			for (PropInfo option : values) {
				if (null == selected) {
					if (null != baseItem) {
						if (!option.getValue().equals(baseItem.getValue())) {
							selected = option;
							break;
						}
					}
				}
			}
			if (null == selected) {
				selected = propInfo;
			}
			writeItemLine(selected, sb);
			for (PropInfo option : values) {
				if (option.getLineDefined() != selected.getLineDefined()) {
					sb.append("# duplicated at line " + option.getLineDefined() + "\n");
					sb.append("# ");
					writeItemLine(option, sb);
				}
			}
		} else {
			writeItemLine(propInfo, sb);
		}
	} // writeSingleItem()

	private void writeItemLine(PropInfo propInfo, StringBuilder sb) {
		propInfo.writeToString(sb);
		sb.append("\n");
	} // writeItemLine()

}
