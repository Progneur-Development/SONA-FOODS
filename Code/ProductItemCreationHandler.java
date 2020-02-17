package com.fplm.teamcenter.fplmoperations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.fplm.teamcenter.fplmoperations.widgets.CreateLayoutDialog;
import com.fplm.teamcenter.fplmoperations.widgets.JobWorkItemCreation;
import com.fplm.teamcenter.fplmoperations.widgets.ProductItemCreation;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCSession;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ProductItemCreationHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public ProductItemCreationHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		
/*		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelection iselecobj=HandlerUtil.getCurrentSelection(event);
		AIFComponentContext contex=(AIFComponentContext) ((IStructuredSelection)iselecobj).getFirstElement();
		TCComponent tccompo=(TCComponent) contex.getComponent();
		TCSession session=tccompo.getSession();*/
//working
		//JobWorkItemCreation dialog = new JobWorkItemCreation(window.getShell(),session);		
		ProductItemCreation dialog = new ProductItemCreation(window.getShell(),session , event);
	//	working
	//	CreateLayoutDialog dialog = new CreateLayoutDialog(window.getShell(),session);	
				
		if(dialog.open()==1)
		{
			AbstractAIFOperation createItemOp = new AbstractAIFOperation() {

				@Override
				public void executeOperation() throws Exception {
					// TODO Auto-generated method stub
					
				}
			};		
		}
		return null;
	}
}
