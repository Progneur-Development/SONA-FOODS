package com.fplm.teamcenter.fplmoperations.widgets;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
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

public class CreateLayoutDialog extends TitleAreaDialog {

	private Text m_txtItemName;
	private Combo m_txtLayoutCode;
	private Combo m_txtCustomerName;
	private Combo m_txtPlantName;
	private Combo m_txtCategoryCode;

	TCComponentItemType itemtype = null;
	IWorkbenchWindow window = null;
	int count = 0;
	int s = 0;
	String ProductId = null;
	String ProductDesc = null;
	String ProductCode = null;
	String PartType = null;
	String ProductName = null;
	String value = null;
	TCComponent[] ProductComp = null;
	AbstractAIFUIApplication app = null;
	TCSession session = null;
	TCComponentQueryType Qtype = null;
	TCComponent tccomponent = null;
	TCComponentQuery query = null;
	TCComponent[] comp = null;
	String FinalString = null;
	String FinalItemId = null;
	String StartCounter = null;
	int StartCountercounter = 0;
	TCComponent item = null;
	TCComponent createitem = null;
	TCComponentUser user = null;
	TCComponentFolder homecontainer = null;
	String arr[] = null;
	private String ProductId1;

	private Set<Object> PropFileAllKey;
	private Properties prop1;
	private FileInputStream fis;
	private String endCounter;
	ExecutionEvent event;

	public CreateLayoutDialog(Shell parentShell, TCSession session2, ExecutionEvent event) {
		super(parentShell);
		this.event = event;
	}

	public Object getPreferenceData(String prefName) {
		String str = null;
		try {
			TCPreferenceService preferService = session.getPreferenceService();
			String[] values = preferService.getStringValues(prefName);
			 str = values[0];
			System.out.println("values length==" + values.length);
			System.out.println("values==" + values);
		
			} catch (Exception e) {
			// TODO: handle exception
			}
		return str;
	}

	
	
	@Override
	public void create() {
		super.create();
		setTitle("Create Layout Item");
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
		createName(container);
		container.setLayout(layout);
		LayoutCode(container);
		createCategoryCode(container);
		createCustomerName(container);
		createPlantname(container);
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
		 PropFileReader();//function call to read Property file
			
		 	String CatagoryType=prop1.getProperty("CategoryCode");
			String[] CatagoryTypeSplitedValue=CatagoryType.split(",");
			for (int i = 0;  i< CatagoryTypeSplitedValue.length; i++) 
			{
				m_txtCategoryCode.add(CatagoryTypeSplitedValue[i]);
				
				
				
			} 
			
			
			String CustomerName=prop1.getProperty("CustomerName");
			String[] CustomerNameSplitedValue=CustomerName.split(",");
			for (int i = 0; i < CustomerNameSplitedValue.length; i++) 
			{
				m_txtCustomerName.add(CustomerNameSplitedValue[i]);
			}
			
						
			String LayoutCode=prop1.getProperty("LayoutCode");
			String[] LayoutCodeSplitedValue=LayoutCode.split(",");
			if (LayoutCodeSplitedValue.length==1)
			{
				m_txtLayoutCode.add(LayoutCode);
				
			}
			else {
				for (int i = 0;  i< LayoutCodeSplitedValue.length; i++) {
					m_txtLayoutCode.add(LayoutCodeSplitedValue[i]);
				}
			}
			
			String PlantName=prop1.getProperty("PlantName");
			String[] PlantNameSplitedValue=PlantName.split(",");
			for (int i = 0;  i< PlantNameSplitedValue.length; i++) 
			{
				m_txtPlantName.add(PlantNameSplitedValue[i]);
			}
			m_txtCategoryCode.setEnabled(false);
			m_txtCustomerName.setEnabled(false);
			m_txtPlantName.setEnabled(false);
			FiledEvents();
				
		return area;
	}

