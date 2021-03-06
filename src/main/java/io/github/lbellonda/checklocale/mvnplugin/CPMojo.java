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

package io.github.lbellonda.checklocale.mvnplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import io.github.lbellonda.checklocale.mvnplugin.operation.Configuration;
import io.github.lbellonda.checklocale.mvnplugin.operation.Execution;
import io.github.lbellonda.checklocale.mvnplugin.operation.errors.PError;

import java.util.ArrayList;
import java.util.List;

/**
 * This goal checks the localizations files.
 * 
 * @goal checklocale
 * 
 * @phase compile
 */
public class CPMojo extends AbstractMojo {
	private static final String MOJO_NAME = "CPMojo";

	public static final String DEFAULT_FOLDER = "checklocale-maven-plugin";
	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * Flag to declare that the localization file contains the code if the flag
	 * is false (the default) the files names are: strings.properties if the
	 * flag is true, the file names will be: strings_en_US.properties,
	 * strings_it_IT.properties
	 * 
	 * @parameter fileNameContainsLocaleCode, defaultValue = false
	 */
	protected boolean fileNameContainsLocaleCode = false;

	/**
	 * The output folder where the normalized data will be written it is a
	 * subfolder of the target folder
	 * 
	 * @parameter outputFolder, defaultValue = checklocale-maven-plugin
	 */
	protected String outputFolder;

	/**
	 * The locale code for the locale to be taken as the base one.
	 * 
	 * @parameter baseLocale
	 */
	protected String baseLocale;

	/**
	 * Flag to fire errors, if false only warnings will be issued
	 * 
	 * @parameter errors, defaultValue = true
	 */
	protected boolean errors = true;

	/**
	 * Flag to skip comments rewriting, if true no comments will be generated
	 * 
	 * @parameter skipComments, defaultValue = false
	 */
	protected boolean skipComments = false;

	/**
	 * Flag to avoid output, if true no output will be generated
	 * 
	 * @parameter errors, defaultValue = false
	 */

	protected boolean preventOutput = false;
	/**
	 * Flag to allow only canonical format using '#' as separator
	 * 
	 * @parameter strict, defaultValue = false
	 */
	protected boolean strict = false;

	/**
	 * Encoding of the properties
	 * 
	 * @parameter encoding, defaultValue = "UTF-8"
	 */
	protected String encoding;

	/**
	 * Folders to check
	 * 
	 * @parameter directories
	 * @required
	 */
	protected String[] directories;

	/**
	 * Project folder
	 * 
	 * @parameter default-value="${basedir}"
	 * @readonly
	 */
	protected String projectBaseDir;

	/**
	 * Project output folder
	 * 
	 * @parameter default-value="${project.build.directory}"
	 * @readonly
	 */
	protected String targetBaseDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		innerExecute();
	}

	protected void innerExecute() throws MojoExecutionException, MojoFailureException {
		try {
			logStart();
			dumpParameters();
			Configuration configuration = setup();

			getLog().debug(MOJO_NAME + " starting execution");
			Execution execution = newExecution();
			List<PError> errorsList = execution.execute(configuration);
			if (errorsList.isEmpty()) {
				getLog().info(MOJO_NAME + " end execution, no errors.");
			} else {
				if (errors) {
					getLog().error(MOJO_NAME + " found " + errorsList.size() + " error(s).");
					for (PError error : errorsList) {
						getLog().error(error.toString());
					}
					throw new MojoFailureException(MOJO_NAME + " found " + errorsList.size() + " error(s).");
				} else {
					getLog().warn(MOJO_NAME + " found " + errorsList.size() + " error(s).");
					for (PError error : errorsList) {
						getLog().warn(error.toString());
					}
				}
			}
		} catch (Exception ex) {
			throw new MojoExecutionException("Error executing " + MOJO_NAME, ex);
		}
	}

	protected Execution newExecution() {
		Execution execution = new Execution();
		return execution;
	}

	protected Configuration setup() throws MojoFailureException {
		Configuration configuration = createConfiguration();
		checkConfiguration(configuration);
		return configuration;
	}

	private void logStart() {
		if (getLog().isDebugEnabled()) {
			getLog().debug(MOJO_NAME + " started, checking properties");
			getLog().debug(MOJO_NAME + " context: " + getPluginContext().toString());
		}
	}

	private void dumpParameters() {
		if (getLog().isDebugEnabled()) {

			getLog().debug(MOJO_NAME + " parameters:");
			getLog().debug("                       errors: " + errors);
			getLog().debug("                       strict: " + strict);
			getLog().debug("                     encoding: " + encoding);
			getLog().debug("                      baseDir: " + projectBaseDir);
			getLog().debug("   fileNameContainsLocaleCode: " + fileNameContainsLocaleCode);
			getLog().debug("                 outputFolder: " + outputFolder);
			getLog().debug("                   baseLocale: " + baseLocale);
			getLog().debug("                 skipComments: " + skipComments);
			getLog().debug("                targetBaseDir: " + targetBaseDir);
			getLog().debug("                preventOutput: " + preventOutput);

			getLog().debug("                      folders:");
			List<String> folders = getListFromFolders();
			if ((null == directories) || (directories.length == 0)) {
				getLog().debug(MOJO_NAME + " no folders to check.");
				return;
			} else {
				for (String folder : folders) {
					getLog().debug("   folder: " + folder);
				}
				getLog().debug(MOJO_NAME + " end folders dump.");
			}
		}
	} // dumpParameters()

	private Configuration createConfiguration() throws MojoFailureException {
		Configuration configuration = new Configuration();
		configuration.setEncoding(cleanParam(encoding, DEFAULT_ENCODING));
		List<String> folders = getListFromFolders();
		if ((null == directories) || (directories.length == 0)) {
			throw new MojoFailureException("no folders to check");
		}
		configuration.setDirectories(folders);
		checkEmptyParam(projectBaseDir, "Invalid project directory");
		configuration.setBaseDir(cleanParam(projectBaseDir));
		configuration.setErrors(errors);
		configuration.setStrict(strict);
		configuration.setSkipComments(skipComments);
		configuration.setFileNameContainsLocaleCode(fileNameContainsLocaleCode);
		checkEmptyParam(targetBaseDir, "Invalid build directory");

		configuration.setOutputFolder(targetBaseDir + "/" + cleanParam(outputFolder, DEFAULT_FOLDER));
		configuration.setBaseLocale(cleanParam(baseLocale));
		configuration.setPreventOutput(preventOutput);

		return configuration;
	} // createConfiguration

	private void checkEmptyParam(String param, String message) throws MojoFailureException {
		if ((null == param) || param.trim().isEmpty()) {
			throw new MojoFailureException(message);
		}
	}

	private String cleanParam(String param, String defaultValue) {
		if (null != param) {
			return param.trim();
		}
		return defaultValue;
	}

	private String cleanParam(String param) {
		return cleanParam(param, "");
	}

	private void checkConfiguration(Configuration configuration) throws MojoFailureException {
		// nothing
	} // checkParameters()

	private List<String> getListFromFolders() {
		List<String> folders = new ArrayList<String>();
		if (null != directories) {
			for (String aFolder : directories) {
				folders.add(aFolder);
				getLog().debug(MOJO_NAME + " add directory:" + aFolder);
			}
		}
		getLog().debug(MOJO_NAME + " directories count:" + folders.size());
		return folders;
	}
}
