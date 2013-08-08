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

package com.predic8.plugin.membrane.views;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.predic8.membrane.core.exchange.AbstractExchange;
import com.predic8.membrane.core.exchangestore.ExchangeStore;
import com.predic8.membrane.core.rules.Rule;
import com.predic8.plugin.membrane.PlatformUtil;
import com.predic8.plugin.membrane.contentproviders.ProxyStatisticsContentProvider;
import com.predic8.plugin.membrane.labelproviders.ProxyStatisticsLabelProvider;
import com.predic8.plugin.membrane.util.SWTUtil;

public class ProxyStatisticsView extends AbstractProxiesView {

	public static final String VIEW_ID = "com.predic8.plugin.membrane.views.ProxyStatisticsView";

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = createComposite(parent);

		createRefreshButton(composite);
		
		createTableViewer(composite);
	
		addCellEditorsAndModifiersToViewer();
		
		new Label(composite, SWT.NONE).setText(" All times in ms");
					
	    getExchangeStore().addExchangesStoreListener(this);
	    setInputForTable(PlatformUtil.getRouter().getRuleManager());
	}

	@Override
	protected void addListenersForTableViewer() {
		super.addListenersForTableViewer();
		tableViewer.addDoubleClickListener(createDoubleClickListener());
	}
	
	private IDoubleClickListener createDoubleClickListener() {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selectedItem = selection.getFirstElement();
				if (selectedItem instanceof Rule) {
					tableViewer.editElement(selectedItem, 0);
				} 
			}
		};
	}
	
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new ProxyStatisticsLabelProvider();
	}
	
	@Override
	protected IContentProvider createContentProvider() {
		return new ProxyStatisticsContentProvider();
	}
	
	private void createRefreshButton(Composite composite) {
		Button bt = new Button(composite, SWT.PUSH);
		bt.setText("Refresh Table");
		bt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				 setInputForTable(PlatformUtil.getRouter().getRuleManager());
			}
		});
	}


	private ExchangeStore getExchangeStore() {
		return PlatformUtil.getRouter().getExchangeStore();
	}

	@Override
	protected void setPropertiesForTableViewer() {
		super.setPropertiesForTableViewer();
		tableViewer.setColumnProperties(new String[] {"name"});
	}

	private Composite createComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = SWTUtil.createGridLayout(1, 10, 5, 20, 5);
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
		return composite;
	}

	@Override
	protected String[] getTableColumnTitles() {
		return new String[] { "Proxy", "Exchanges", "Minimum Time", "Maximum Time", "Average Time", "Bytes Sent", "Bytes Received", "Errors"};
	}

	@Override
	protected int[] getTableColumnBounds() {
		return new int[] { 140, 80, 90, 90, 100, 80, 90, 70};
	}
	
	@Override
	public void ruleRemoved(Rule rule, int rulesleft) {
		setInputForTable(PlatformUtil.getRouter().getRuleManager());
	}

	@Override
	public void ruleUpdated(Rule rule) {
		setInputForTable(PlatformUtil.getRouter().getRuleManager());
	}


	@Override
	public void rulePositionsChanged() {
		setInputForTable(PlatformUtil.getRouter().getRuleManager());
	}

	@Override
	public void setExchangeStopped(AbstractExchange exchange) {
		// ignore
	}


}
