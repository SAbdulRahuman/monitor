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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.predic8.membrane.core.Router;
import com.predic8.membrane.core.RuleManager;
import com.predic8.membrane.core.exchange.AbstractExchange;
import com.predic8.membrane.core.rules.Rule;
import com.predic8.plugin.membrane.actions.exchanges.RemoveAllExchangesAction;
import com.predic8.plugin.membrane.actions.rules.RemoveProxyAction;
import com.predic8.plugin.membrane.actions.rules.RenameProxyAction;
import com.predic8.plugin.membrane.actions.rules.AbstractProxyAction;
import com.predic8.plugin.membrane.actions.rules.EditProxyAction;
import com.predic8.plugin.membrane.actions.views.ShowProxyDetailsViewAction;
import com.predic8.plugin.membrane.celleditors.ProxyNameCellEditorModifier;
import com.predic8.plugin.membrane.components.composites.ProxiesViewControlsComposite;
import com.predic8.plugin.membrane.contentproviders.ProxiesViewContentProvider;
import com.predic8.plugin.membrane.labelproviders.ProxiesViewLabelProvider;
import com.predic8.plugin.membrane.util.SWTUtil;

public class ProxiesView extends AbstractProxiesView {

	public static final String VIEW_ID = "com.predic8.plugin.membrane.views.ProxiesView";

	private ProxiesViewControlsComposite controlsComposite;
	
	protected ProxyNameCellEditorModifier cellEditorModifier;
	
	protected List<AbstractProxyAction> actions = new ArrayList<AbstractProxyAction>();
	
	@Override
	public void createPartControl(Composite parent) {
		Composite composite = createComposite(parent);

		createTableViewer(composite);
	
		controlsComposite = new ProxiesViewControlsComposite(composite);
		
		createCommentLabel(composite);
		
		createActions();
		addTableMenu();
		
		Router.getInstance().getExchangeStore().addExchangesStoreListener(this);
		Router.getInstance().getRuleManager().addRuleChangeListener(this);
		setInputForTable(Router.getInstance().getRuleManager());
	}
	
	
	protected void createActions () {
		actions.add(new RemoveProxyAction());
		actions.add(new EditProxyAction());
		actions.add(new RemoveAllExchangesAction());
		actions.add(new RenameProxyAction(tableViewer));
		actions.add(new ShowProxyDetailsViewAction());
	}
	
	protected void addTableMenu() {
		MenuManager menuManager = new MenuManager();
		for (AbstractProxyAction action : actions) {
			menuManager.add(action);
		}
		tableViewer.getControl().setMenu(menuManager.createContextMenu(tableViewer.getControl()));
		getSite().registerContextMenu(menuManager, tableViewer);
	}
	
	private void enableActions(boolean enabled) {
		for (AbstractProxyAction action : actions) {
			action.setEnabled(enabled);
		}
	}

	private Composite createComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = SWTUtil.createGridLayout(2, 10, 5, 20, 5);
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		return composite;
	}

	private void createCommentLabel(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("Proxies are evaluated in top-down direction.");
		GridData gData = new GridData();
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new ProxiesViewLabelProvider();
	}
	
	@Override
	protected IContentProvider createContentProvider() {
		return new ProxiesViewContentProvider();
	}
	
	@Override
	protected void setPropertiesForTableViewer() {
		super.setPropertiesForTableViewer();
		tableViewer.setColumnProperties(new String[] {"name" });
	}
	
	@Override
	protected void addListenersForTableViewer() {
		super.addListenersForTableViewer();
		tableViewer.addDoubleClickListener(createDoubleClickListener());
		tableViewer.addSelectionChangedListener(createSelectionChangeListener());
	}
	
	protected void addCellEditorsAndModifiersToViewer() {
		setCellEditorForTableViewer(tableViewer);
		cellEditorModifier = new ProxyNameCellEditorModifier();
		cellEditorModifier.setTableViewer(tableViewer);
		tableViewer.setCellModifier(cellEditorModifier);

		TableViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		}, ColumnViewerEditor.DEFAULT);
	}

	private ISelectionChangedListener createSelectionChangeListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (selection == null || selection.isEmpty()) {
					controlsComposite.enableDependentButtons(false);
					return;
				}
				controlsComposite.enableDependentButtons(true);
				
				setSelectedProxy((Rule)selection.getFirstElement());
			}
			
			private void setSelectedProxy(Rule selectedProxy) {
				for (AbstractProxyAction action : actions) {
					action.setSelectedProxy(selectedProxy);
				}
				controlsComposite.setSelectedProxy(selectedProxy);
				updateDetailsViewIfVisible(selectedProxy);
			}
			
		};
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
	
	private void updateDetailsViewIfVisible(Rule selectedProxy) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(ProxyDetailsView.VIEW_ID);
		if (part == null || !page.isPartVisible(part)) 
			return;
		
		ProxyDetailsView view = (ProxyDetailsView)part;
		view.setProxyToDisplay(selectedProxy);
		
	}
	
	private void setCellEditorForTableViewer(final TableViewer tableViewer) {
		final CellEditor[] cellEditors = new CellEditor[1];
		cellEditors[0] = new TextCellEditor(tableViewer.getTable(), SWT.BORDER);
		tableViewer.setCellEditors(cellEditors);
	}

	@Override
	protected String[] getTableColumnTitles() {
		return new String[] { "Proxy", "Exchanges"};
	}
	
	@Override
	protected int[] getTableColumnBounds() {
		return new int[] { 158, 80 };
	}
	
	@Override
	public void ruleRemoved(Rule rule, int rulesLeft) {
		setInputForTable(Router.getInstance().getRuleManager());
		changeSelectionAfterRemoval();
		if (rulesLeft == 0){
			enableActions(false);
		}
	}

	public void ruleUpdated(Rule rule) {
		setInputForTable(Router.getInstance().getRuleManager());
	}

	public void rulePositionsChanged() {
		setInputForTable(Router.getInstance().getRuleManager());
	}

	
	private void changeSelectionAfterRemoval() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (tableViewer.getTable().getItemCount() == 0) {
					updateDetailsViewIfVisible(null);
					return;
				}
				TableItem item = tableViewer.getTable().getItem(0);
				tableViewer.setSelection(new StructuredSelection(item.getData()));
				notifytableSelectionListeners(item); 
			}
		});
	}

	private void notifytableSelectionListeners(TableItem item) {
		Event e = new Event();
		e.item = item;
		e.widget = tableViewer.getTable();
		e.type = SWT.Selection;
		tableViewer.getTable().notifyListeners(SWT.Selection, e);
	}

	public void setExchangeStopped(AbstractExchange exchange) {
		// ignore
	}
	
	@Override
	public void ruleAdded(Rule rule) {
		super.ruleAdded(rule);
		enableActions(true);
	}
	
	@Override
	public void batchUpdate(int size) {
		super.batchUpdate(size);
		if (size > 0)
			enableActions(true);
	}
	
	@Override
	public void setInputForTable(RuleManager manager) {
		super.setInputForTable(manager);
		enableActions(manager.getNumberOfRules() > 0);
	}
	
}
