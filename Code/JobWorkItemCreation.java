package com.fplm.teamcenter.fplmoperations.widgets;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class JobWorkItemCreation extends TitleAreaDialog {

	private Text m_txtItemName;
	private Combo m_txtClientName;
	private Combo m_txtTypeoffile;
	private Combo m_txtCategoryCode;
	private String sQueryInputProductId;
	private Set<Object> PropFileAllKey;
	private Properties prop;
	private FileInputStream fis;
	private String sEndCounter;

	TCComponentItemType itemtype = null;
	IWorkbenchWindow window = null;
	TCComponent[] ProductComp = null;
	AbstractAIFUIApplication app = null;
	TCSession session = null;
	TCComponentQueryType Qtype = null;
	TCComponent tccomponent = null;
	TCComponentQuery query = null;
	TCComponent[] comp = null;
	TCComponent item = null;
	TCComponent createitem = null;
	TCComponentUser user = null;
	TCComponentFolder homecontainer = null;

	String sProductId = null;
	String sProductName = null;
	String sFinalString = null;
	String sFinalItemId = null;
	String sStartCounter = null;

	public JobWorkItemCreation(Shell parentShell, TCSession session) {

		super(parentShell);
		this.session = session;

		// Reading the property file from preference
		PropFileReader();
	}

	// Function to get data from particular preference
	public Object getPreferenceData(String prefName) {
		String str = null;
		try {
			TCPreferenceService preferService = session.getPreferenceService();
			String[] values = preferService.getStringValues(prefName);
			str = values[0];
		} catch (Exception e) {
			// TODO: handle exception
		}
		return str;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create Job Work Item");
		setMessage("This is a Job Work Item Create Dialog",
				IMessageProvider.INFORMATION);
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
		createClientName(container);
		createTypeofFile(container);
		createCategoryCode(container);
		app = AIFUtility.getCurrentApplication();
		tccomponent = (TCComponent) AIFUtility.getTargetComponent();
		session = (TCSession) app.getSession();
		user = session.getUser();
		try {
			homecontainer = user.getHomeFolder();
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PropFileReader();// function call to read Property file

		String sClientName = prop.getProperty("ClientName");
		String[] CleintNameSplitedValue = sClientName.split(",");

		for (int i = 0; i < CleintNameSplitedValue.length; i++) {
			m_txtClientName.add(CleintNameSplitedValue[i]);
		}

		String sFileType = prop.getProperty("TypeOfFile");

		String[] FileTypeSplitedValue = sFileType.split(",");

		for (int i = 0; i < FileTypeSplitedValue.length; i++) {
			m_txtTypeoffile.add(FileTypeSplitedValue[i]);
		}

		String sCatagoryType = prop.getProperty("CategoryCode");
		String[] CatagoryTypeSplitedValue = sCatagoryType.split(",");
		if (CatagoryTypeSplitedValue.length == 1) {
			m_txtCategoryCode.add(sCatagoryType);

		} else {
			for (int i = 0; i < CatagoryTypeSplitedValue.length; i++) {
				m_txtCategoryCode.add(CatagoryTypeSplitedValue[i]);
			}
		}
		m_txtCategoryCode.setEnabled(false);
		m_txtTypeoffile.setEnabled(false);
		FiledEvents();
		return area;
	}

	private void FiledEvents() {
		m_txtClientName.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				m_txtTypeoffile.setEnabled(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		m_txtTypeoffile.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				m_txtCategoryCode.setEnabled(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void createCategoryCode(Composite container) {
		Label lbOperation = new Label(container, SWT.NONE );
		lbOperation.setText("Category Code");
		GridData dataOperation = new GridData();
		dataOperation.grabExcessHorizontalSpace = true;
		dataOperation.horizontalAlignment = GridData.FILL;
		m_txtCategoryCode = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtCategoryCode.setLayoutData(dataOperation);
		m_txtCategoryCode.setText("Select Category Code");

	}

	private void createTypeofFile(Composite container) {
		Label lbTypeofFile = new Label(container, SWT.NONE);
		lbTypeofFile.setText("Type of File");
		GridData dataTypeofTool = new GridData();
		dataTypeofTool.grabExcessHorizontalSpace = true;
		dataTypeofTool.horizontalAlignment = GridData.FILL;
		m_txtTypeoffile = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtTypeoffile.setLayoutData(dataTypeofTool);
		m_txtTypeoffile.setText("Select Type of File");
	}

	private void createClientName(Composite container) {
		Label lbFirstName = new Label(container, SWT.NONE);
		lbFirstName.setText("Client Name");
		GridData dataItemID = new GridData();
		dataItemID.grabExcessHorizontalSpace = true;
		dataItemID.horizontalAlignment = GridData.FILL;
		m_txtClientName = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtClientName.setLayoutData(dataItemID);
		m_txtClientName.setText("Select Client Name");
	}

	private void createName(Composite container) {
		Label lbCreateName = new Label(container, SWT.NONE);
		lbCreateName.setText("Name");
		GridData dataItemName = new GridData();
		dataItemName.grabExcessHorizontalSpace = true;
		dataItemName.horizontalAlignment = GridData.FILL;
		m_txtItemName = new Text(container, SWT.BORDER);
		m_txtItemName.setLayoutData(dataItemName);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		if ((m_txtItemName.getText().equals(""))
				|| (m_txtTypeoffile.getText().equals("Select Type of File"))
				|| (m_txtClientName.getText().equals("Select Client Name"))
				|| (m_txtCategoryCode.getText().equals("Select Category Code"))
				|| (m_txtTypeoffile.getText().trim().equals(""))
				|| (m_txtClientName.getText().trim().equals(""))
				|| (m_txtCategoryCode.getText().trim().equals(""))) 
			{
				MessageDialog.openError(getParentShell(), "ERROR",
						"Please enter Valid Data");
			
		} else {
			try {
				itemtype = (TCComponentItemType) session
						.getTypeComponent("S2Job_Work");

				String sTypeOfFile = m_txtTypeoffile.getText();
				String sTypeOfFileSplited = sTypeOfFile.split(" ")[0].trim();
				for (Object key : PropFileAllKey) {
					String sKeyValue = key.toString();
					if (sKeyValue.equals(sTypeOfFileSplited)) {

						String sCounterString = prop
								.getProperty(sTypeOfFileSplited);
						String[] SplitedCounter = sCounterString.split("=");
						String sCounter = SplitedCounter[(SplitedCounter.length - 1)];
						String[] CounterSplitedValue = sCounter.split(",");
						sStartCounter = CounterSplitedValue[0].trim();
						sEndCounter = CounterSplitedValue[1].trim();

						sProductId = m_txtCategoryCode.getText().trim()
								+ m_txtClientName.getText().trim()+ "-"
								+ sTypeOfFileSplited.trim();

						System.out.println("Product ID:" + sProductId);
						sProductName = m_txtItemName.getText().trim();
						Qtype = (TCComponentQueryType) session
								.getTypeComponent("ImanQuery");
						query = (TCComponentQuery) Qtype.find("Item ID");
						String[] Entryname = { "Item ID" };
						sQueryInputProductId = sProductId + "*";
						String[] EntryValue = { sQueryInputProductId };
						ProductComp = query.execute(Entryname, EntryValue);

						if (ProductComp.length == 0) {
							sProductId = sProductId.concat(sStartCounter);
							createitem = itemtype.create(sProductId, "A",
									"S2Job_Work", sProductName, "", null);
							 homecontainer.add("contents",createitem);
							MessageDialog.openInformation(getParentShell(),
									"Item Creation Box",
									"Job Work Item Created Sucessfully");
						} else {
							String SArray[] = new String[ProductComp.length];
							for (int z = 0; z < ProductComp.length; z++) {
								String Str = ProductComp[z].toString();
								SArray[z] = Str;
							}
							Arrays.sort(SArray);
							String sLastGeneratedID = SArray[SArray.length - 1];

							String[] splitedValueArray = sLastGeneratedID
									.split("-");
							String sLatestCounter = splitedValueArray[splitedValueArray.length - 2];

							String sCurrentCounter = sLatestCounter.replaceAll(
									"[A-Za-z]", "").trim();
							int iLatestCounter = Integer.parseInt(sCurrentCounter);
							iLatestCounter++;

							if (iLatestCounter <= Integer.parseInt(sEndCounter)) {
								String Sr = String.format("%02d",
										iLatestCounter);
								sFinalItemId = sProductId.concat(Sr);
								createitem = itemtype.create(sFinalItemId, "A",
										"S2Job_Work", sProductName, "", null);
								 homecontainer.add("contents",createitem);
								MessageDialog.openInformation(getParentShell(),
										"Item Creation Box",
										"Job Work Item Created Sucessfully");
							} else
								MessageDialog.openError(getParentShell(),
										"Error to Create Item",
										"Counter limit Exceed");
						}
					}
				}
			}

			catch (TCException e1) {
				e1.printStackTrace();
			}
			super.okPressed();
		}
	}

	public Properties PropFileReader() {
		String PropFileValue = (String) getPreferenceData("SonaPropertFilePath");
		try {

			fis = new FileInputStream(PropFileValue);
			prop = new Properties();
			// load a properties file
			prop.load(fis);

			PropFileAllKey = prop.keySet();

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}

		return prop;
	}

}
