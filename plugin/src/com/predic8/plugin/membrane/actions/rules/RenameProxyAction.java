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

import org.eclipse.jface.viewers.TableViewer;

public class RenameProxyAction extends AbstractProxyAction {

	private TableViewer tableViewer;
	
	public RenameProxyAction(TableViewer treeView) {
		super("Rename Proxy Action", "Rename Proxy");
		this.tableViewer = treeView;
	}
	
	public void run() {		
		tableViewer.editElement(selectedProxy, 0);
	}
	
}
