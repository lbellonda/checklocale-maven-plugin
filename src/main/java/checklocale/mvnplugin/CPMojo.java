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

package checklocale.mvnplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import checklocale.mvnplugin.operation.Configuration;
import checklocale.mvnplugin.operation.Execution;
import checklocale.mvnplugin.operation.errors.PError;

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

	/**
	 * Flag to fire errors, if false only warnings will be issued
	 * 
	 * @parameter errors, defaultValue = true
	 */
	private boolean errors = true ;

	/**
	 * Encoding of the properties
	 * 
	 * @parameter encoding, defaultValue = "UTF-8"
	 */
	private String encoding;

	/**
	 * Folders to check
	 * 
	 * @parameter directories
	 * @required
	 */
	private String[] directories;

	/**
	 * Project folder
	 * 
	 * @parameter default-value="${basedir}"
	 * @readonly
	 */
	private String projectBaseDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().debug(MOJO_NAME + " started, checking properties");
			getLog().debug(MOJO_NAME + " context: " + getPluginContext().toString());

			getLog().debug(MOJO_NAME + " parameters:");
			getLog().debug("   errors:" + errors);
			getLog().debug("   encoding:" + encoding);
			getLog().debug("   baseDir:" + projectBaseDir);
			List<String> folders = getListFromFolders();
			if ((null == directories) || (directories.length == 0)) {
				getLog().debug(MOJO_NAME + " no folders to check.");
				return;
			}
			Configuration configuration = new Configuration();
			if ((null != encoding) && (encoding.length() > 0)) {
				configuration.setEncoding(encoding);
			}
			configuration.setDirectories(folders);
			configuration.setBaseDir(projectBaseDir);
			getLog().debug(MOJO_NAME + " starting execution");
			Execution execution = new Execution();
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
