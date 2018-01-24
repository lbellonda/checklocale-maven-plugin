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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import checklocale.mvnplugin.operation.errors.DuplicateKeyError;
import checklocale.mvnplugin.operation.errors.IOReadError;
import checklocale.mvnplugin.operation.errors.InvalidEntryError;
import checklocale.mvnplugin.operation.errors.MalformedFileNameError;
import checklocale.mvnplugin.operation.errors.MissingDirError;
import checklocale.mvnplugin.operation.errors.MissingFileError;
import checklocale.mvnplugin.operation.errors.MissingKeyError;
import checklocale.mvnplugin.operation.errors.NoLocaleFoundError;
import checklocale.mvnplugin.operation.errors.PError;

public class Execution {

	public List<PError> execute(Configuration configuration) {
		List<PError> errors = new ArrayList<PError>();
		for (String resourceDir : configuration.getDirectories()) {
			errors.addAll(execute(configuration, resourceDir));
		}
		return errors;
	}

	public SingleExecution createSingleExecution(Configuration configuration) {
		SingleExecution execution = new SingleExecution(configuration);
		return execution;
	}

	protected List<PError> execute(Configuration configuration, String folder) {
		SingleExecution execution = createSingleExecution(configuration);
		execution.setFolderName(folder);
		ReadDirInfoResult result = readData(configuration, execution, folder);
		execution.addErrors(result.getErrors());
		if (!execution.getErrors().isEmpty()) {
			return execution.getErrors();
		}
		List<DirInfo> dirs = result.getFolders();
		execution.setDirs(dirs);
		execution.addErrors(checkFileExsistence(dirs));
		execution.addErrors(checkDuplicateItems(dirs));
		execution.addErrors(checkMissingItems(execution, dirs));
		if (!configuration.isPreventOutput()) {
			RewriteEngine rewriteEngine = new RewriteEngine();
			rewriteEngine.writeOutInfo(execution);
		}
		return execution.getErrors();
	}

