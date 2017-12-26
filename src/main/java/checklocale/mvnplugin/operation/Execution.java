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

package checklocale.mvnplugin.operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import checklocale.mvnplugin.operation.errors.DuplicateKeyError;
import checklocale.mvnplugin.operation.errors.IOReadError;
import checklocale.mvnplugin.operation.errors.MissingDirError;
import checklocale.mvnplugin.operation.errors.MissingFileError;
import checklocale.mvnplugin.operation.errors.MissingKeyError;
import checklocale.mvnplugin.operation.errors.PError;
import checklocale.mvnplugin.operation.model.FileInfoModel;
import checklocale.mvnplugin.operation.model.PropInfoModel;

public class Execution {

	public List<PError> execute(Configuration configuration) {
		List<PError> errors = new ArrayList<PError>();
		for (String resourceDir : configuration.getDirectories()) {
			errors.addAll(execute(configuration, resourceDir));
		}
		return errors;
	}

	protected List<PError> execute(Configuration configuration, String folder) {
		List<PError> errors = new ArrayList<PError>();
		ReadDirInfoResult result = readData(configuration, folder);
		errors.addAll(result.getErrors());
		if (!errors.isEmpty()) {
			return result.getErrors();
		}
		List<DirInfo> dirs = result.getFolders();
		errors.addAll(checkFileExsistence(dirs));
		errors.addAll(checkDuplicateItems(dirs));
		errors.addAll(checkMissingItems(dirs));
		return errors;
	}

	protected List<PError> checkFileExsistence(List<DirInfo> dirs) {
		List<PError> errors = new ArrayList<PError>();
		for (DirInfo reference : dirs) {
			for (DirInfo candidate : dirs) {
				if (!reference.getName().equals(candidate.getName())) {
					for (FileInfoModel referenceFile : reference.getFiles()) {
						boolean found = false;
						for (FileInfoModel candidateFile : candidate.getFiles()) {
							if (candidateFile.getName().equals(referenceFile.getName())) {
								found = true;
								break;
							}
						}
						if (!found) {
							errors.add(new MissingFileError(reference.getName(), candidate.getName(),
									referenceFile.getName()));
						}
					}
				}
			}
		}
		return errors;
	}

	protected List<PError> checkDuplicateItems(List<DirInfo> dirs) {
		List<PError> errors = new ArrayList<PError>();
		for (DirInfo dirInfo : dirs) {
			errors.addAll(checkDuplicateItems(dirInfo));
		}
		return errors;
	}

	protected List<PError> checkDuplicateItems(DirInfo dirInfo) {
		List<PError> errors = new ArrayList<PError>();
		for (FileInfoModel fileInfo : dirInfo.getFiles()) {
			errors.addAll(checkDuplicateItems(fileInfo));
		}
		return errors;
	}

	protected List<PError> checkDuplicateItems(FileInfoModel fileInfo) {
		List<PError> errors = new ArrayList<PError>();
		for (PropInfoModel propInfo : fileInfo.getProperties().values()) {
			if (!propInfo.getRedefinitions().isEmpty()) {
				DuplicateKeyError duplicateKeyError = new DuplicateKeyError(fileInfo.getFolderName(),
						fileInfo.getName(), propInfo.getKey(), propInfo.getLineDefined());
				for (int redefinition : propInfo.getRedefinitions()) {
					duplicateKeyError.addRedefinition(redefinition);
				}
				errors.add(duplicateKeyError);
			}
		}
		return errors;
	}

	protected List<PError> checkMissingItems(List<DirInfo> dirs) {
		List<PError> errors = new ArrayList<PError>();
		for (DirInfo dirInfo : dirs) {
			for (DirInfo target : dirs) {
				if (!dirInfo.getName().equals(target.getName())) {
					errors.addAll(checkMissingItems(dirInfo, target));
				}
			}
		}
		return errors;
	}

	private List<PError> checkMissingItems(DirInfo reference, DirInfo candidate) {
		List<PError> errors = new ArrayList<PError>();
		for (FileInfoModel referenceFile : reference.getFiles()) {
			for (FileInfoModel candidateFile : candidate.getFiles()) {
				if (candidateFile.getName().equals(referenceFile.getName())) {
					errors.addAll(checkMissingItems(referenceFile, candidateFile));
				}
			}
		}
		return errors;
	}

	private List<PError> checkMissingItems(FileInfoModel referenceFile, FileInfoModel candidateFile) {
		List<PError> errors = new ArrayList<PError>();
		for (String key : referenceFile.getProperties().keySet()) {
			if (!candidateFile.getProperties().containsKey(key)) {
				PropInfoModel propInfo = referenceFile.getProperties().get(key);
				MissingKeyError missingKeyError = new MissingKeyError();
				missingKeyError.setFolder(candidateFile.getFolderName());
				missingKeyError.setFile(candidateFile.getName());
				missingKeyError.setKey(key);
				missingKeyError.setReferenceFolder(referenceFile.getFolderName());
				missingKeyError.setDefinedAt(propInfo.getLineDefined());
				errors.add(missingKeyError);
			}
		}
		return errors;
	}

	protected ReadDirInfoResult readData(Configuration configuration, String folder) {
		ReadDirInfoResult result = new ReadDirInfoResult();
		String projectBaseDir = configuration.getBaseDir();
		File baseDir = null ;
		if( null != projectBaseDir ) {
			baseDir = new File(projectBaseDir, folder);
		} else {
			baseDir = new File(folder);
		}
		File[] dirs = null;
		if (baseDir.exists() && baseDir.isDirectory()) {
			dirs = baseDir.listFiles();
		}
		if (null == dirs) {
			MissingDirError error = new MissingDirError(folder, baseDir.getAbsolutePath());
			result.addError(error);
			return result;
		}
		for (File subDir : dirs) {
			if (subDir.isDirectory()) {
				DirInfo dirInfo = new DirInfo();
				dirInfo.setName(subDir.getName());
				result.addFolder(dirInfo);
				result.getErrors().addAll(readSubDir(configuration, dirInfo, subDir));
			}
		}
		return result;
	}

	private List<PError> readSubDir(Configuration configuration, DirInfo dirInfo, File folderFile) {
		List<PError> errors = new ArrayList<PError>();
		File[] files = folderFile.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				FileInfo fileInfo = new FileInfo(dirInfo);
				fileInfo.setName(file.getName());
				dirInfo.addFile(fileInfo);
				errors.addAll(readFile(configuration, fileInfo, file));
			}
		}
		return errors;
	}

	private List<PError> readFile(Configuration configuration, FileInfo fileInfo, File dataFile) {
		List<PError> errors = new ArrayList<PError>();
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(dataFile, configuration.getEncoding());
		} catch (IOException ex) {
			errors.add(new IOReadError(dataFile.getName(), ex.getMessage()));
			return errors;
		}
		try {
			int lineNo = 0;
			for (String entry : lines) {
				lineNo++;
				final String key = extractKey(entry);
				if (null != key) {
					fileInfo.addKey(key, lineNo);
				}
			}
		} catch (Exception ex) {
			errors.add(new IOReadError(dataFile.getName(), ex.getMessage()));
			return errors;
		}
		return errors;
	} // readSubDir

	protected String extractKey(final String input) {
		if (null == input) {
			return null;
		}
		String input2 = input.trim();
		if (input2.startsWith("#")) {
			return null;
		}
		int indexOfSep = input2.indexOf("=");
		if (indexOfSep >= 0) {
			String key = input2.substring(0, indexOfSep);
			if (key.isEmpty()) {
				return null;
			}
			return key.trim();
		}
		return null;
	} // extractKey

}
