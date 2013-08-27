/*******************************************************************************
 *  Copyright (c) 2012 VMware, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.gettingstarted.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.springsource.ide.eclipse.commons.core.preferences.StsProperties;
import org.springsource.ide.eclipse.commons.core.templates.TemplateProcessor;
import org.springsource.ide.eclipse.commons.gettingstarted.GettingStartedActivator;

/**
 * Makes a copy of some content from one directory to another.
 * The target directory is wiped before copying, but only one copy
 * is made per STS session. Subsequent copies requested will
 * reuse the first copy (until STS is restarted).
 * 
 * @author Kris De Volder
 */
public class DashboardCopier {
	
	private static DashboardCopier instance = null;
	
	/**
	 * List of properties that need to be substitute in dashboard content. Other properties will
	 * be ignored.
	 */
	private static String[] DASHBOARD_PROPERTIES = {
			"spring.site.url"
	};
	
	
	/**
	 * Cache of processed content. This cache only retains content per Eclipse session.
	 * So restarting STS should begin with a clear cache.
	 */
	private Map<String, File> copied = new HashMap<String, File>();

	private File workdir;
	
	private DashboardCopier() {
		workdir = new File(GettingStartedActivator.getDefault().getStateLocation().toFile(), "dashboard");
		FileUtils.deleteQuietly(workdir);
	}
	
	public static DashboardCopier getInstance() {
		if (instance == null) {
			instance = new DashboardCopier();
		}
		return instance;
	}

	/**
	 * Copy directory contens from some source directory to some working directory
	 */
	public static File getCopy(File from, String pathKey, IProgressMonitor mon) throws IOException {
		return getInstance()._getCopy(from, pathKey, mon);
	}

	private File _getCopy(File from, String pathKey, IProgressMonitor mon) throws IOException {
		File cached = copied.get(pathKey);
		if (cached!=null) {
			return cached;
		}
		File to = new File(workdir, pathKey);
		if (!to.mkdirs()) {
			throw new IOException("Couldn't create dir "+to);
		}
		mon.beginTask("Instantiating Dashboard Content", 3);
		try {
			StsProperties props = StsProperties.getInstance(new SubProgressMonitor(mon, 1));
			Map<String, String> replacementContext = new HashMap<String, String>();
			for (String propName : DASHBOARD_PROPERTIES) {
				String value = props.get(propName);
				if (value!=null) {
					replacementContext.put("${"+propName+"}", value);
				}
			}
			TemplateProcessor processor = new TemplateProcessor(replacementContext);
			processor.process(from, to);
			return to;
		} finally {
			mon.done();
		}
	}
	
	
}