	void FiledEvents()
	{

		try
		{
			m_txtLayoutCode.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					m_txtCategoryCode.setEnabled(true);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			m_txtCategoryCode.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
				
					m_txtCustomerName.setEnabled(true);
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			m_txtCustomerName.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					
					m_txtPlantName.setEnabled(true);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}

	private void createCategoryCode(Composite container) {
		Label lbCategoryCode = new Label(container, SWT.NONE);
		lbCategoryCode.setText("Category Code");
		GridData dataCategoryCode = new GridData();
		dataCategoryCode.grabExcessHorizontalSpace = true;
		dataCategoryCode.horizontalAlignment = GridData.FILL;
		m_txtCategoryCode = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtCategoryCode.setLayoutData(dataCategoryCode);
		m_txtCategoryCode.setText("Select Category Code");

	}

	private void createPlantname(Composite container) {
		Label lbtPlantname = new Label(container, SWT.NONE);
		lbtPlantname.setText("Plant Name");
		GridData dataPlantname = new GridData();
		dataPlantname.grabExcessHorizontalSpace = true;
		dataPlantname.horizontalAlignment = GridData.FILL;
		m_txtPlantName = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtPlantName.setLayoutData(dataPlantname);
		m_txtPlantName.setText("Select Plant Name");
	}

	private void createCustomerName(Composite container) {
		Label lbtCustomerName = new Label(container, SWT.NONE);
		lbtCustomerName.setText("Customer Name");
		GridData dataCustomerName = new GridData();
		dataCustomerName.grabExcessHorizontalSpace = true;
		dataCustomerName.horizontalAlignment = GridData.FILL;
		m_txtCustomerName = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtCustomerName.setLayoutData(dataCustomerName);
		m_txtCustomerName.setText("Select Customer Name");
	}

	private void LayoutCode(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Layout Code");
		GridData dataItemName = new GridData();
		dataItemName.grabExcessHorizontalSpace = true;
		dataItemName.horizontalAlignment = GridData.FILL;
		m_txtLayoutCode = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		m_txtLayoutCode.setLayoutData(dataItemName);
		m_txtLayoutCode.setText("Select Catagory Code");
	}

	private void createName(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Name");
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

		if (m_txtItemName.getText().equals("")
				|| m_txtLayoutCode.getText().equals("Select Catagory Code")
				|| m_txtPlantName.getText().equals("Select Type of File")
				|| m_txtCustomerName.getText().equals("Select Client Name") 
				|| m_txtCategoryCode.getText().equals("Select Category Code")
				|| m_txtLayoutCode.getText().trim().equals("")
				|| m_txtPlantName.getText().trim().equals("")
				|| m_txtCustomerName.getText().trim().equals("") 
				|| m_txtCategoryCode.getText().trim().equals("")) 
		
			{
				MessageDialog.openError(getParentShell(), "ERROR",
						"Please enter Valid Data");
			}
		 else {
			try {
				itemtype = (TCComponentItemType) session.getTypeComponent("S2Layout");
				
				String PlantNameText=m_txtPlantName.getText();
				String PlantNameSplited=PlantNameText.split(" ")[0].trim();

				//typeOfFile = m_txtTypeoffile.getText();
				
				for (Object key: PropFileAllKey) {
					String keyValue=key.toString();
					if (keyValue.equals(PlantNameSplited)) {
						
						//String str=(String)prop1.get(key);
						
						
						String CounterString=prop1.getProperty(PlantNameSplited);
						String[] SplitedCounter=CounterString.split("=");
						String Counter=SplitedCounter[(SplitedCounter.length-1)];
						String[] CounterSplitedValue=Counter.split(",");
						StartCounter = CounterSplitedValue[0];
						endCounter=CounterSplitedValue[1];
						
						String CateogryCodeText=m_txtCategoryCode.getText();
						String[] CateogryCodeSplited=CateogryCodeText.split(" ");
						
						String CustomerNameText=m_txtCustomerName.getText();
						String[] CustomerNameSplited=CustomerNameText.split(" ");
						
						ProductId = m_txtLayoutCode.getText()+CateogryCodeSplited[0] + "-" + CustomerNameSplited[0] + "-" + PlantNameSplited;
						System.out.println("Product ID:" + ProductId);
						
						ProductName = m_txtItemName.getText();
						System.out.println("Product Name:" + ProductName);
						
						Qtype=(TCComponentQueryType) session.getTypeComponent("ImanQuery");
						query=(TCComponentQuery) Qtype.find("Item ID");
						String[] Entryname={"Item ID"};
						ProductId1=ProductId+"*";
						String[] EntryValue={ProductId1};
						ProductComp=query.execute(Entryname,EntryValue);

						if (ProductComp.length==0) 
						{
							ProductId = ProductId.concat(StartCounter);
							createitem = itemtype.create(ProductId, "A"
									,"S2Layout", ProductName, "", null);
							 homecontainer.add("contents",createitem);
							MessageDialog.openInformation(getParentShell()
									, "Item Creation Box"
									, "Layout Item Created Sucessfully");
						}
						else 
						{
							int n=ProductComp.length;
							String SArray[]=new String[n];
							for (int z = 0; z < n; z++) {
								String Str=ProductComp[z].toString();
								SArray[z]=Str;
						}
						Arrays.sort(SArray);
						String LastGeneratedID= SArray[SArray.length-1];
						
						String[] splitedValueArray=LastGeneratedID.split("-");
						String sLatestCounter=splitedValueArray[splitedValueArray.length-2];
						
						String latestcounter=sLatestCounter.replaceAll("[A-Za-z]", "").trim();
						int ilatestcounter=Integer.parseInt(latestcounter);
						ilatestcounter++;
						
						if (ilatestcounter<=Integer.parseInt(endCounter)) {
							String Sr =String.format("%03d", ilatestcounter);
							FinalItemId=ProductId.concat(Sr);
							createitem = itemtype.create(FinalItemId,"A","S2Layout", ProductName, "", null);
							homecontainer.add("contents",createitem);
							MessageDialog.openInformation(getParentShell()
									, "Item Creation Box"
									, "Job_Work Item Created Sucessfully");
						}
						else
							MessageDialog.openError(getParentShell()
							, "Error to Create Item"
							, "Counter limit Exceed");

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
	
	

	public Properties PropFileReader()
	{
		String PropFileValue=(String) getPreferenceData("SonaPropertFilePathForLayout");
		try  {

			fis=new FileInputStream(PropFileValue);
			prop1=new Properties();
			// load a properties file
			prop1.load(fis);
			
			PropFileAllKey=prop1.keySet();
		
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	
		return prop1;
	}	
}
