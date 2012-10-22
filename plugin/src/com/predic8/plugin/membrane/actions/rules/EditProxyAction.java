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

package com.predic8.plugin.membrane.actions.rules;

import org.eclipse.swt.widgets.Display;

import com.predic8.membrane.core.rules.*;
import com.predic8.plugin.membrane.dialogs.rule.*;

public class EditProxyAction extends AbstractProxyAction {

	public EditProxyAction() {
		super("Edit Proxy Action", "Edit Proxy");
	}

	@Override
	public void run() {
				
		try {
			if (selectedProxy instanceof ServiceProxy) {
				openProxyEditDialog(new ServiceProxyEditDialog(Display.getCurrent().getActiveShell()), (ServiceProxy) selectedProxy);

			} else if (selectedProxy instanceof ProxyRule) {
				openProxyEditDialog(new ProxyEditDialog(Display.getCurrent().getActiveShell()), (ProxyRule) selectedProxy);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	private void openProxyEditDialog(AbstractProxyEditDialog dialog, Rule rule) {
		if (dialog.getShell() == null) {
			dialog.create();
		}
		dialog.setInput(rule);
		dialog.open();
	}
	
}