	protected List<PError> checkFileExsistence(List<DirInfo> dirs) {
		List<PError> errors = new ArrayList<PError>();
		for (DirInfo reference : dirs) {
			for (DirInfo candidate : dirs) {
				if (!reference.getName().equals(candidate.getName())) {
					for (FileInfo referenceFile : reference.getFiles()) {
						boolean found = false;
						for (FileInfo candidateFile : candidate.getFiles()) {
							if (candidateFile.getToken().equals(referenceFile.getToken())) {
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
		for (FileInfo fileInfo : dirInfo.getFiles()) {
			errors.addAll(checkDuplicateItems(fileInfo));
		}
		return errors;
	}

	protected List<PError> checkDuplicateItems(FileInfo fileInfo) {
		List<PError> errors = new ArrayList<PError>();
		for (PropInfo propInfo : fileInfo.getProperties().values()) {
			if (!propInfo.getRedefinitions().isEmpty()) {
				DuplicateKeyError duplicateKeyError = new DuplicateKeyError(fileInfo.getFolderName(),
						fileInfo.getName(), propInfo.getKey(), propInfo.getLineDefined());
				for (PropInfo redefinition : propInfo.getRedefinitions()) {
					duplicateKeyError.addRedefinition(redefinition.getLineDefined());
				}
				errors.add(duplicateKeyError);
			}
		}
		return errors;
	}

	protected List<PError> checkMissingItems(SingleExecution execution, List<DirInfo> dirs) {
		Configuration configuration = execution.getConfiguration();
		List<PError> errors = new ArrayList<PError>();

		for (DirInfo dirInfo : dirs) {
			// First use the reference
			DirInfo baseReference = execution.getBaseLocaleExcludingCurrentIfLocale(dirInfo.getLocale());
			if (null != baseReference) {
				errors.addAll(checkMissingItems(configuration, baseReference, dirInfo));
			}
			// then check the other locales
			for (DirInfo target : dirs) {
				if (!dirInfo.getName().equals(target.getName())) {
					errors.addAll(checkMissingItems(configuration, target, dirInfo));
				}
			}
		}
		return errors;
	}

	private List<PError> checkMissingItems(Configuration configuration, DirInfo reference, DirInfo candidate) {
		List<PError> errors = new ArrayList<PError>();
		for (FileInfo referenceFile : reference.getFiles()) {
			for (FileInfo candidateFile : candidate.getFiles()) {
				if (candidateFile.getToken().equals(referenceFile.getToken())) {
					errors.addAll(checkMissingItems(referenceFile, candidateFile));
				}
			}
		}
		return errors;
	}

	private List<PError> checkMissingItems(FileInfo referenceFile, FileInfo candidateFile) {
		List<PError> errors = new ArrayList<PError>();
		for (String key : referenceFile.getProperties().keySet()) {
			if (!candidateFile.getProperties().containsKey(key)) {
				if (!candidateFile.getMissingItems().containsKey(key)) {
					PropInfo propInfo = referenceFile.getProperties().get(key);
					candidateFile.addMissingItem(propInfo);
					MissingKeyError missingKeyError = new MissingKeyError();
					missingKeyError.setFolder(candidateFile.getFolderName());
					missingKeyError.setFile(candidateFile.getName());
					missingKeyError.setKey(key);
					missingKeyError.setReferenceFolder(referenceFile.getFolderName());
					missingKeyError.setDefinedAt(propInfo.getLineDefined());
					errors.add(missingKeyError);
				}
			}
		}
		return errors;
	}

	protected ReadDirInfoResult readData(Configuration configuration, SingleExecution data, String folder) {
		ReadDirInfoResult result = new ReadDirInfoResult();
		String projectBaseDir = configuration.getBaseDir();
		File baseDir = null;
		if (null != projectBaseDir) {
			baseDir = new File(projectBaseDir, folder);
		} else {
			baseDir = new File(folder);
		}
		File[] filesOfMainFolder = null;
		if (baseDir.exists() && baseDir.isDirectory()) {
			filesOfMainFolder = baseDir.listFiles();
		}
		if (null == filesOfMainFolder) {
			MissingDirError error = new MissingDirError(folder, baseDir.getAbsolutePath());
			result.addError(error);
			return result;
		}

		readDataLayoutMultiple(configuration, data, folder, result, filesOfMainFolder, baseDir);
		if (!data.isHasData()) {
			NoLocaleFoundError error = new NoLocaleFoundError(folder, baseDir.getAbsolutePath());
			result.addError(error);
			return result;
		}
		return result;
	}

	protected ReadDirInfoResult readDataLayoutMultiple(Configuration configuration, SingleExecution data, String folder,
			ReadDirInfoResult result, File[] dirs, File baseDir) {
		for (File subDir : dirs) {
			if (subDir.isDirectory()) {
				DirInfo dirInfo = new DirInfo();
				dirInfo.setName(subDir.getName());
				dirInfo.setLocale(subDir.getName());
				result.addFolder(dirInfo);
				result.getErrors().addAll(readSubDir(configuration, data, dirInfo, subDir));
			}
		}
		return result;
	} // readDataLayoutMultiple()

	private List<PError> readSubDir(Configuration configuration, SingleExecution data, DirInfo dirInfo,
			File folderFile) {
		List<PError> errors = new ArrayList<PError>();
		File[] files = folderFile.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				FileInfo fileInfo = new FileInfo(dirInfo);
				fileInfo.setName(file.getName());
				if (configuration.isFileNameContainsLocaleCode()) {
					String fileName = file.getName();
					int indexOf = fileName.lastIndexOf(dirInfo.getLocale());
					if (indexOf < 0) {
						errors.add(new MalformedFileNameError(dirInfo.getName(), fileName));
						continue;
					}
					String fileToken = fileName.substring(0, indexOf);
					fileToken += fileName.substring(indexOf + dirInfo.getLocale().length());
					fileInfo.setToken(fileToken);
				} else {
					fileInfo.setToken(file.getName());
				}
				dirInfo.addFile(fileInfo);
				errors.addAll(readFile(configuration, fileInfo, file));
				if (!fileInfo.getProperties().isEmpty()) {
					data.setHasData(true);
				}
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
				ReadInfoResult result = extractKey(entry, configuration.isStrict());
				if (null != result) {
					if (result.isError()) {
						InvalidEntryError error = new InvalidEntryError(fileInfo.getDirInfo().getLocale(),
								fileInfo.getName(), entry, lineNo);
						errors.add(error);
					}
					PropInfo propInfo = result.getPropInfo();
					if (null != propInfo) {
						fileInfo.addInfo(propInfo);
						propInfo.setLineDefined(lineNo);
					}
				}
			}
		} catch (Exception ex) {
			errors.add(new IOReadError(dataFile.getName(), ex.getMessage()));
			return errors;
		}
		return errors;
	} // readSubDir

	/**
	 * Returns null for no valid, result with error if error, else a propinfo
	 * 
	 * @param propInfo
	 * @param input
	 * @return
	 */

	protected ReadInfoResult extractKey(final String input, final boolean strict) {
		ReadInfoResult result = new ReadInfoResult();
		result.setError(false);
		if ((null == input) || input.isEmpty()) {
			return result;
		}
		String rInput = input;
		if (rInput.charAt(0) == '\ufeff') {
			rInput = rInput.substring(1);
		}
		String input2 = rInput.trim();
		if (input2.startsWith("#") || input2.startsWith("!") || input2.isEmpty()) {
			return result;
		}
		int indexOfSep = input2.indexOf("=");
		if (indexOfSep < 0) {
			if (!strict) {
				indexOfSep = input2.indexOf(":");
				if (indexOfSep < 0) {
					indexOfSep = input2.indexOf(" ");
				}
			}
		}

		if (indexOfSep >= 0) {
			String key = input2.substring(0, indexOfSep);
			if (key.isEmpty()) {
				result.setError(true);
				return result;
			}
			PropInfo propInfo = new PropInfo();
			propInfo.setKey(key.trim());
			propInfo.setValue(input2.substring(indexOfSep + 1));
			result.setPropInfo(propInfo);
			return result;
		} else {
			result.setError(true);
		}
		return result;
	} // extractKey

}
