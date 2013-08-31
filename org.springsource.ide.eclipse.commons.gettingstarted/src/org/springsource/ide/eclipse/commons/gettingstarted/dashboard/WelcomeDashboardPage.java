/*******************************************************************************
 *  Copyright (c) 2013 GoPivotal, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      GoPivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.gettingstarted.dashboard;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.springsource.ide.eclipse.commons.core.preferences.StsProperties;
import org.springsource.ide.eclipse.commons.gettingstarted.GettingStartedActivator;
import org.springsource.ide.eclipse.commons.gettingstarted.browser.BrowserContext;
import org.springsource.ide.eclipse.commons.ui.UiUtil;

@SuppressWarnings("restriction")
public class WelcomeDashboardPage extends WebDashboardPage {

	private File welcomeHtml;
	private DashboardEditor dashboard;

	public WelcomeDashboardPage(DashboardEditor dashboard) throws URISyntaxException, IOException {
		StsProperties props = StsProperties.getInstance(new NullProgressMonitor());
		this.dashboard = dashboard;
		String contentUrl = props.get("dashboard.welcome.url");
		if (contentUrl==null) {
			//shouldn't happen, but do something with this anyhow, better than a blank page or an error.
			setHomeUrl("http://springsource.org");
		} else if (contentUrl.startsWith("platform:")) {
			//platform url assumed to point to a bundled directory of 'templated' content that needs StsProperties replaced.
			File contentInstance = DashboardCopier.getCopy(new File(FileLocator.toFileURL(new URL(contentUrl)).toURI()), new NullProgressMonitor());
			welcomeHtml = new File(contentInstance, "index.html");
//			setName("Welcome");
			setHomeUrl(welcomeHtml.toURI().toString());
		} else {
			setHomeUrl(contentUrl);
		}
	}
	
	@Override
	protected boolean hasToolbar() {
		return false;
	}
	
	@Override
	public boolean canClose() {
		//Shouldn't allow closing the main / welcome page.
		return false;
	}

	@Override
	protected void addBrowserHooks(Browser browser) {
		super.addBrowserHooks(browser);
		browser.addLocationListener(new URLInterceptor(browser));
	}
	
	/**
	 * Not used anymore. Now only using the JavaScript function approach.
	 */
	public class URLInterceptor extends BrowserContext implements LocationListener {
		
		public URLInterceptor(Browser browser) {
			super(browser);
		}
		
		@Override
		public void changed(LocationEvent event) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void changing(LocationEvent event) {
			event.doit = false; //all navigation in welcome page must be intercepted.
			
			
			//Be careful...any  exception thrown out of here have a nasty tendency to deadlock Eclipse 
			// (By crashing native UI thread maybe?)
			try {
				URI uri = new URI(event.location);
				//zip file containing a codeset single codeset:
				// https://github.com/kdvolder/gs-one-codeset/archive/master.zip?sts_codeset=true
//				Map<String, String> params = URIParams.parse(uri);
				IPath path = new Path(uri.getPath());
				String host = uri.getHost();
				if (event.location.equals("http://dashboard/guides")) {
					openImportWizard();
					return;
				}
				if ("dashboard".equals(host)) {
					if (dashboard.setActivePage(getPageId(path))) {
						return;
					}
				}
				if (WebBrowserPreference.getBrowserChoice()==WebBrowserPreference.INTERNAL) {
					if (dashboard.openWebPage(event.location)) {
						return;
					}
				}

				//Nothing else worked so far, try the most generic way to open a web browser.
				UiUtil.openUrl(event.location);
			} catch (Throwable e) {
				GettingStartedActivator.log(e);
			}
		}


		private String getPageId(IPath path) {
			String pageId = path.toString();
			if (pageId.startsWith("/")) {
				pageId = pageId.substring(1);
			}
			return pageId;
		}

//
//		@Override
//		public void changed(LocationEvent event) {
//		}
		
		private boolean openImportWizard() {
			Shell shell = getShell();
			if (shell!=null) {
				try {
					IWizardRegistry registry = PlatformUI.getWorkbench().getImportWizardRegistry();
					IWizardDescriptor descriptor = registry.findWizard("org.springsource.ide.eclipse.gettingstarted.wizards.import.generic");
					if (descriptor!=null) {
						IWorkbenchWizard wiz = descriptor.createWizard();
						
			//			wiz.setEnableOpenHomePage(enableOpenHomepage);
			//			wiz.setItem(guide);
						WizardDialog dialog = new WizardDialog(shell, wiz);
						dialog.setBlockOnOpen(false);
						dialog.open();
						return true;
					}
				} catch (CoreException e) {
					GettingStartedActivator.log(e);
				}
			}
			return false;
		}
		
		
	}
		
	
}
