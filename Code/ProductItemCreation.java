package com.fplm.teamcenter.fplmoperations.widgets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class ProductItemCreation extends TitleAreaDialog {

	private static Set<Object> propFileValue;
	private static Properties prop;
	private Text m_txtItemName;
	private static Combo m_txtModelCode;
	private Combo m_txtTypeoffile;
	private Combo m_txtProductCode;
	private Combo m_txtCategoryCode;
	private Combo m_txtItemType;

	TCComponentItemType itemtype = null;
	IWorkbenchWindow window = null;
	int iQueryCount = 0;
	AbstractAIFUIApplication app = null;
	TCSession session = null;
	TCComponentQueryType queryTtype = null;
	TCComponent tccomponent = null;
	TCComponentQuery query = null;
	TCComponent[] queryResult = null;
	TCComponentUser user = null;
	TCComponentFolder homecontainer = null;
	private String sProductId;
	private String[] sSplitedValueArr;
	private String sProductName;
	private TCComponent[] ProductComp;
	private TCComponentItem createItem;
	private String sProductIdForQuery;
	private String sFinalItemId;
	private String sNewCount;
	String sCode;
	ExecutionEvent event;
	
	public ProductItemCreation(Shell parentShell, TCSession session2, ExecutionEvent event) {
		super(parentShell);
		
		try {
			// Reading the property file from preference
			String sModelCodePrefValue = (String) getPreferenceData("SonaFoodModelCode");
			readPropertiesFile(sModelCodePrefValue);
			this.event = event;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*public ProductItemCreation(Shell parentShell, TCSession session) {

		super(parentShell);
		
		this.session = session;
	}*/

	

	public Object getPreferenceData(String sPreferenceName) {
		String sValueofPositon = null;
		try {
			
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			TCSession session = (TCSession) app.getSession();

			TCPreferenceService preferService1 = session.getPreferenceService();
			String[] sArrayPreferenceValue = preferService1
					.getStringValues(sPreferenceName);
			sValueofPositon = sArrayPreferenceValue[0];

		} catch (Exception e) {
			// TODO: handle exception
		}
		return sValueofPositon;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create Product Item");
		setMessage("This is a Product Item Create Dialog",
				IMessageProvider.INFORMATION);
	}

	private void createProductCode(Composite container) {
		Label lbProductCode = new Label(container, SWT.NONE);
		lbProductCode.setText("Product Code");
		GridData gProductCode = new GridData();
		gProductCode.grabExcessHorizontalSpace = true;
		gProductCode.horizontalAlignment = GridData.FILL;
		m_txtProductCode = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtProductCode.setLayoutData(gProductCode);
		m_txtProductCode.setText("Select Product Code");

	}

	private void createTypeofFile(Composite container) {
		Label lbTypeOfToolName = new Label(container, SWT.NONE);
		lbTypeOfToolName.setText("Type of File");
		GridData gTypeofTool = new GridData();
		gTypeofTool.grabExcessHorizontalSpace = true;
		gTypeofTool.horizontalAlignment = GridData.FILL;
		m_txtTypeoffile = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtTypeoffile.setLayoutData(gTypeofTool);
		m_txtTypeoffile.setText("Select Type of File");
		m_txtTypeoffile.setEnabled(false);
	}

	private void createModelCode(Composite container) {
		Label lbModelCode = new Label(container, SWT.NONE);
		lbModelCode.setText("Model Code");
		GridData gModelCode = new GridData();
		gModelCode.grabExcessHorizontalSpace = true;
		gModelCode.horizontalAlignment = GridData.FILL;
		m_txtModelCode = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtModelCode.setLayoutData(gModelCode);
		m_txtModelCode.setText("Select Model Code");
		m_txtModelCode.setEnabled(false);
	}

	private void createItemType(Composite container) {
		Label lbItemType = new Label(container, SWT.NONE);
		lbItemType.setText("Item Type");
		GridData gItemType = new GridData();
		gItemType.grabExcessHorizontalSpace = true;
		gItemType.horizontalAlignment = GridData.FILL;
		m_txtItemType = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtItemType.setLayoutData(gItemType);
		m_txtItemType.setText("Select Item Type");
		m_txtItemType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				m_txtTypeoffile.setEnabled(true);
				m_txtTypeoffile.removeAll();

				// Type of file combo value
				String sItemType = m_txtItemType.getText().toString().trim();
				String[] valueArr1 = getComboValues(sItemType);
				m_txtTypeoffile.setItems(valueArr1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void createCategoryCode(Composite container) {
		Label lbCategoryCode = new Label(container, SWT.NONE);
		lbCategoryCode.setText("Category Code");
		GridData gCategoryCode = new GridData();
		gCategoryCode.grabExcessHorizontalSpace = true;
		gCategoryCode.horizontalAlignment = GridData.FILL;
		m_txtCategoryCode = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtCategoryCode.setLayoutData(gCategoryCode);
		m_txtCategoryCode.setText("Select Category Code");
		m_txtCategoryCode.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				m_txtModelCode.setEnabled(true);
				if (m_txtModelCode.getItemCount() > 0)
					m_txtModelCode.removeAll();

				// Model code combo value
				sCode = m_txtCategoryCode.getText().toString().trim();
				String[] sArrayCategoryCode = getComboValues(sCode);
				m_txtModelCode.setItems(sArrayCategoryCode);
			}
		});
	}

	private void createName(Composite container) {
		Label lbName = new Label(container, SWT.NONE);
		lbName.setText("Name");
		GridData gName = new GridData();
		gName.grabExcessHorizontalSpace = true;
		gName.horizontalAlignment = GridData.FILL;
		m_txtItemName = new Text(container, SWT.BORDER);
		m_txtItemName.setLayoutData(gName);

	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		createName(container);
		createCategoryCode(container);
		createModelCode(container);
		createItemType(container);
		createTypeofFile(container);
		createProductCode(container);

		app = AIFUtility.getCurrentApplication();
		tccomponent = (TCComponent) AIFUtility.getTargetComponent();
			session = (TCSession) app.getSession();
		user = session.getUser();
		try {
			homecontainer = user.getHomeFolder();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// Category code combo value
		String[] sCategoryCodeComboArray = getComboValues("CategoryCode");
		m_txtCategoryCode.setItems(sCategoryCodeComboArray);

		// Product code combo value

		String[] sProductCodeComboArray = getComboValues("Product");
		m_txtProductCode.setItems(sProductCodeComboArray);

		// Item Type combo value

		String[] sItemTypeComboArray = getComboValues("ItemType");
		m_txtItemType.setItems(sItemTypeComboArray);

		return area;
	}

	@Override
	protected void okPressed() {
		if ((m_txtItemName.getText().equals(""))
				|| (m_txtTypeoffile.getText().equals("Select Type of File"))
				|| (m_txtItemType.getText().equals("Select Item Type"))
				|| (m_txtProductCode.getText().equals("Select Product Code"))
				|| (m_txtModelCode.getText().equals("Select Model Code"))
				|| (m_txtCategoryCode.getText().equals("Select Category Code"))
				|| (m_txtTypeoffile.getText().trim().equals(""))
				|| (m_txtItemType.getText().trim().equals(""))
				|| (m_txtProductCode.getText().trim().equals(""))
				|| (m_txtModelCode.getText().trim().equals(""))
				|| (m_txtCategoryCode.getText().trim().equals(""))) 
			{
				MessageDialog.openError(getParentShell(), "ERROR",
						"Please enter Valid Data");
			}
		else {

			try {
				itemtype = (TCComponentItemType) session
						.getTypeComponent("S2Machine_Design");

				String catCode = m_txtTypeoffile.getText().toString().trim();
				String catcd = catCode.split(" ")[0].trim();
				for (Object key : propFileValue) {

					String sCombokey = (String) key;
					if (sCombokey.equals(catcd)) {

						String sValueset = (String) prop.get(key);
						String[] sArrayValueset = (String[]) sValueset
								.split("=");
						if (sValueset != null && sValueset.length() > 0) {
							if (sValueset.contains(",")) {

								sSplitedValueArr = sValueset.split(",");
								for (int icnt = 0; icnt < sSplitedValueArr.length; icnt++) {
									if (sSplitedValueArr[icnt].contains("=")) {
										String[] str = sSplitedValueArr[icnt]
												.split("=");
										int arrLen = str.length;
										String sCounterStart = str[arrLen - 1].trim();

										int iSplitedValueArrLength = sSplitedValueArr.length;
										String sCounterEnd = sSplitedValueArr[iSplitedValueArrLength - 1].trim();

										// Category code
										String[] sCategoryCodeCombo = m_txtCategoryCode
												.getText().split(" ");
										String sCategoryCode = sCategoryCodeCombo[0].trim();

										// Item Type
										String[] sItemTypeCombo = m_txtItemType
												.getText().split(" ");
										String sItemType = sItemTypeCombo[0].trim();

										// Item ID before counter
										sProductId = m_txtProductCode.getText()
												+ sCategoryCode + "-"
												+ m_txtModelCode.getText()
												+ "-" + sItemType + "-"
												+ sCombokey;

										queryTtype = (TCComponentQueryType) session
												.getTypeComponent("ImanQuery");
										query = (TCComponentQuery) queryTtype
												.find("Item ID");
										String[] Entryname = { "Item ID" };
										sProductIdForQuery = sProductId + "*";
										String[] Entryval = { sProductIdForQuery };
										ProductComp = query.execute(Entryname,
												Entryval);

										int iQueryCount = ProductComp.length;
										if (iQueryCount == 0) {
											sProductId = sProductId + sCounterStart;
											sProductName = m_txtItemName.getText();
											createItem = itemtype.create(
													sProductId, "A",
													"S2Machine_Design",
													sProductName, "", null);
											homecontainer.add("contents",createItem);
											MessageDialog
													.openInformation(
															getParentShell(),
															"Item Creation Box",
															"Machine Design Item Created Sucessfully");
										} else {

											String FoundItemArray[] = null;
											FoundItemArray = new String[iQueryCount];
											for (int z = 0; z < iQueryCount; z++) {

												String FoundItemID = ProductComp[z]
														.toString();
												FoundItemArray[z] = FoundItemID;
											}
											Arrays.sort(FoundItemArray);
											int iSortArrayLen = FoundItemArray.length;
											String sLatestID = FoundItemArray[iSortArrayLen - 1];

											String[] getLatestCounter = sLatestID
													.split("-");
											int igetLatestCounterLen = getLatestCounter.length;
											String sLatestCounter = getLatestCounter[igetLatestCounterLen - 2];
											String sCounter = sLatestCounter.replaceAll("[^0-9]", "");
											int iCounter = Integer.parseInt(sCounter);

											// Check the last counter value is
											// less then end counter value
											if (iCounter < (Integer.parseInt(sCounterEnd) + 1)) {
												
												iCounter = iCounter + 1;
												String incNoStr="";
												if(sCombokey.equals("A")) 
													incNoStr = String.format("%01d",iCounter);
												else if(sCombokey.equals("SA")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("P")) 
													incNoStr = String.format("%03d",iCounter);
												else if(sCombokey.equals("PD")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("CL")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("FD")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("TS")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("TR")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("ML")) 
													incNoStr = String.format("%02d",iCounter);
												else if(sCombokey.equals("ML")) 
													incNoStr = String.format("%03d",iCounter);
												
												
												sFinalItemId = sProductId.concat(incNoStr);
												sProductName = m_txtItemName
														.getText();

												createItem = itemtype.create(
														sFinalItemId, "A",
														"S2Machine_Design",
														sProductName, "", null);
												homecontainer.add("contents",createItem);
												MessageDialog
														.openInformation(
																getParentShell(),
																"Item Creation Box",
																"Machine Design Item Created Sucessfully");
												

											} else {
												MessageDialog.openError(
														getParentShell(),
														"Error",
														"Counter limit exceed");
											}

										}
									}

								}
							} else
								sSplitedValueArr = new String[] { sValueset };
						}

					}
				}
			} catch (TCException e) {
				// TODO Auto-generated catch block
				String message = e.getMessage();
				if(message.contains("is not unique."))
				{
					MessageDialog.openError(getParentShell(),"Error","The Item "+sFinalItemId+" is not unique.");
				}
				e.printStackTrace();
			}

		}
	}

	// Function to read property file
	public static Properties readPropertiesFile(String fileName)
			throws IOException {
		FileInputStream fis = null;

		prop = null;
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);

			propFileValue = prop.keySet();

		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fis.close();
		}
		return prop;
	}

	// Function to get specific value from combo box for Item ID
	public static String[] getComboValues(String catCode) {
		String catcd = catCode.split(" ")[0].trim();
		String[] valueArr = null;
		for (Object key : propFileValue) {
			String combokey = (String) key;
			if (combokey.equals(catcd)) {
				String valueset = (String) prop.get(key);

				if (valueset != null && valueset.length() > 0) {
					if (valueset.contains(",")) {
						valueArr = valueset.split(",");
					} else
						valueArr = new String[] { valueset };
				}
				break;
			}
		}
		return valueArr;
	}
}