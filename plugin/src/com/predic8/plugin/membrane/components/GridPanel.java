/* Copyright 2011 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package com.predic8.plugin.membrane.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.predic8.plugin.membrane.util.SWTUtil;

public class GridPanel extends Composite {

	public GridPanel(Composite parent, int margin, int columns) {
		super(parent, SWT.NONE);
		setLayout(SWTUtil.createGridLayout(columns, margin));
	}

}
