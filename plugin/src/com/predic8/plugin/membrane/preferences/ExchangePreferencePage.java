/* Copyright 2009 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.predic8.plugin.membrane.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.predic8.plugin.membrane.MembraneUIPlugin;
import com.predic8.plugin.membrane.PlatformUtil;

public class ExchangePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "com.predic8.plugin.membrane.preferences.ExchangePreferencePage";

	private Button autoTrack;

	public ExchangePreferencePage() {

	}

	public ExchangePreferencePage(String title) {
		super(title);
		setDescription("Provides settings for Exchange options.");
	}

	public ExchangePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new RowLayout(SWT.VERTICAL));
		autoTrack = new Button(comp, SWT.CHECK);
		autoTrack.setText("Autotrack New Exchanges");
		autoTrack.setSelection(PlatformUtil.getRouter().getConfigurationManager().getProxies().getTrackExchange());

		return comp;
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MembraneUIPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void performApply() {
		PlatformUtil.getRouter().getConfigurationManager().getProxies().setTrackExchange(autoTrack.getSelection());
		try {
			PlatformUtil.getRouter().getConfigurationManager().saveConfiguration(PlatformUtil.getRouter().getConfigurationManager().getDefaultConfigurationFile());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to save configuration: " + e.getMessage());
		}
	}

	@Override
	public boolean performOk() {
		PlatformUtil.getRouter().getConfigurationManager().getProxies().setTrackExchange(autoTrack.getSelection());
		try {
			PlatformUtil.getRouter().getConfigurationManager().saveConfiguration(PlatformUtil.getRouter().getConfigurationManager().getDefaultConfigurationFile());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to save configuration: " + e.getMessage());
		}
		return true;
	}

}
