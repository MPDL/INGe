package de.mpg.escidoc.services.citationmanager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import net.sf.jasperreports.engine.JRException;

import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;



public class ReportingGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final String CITATION_ELEMENTS_XML_SCHEMA_FILE = "Schemas\\CitationElements.xsd";  //  @jve:decl-index=0:

	 private javax.swing.JPanel jContentPane = null;

	    private JPanel panel = null; // First Panel

	    private JPanel panel1 = null; // Second Panel

	    private JButton editjButton = null; // Edit Button

	    private JButton newjButton = null; // New Button

	    private JButton canceljButton = null; // Cancel Button

	    private JButton backjButton = null; // Back Button

	    private JLabel namejLabel = null; // Name Label for First Panel

	    private JLabel namejLabel1 = null; // Name Label for Second Panel

	    private JTextField namejTextField = null; // Name Text Field for First

	    // Panel

	    private JTextField namejTextField1 = null; // Name Text Field for Second

	    // Panel

	    private JCheckBox selectjCheckBox = null; // Select Check Box for First

	    // Panel

	    private JLabel multiplejLabel = null; // Multiple Label for First Panel

	    private JLabel selectjLabel = null; // Select Label for First Panel

	    private JLabel elementjLabel = null; // Element Label for Second Panel

	    private JComboBox citationjComboBox = null; // Citation Combo Box for First

	    // Panel

	    private JComboBox elementjComboBox = null; // Citation Combo Box for Second

	    // Panel

	    private JButton addtypejButton = null; // Add Type Button for First Panel

	    private JButton addelementjButton = null; // Add Element Button for Second

	    // Panel

	    private JButton removetypejButton = null; // Remove Type Button for First

	    // Panel

	    private JButton removeelementjButton = null; // Remove Element Button for

	    // Second Panel

	    private JLabel parametersjLabel = null; // Parameter Label for both Panels

	    private JLabel delimiterjLabel = null; // Delimiter Label for both Panels

	    private JTextField delimiterjTextField = null; // Delimiter Text Field for

	    // First Panel

	    private JTextField delimiterjTextField1 = null; // Delimiter Text Field for

	    // Second Panel

	    private JLabel startswithjLabel = null; // Start With Label for both Panels

	    private JTextField startswithjTextField = null; // Start With Text Field for

	    // First Panel

	    private JTextField startswithjTextField1 = null; // Start With Text Field

	    // for Second Panel

	    private JLabel endswithjLabel = null; // Ends With Label for both Panels

	    private JLabel maxlengthjLabel = null; // Max Length Label for both Panels

	    private JTextField endswithjTextField = null; // Ends With Text Field for

	    // First Panel

	    private JTextField endswithjTextField1 = null; // Ends With Text Field for

	    // Second Panel

	    private JTextField maxlengthjTextField = null; // Max Length Text Field for

	    // First Panel

	    private JTextField maxlengthjTextField1 = null; // Max Length Text Field for

	    // Second Panel

	    private JLabel maxlengthendswithjLabel = null; // Max Length Ends With

	    // Label for both Panels

	    private JTextField maxlengthendswithjTextField = null; // Max Length Ends

	    // With Text Field
	    // for First Panel

	    private JTextField maxlengthendswithjTextField1 = null; // Max Length Ends

	    // With Text Field
	    // for Second Panel

	    private JLabel fontjLabel = null; // Font Label for both panels

	    private JComboBox citationjComboBox1 = null; // Font Combo Box for first

	    // panel

	    private JComboBox fontjComboBox = null; // Font Combo Box for first panel

	    private JComboBox fontjComboBox1 = null; // Font Combo Box for Second

	    // panel

	    private JComboBox fontjComboBox2 = null;

	    private JLabel citationelementsjLabel = null; // Citation Element Label

	    // for First panel

	    private JLabel citationelementsjLabel1 = null; // Citation Element Label

	    // for Second panel

	    private JScrollPane citationjScrollPane = null; // ScrollPane for First

	    // panel

	    private JScrollPane citationjScrollPane1 = null; // ScrollPane for Second

	    // panel

	    private JTable citationjTable = null; // Tabel for First panel

	    private JTable citationjTable1 = null; // Tabel for Second panel

	    private JButton addbeforejButton = null; // Add before Button for First

	    // panel

	    private JButton addbeforejButton1 = null; // Add before Button for Second

	    // panel

	    private JButton addafterjButton = null; // Add after Button for First panel

	    private JButton addafterjButton1 = null; // Add after Button for Second

	    // panel

	    private JButton upjButton = null; // Up Button for First panel

	    private JButton upjButton1 = null; // Up Button for Second panel

	    private JButton downjButton = null; // Down Button for First panel

	    private JButton downjButton1 = null; // Down Button for Second panel

	    private JButton removejButton = null; // Remove Button for First panel

	    private JButton removejButton1 = null; // Remove Button for Second panel

	    private JButton savejButton = null; // Save Button for First panel

	    private JButton savejButton1 = null; // Save Button for Second panel

	    private JRadioButton CitationStyle_Default = null; // Default Radio Button

	    private JRadioButton CitationStyle_eDoc = null; // eDoc Radio Button

	    private JRadioButton CitationStyle_eSciDoc = null; // eSciDoc Radio Button

	    private ButtonGroup buttonGroup = null; // Button Group

	    private DefaultTableModel model = null; // Default Table Model for First

	    // panel

	    private DefaultTableModel model1 = null; // Default Table Model for

	    // Second panel

	    private DefaultTableModel model2 = null; // Default Table Model for First

	    // panel

	    private DefaultTableModel model3 = null; // Default Table Model for

	    // Second panel

	    private String citationSelection = null; // Citation Selection

	    private String citationSelection1 = null; // Citation Selection

	    private String citationSelection2 = null; // Citation Selection

	    private String elementSelection = null; // Element Selection

	    private String fontSelection = null; // Font Selection for First panel

	    private String fontSelection1 = null; // Font Selection for Second panel

	    private JLabel defaultjLabel = null; // Default Label

	    private JLabel edocjLabel = null; // eDoc Label

	    private JLabel escidocjLabel = null; // eSciDoc Label

	    private JFrame frame; // Frame

	    private JButton layoutelementsjButton = null;

	    private JLabel referencejLabel = null; // Reference Label

	    private JComboBox referncejComboBox = null; // Reference Combo Box for First

	    // panel

	    private JComboBox referncejComboBox1 = null; // Reference Combo Box for

	    // Second panel

	    private JComboBox referncejComboBox2 = null; // Reference Combo Box for

	    // Second panel

	    private JLabel validifjLabel = null; // Valid If Label for both panels

	    private JTextField validifjTextField = null; // Valid If Text Field for

	    // First panel

	    private JTextField validifjTextField1 = null; // Valid If Text Field for

	    // Second panel

	    private JTable parametersjTable = null;

	    // Variable for Addbefore and Addafer panel
	    private JPanel addnewcitationelementjPanel = null;  //  @jve:decl-index=0:visual-constraint="668,670"

	    private JButton selectalljButton = null;

	    private JButton deselectalljButton = null;

	    private JCheckBox[] jCheckBox = new JCheckBox[8];
	    
	 //   private JLabel[] jCheckBoxLabel = new JLabel[8];
	    
	    
//	    private JLabel typecitationjLabel = null;
	//
//	    private JLabel nameofeventjLabel = null;
	//
//	    private JLabel dateofeventjLabel = null;
	//
//	    private JLabel placeofeventjLabel = null;
	//
//	    private JLabel publisherjLabel = null;
	//
//	    private JLabel pagejLabel = null;
	//
//	    private JLabel yearjLabel = null;
	    

	    private JButton addtocitationstylejButton = null;
	    private JButton canceselectionljButton = null;
	    
	    boolean addbefore, addafter;

	    boolean article, book;

	    private JRadioButton jrb = null;

	    private JLabel jl = null;

	    private Vector v1, v2, v3 = null;

	    private boolean changed = false;

	    // Variables for processing

	    private CitationStylesCollection csc = null;  //  @jve:decl-index=0:

	    private CitationStyle cs = null;  //  @jve:decl-index=0:

	    private CitationStyle csnew = null;

	    private Parameters p = null;

	    private LayoutElement le = null;

	    private LayoutElement lenew = null;

	    private LayoutElement csld = null;

	    private LayoutElementsCollection lec = null;  //  @jve:decl-index=0:

	    private JPanel newtypejPanel = null;  //  @jve:decl-index=0:visual-constraint="288,683"

	    private JLabel newtypenamejLabel = null;

	    private JTextField newtypenamejTextField = null;

	    private JLabel newtypeselectjLabel = null;

	    private JComboBox newtypejComboBox = null;

	    private JButton newtypecanceljButton = null;

	    private JButton newtypeokjButton = null;

	    private int num = 0;

	    private JPanel addnewcsjPanel = null;  //  @jve:decl-index=0:visual-constraint="-31,756"
	    private JLabel newcsnamejLabel = null;
	    private JTextField newcsnamejTextField = null;
	    private JLabel usecsastemplatejLabel = null;
	    private JComboBox selectcsastemplatejComboBox = null;
	    private JButton addnewcscancejButton = null;
	    private JButton addnewcscreatejButton = null;
	    private JLabel citationstyleselectionjLabel = null;
	    private JLabel selectdatasourcejLabel = null;
	    private JLabel selectxmlfilejLabel = null;
	    private JTextField xmlfilejTextField = null;
	    private JButton selectjButton = null;
	    private JButton validatefilejButton = null;
	    private JButton processesfilejButton = null;
	    private JLabel viewoutputjLabel = null;
	    private JComboBox viewoutputjComboBox = null;
	    private JButton runoutputjButton = null;
	    private String jrxmlFileName, xmlFileName, outPutFormat = null;
	    private JButton valiadatejButton = null;
	    private JLabel addnewcitationstylejLabel = null;

	    private JLabel addnewcitationstylejLabel1 = null;

	    private JLabel addnewtypejLabel = null;
	    private JComboBox listofcitationstylesjComboBox = null;

	    private String selectedCS = null;

	    private String pathToAPA = "CitationStyles\\APA\\";  //  @jve:decl-index=0:
	    
	    private String pathToDefault = "CitationStyles\\Default\\";  //  @jve:decl-index=0:
	    
	    private CitationStyle ctc = null;
	    
	    private ArrayList<LayoutElement> tblElems = new ArrayList<LayoutElement>();  //  @jve:decl-index=0:
	    
	    private ArrayList colNames = new ArrayList();  //  @jve:decl-index=0:

		private JLabel addnewtypecsastemplatejLabel = null;

		private JComboBox addnewtypecsastemplatejComboBox = null;

		private JList addlayoutelementsjList = null;  //  @jve:decl-index=0:visual-constraint="931,10"
		
		private DefaultListModel listModel;
		
		private JScrollPane addLayoutElementsjScrollPane = null;
	

		private JButton addNewEmptyTypejButton = null;

		private DefaultListModel listModel1 = null;

		private JLabel infoMessage1jLabel = null;

		private JLabel infoMessage2jLabel = null;

		/**
	     * This method initializes first panel (First Panel)
	     *
	     * @return java.awt.Panel
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JPanel getPanel() throws IOException, SAXException {
	        if (panel == null) {
	            citationelementsjLabel = new JLabel();
	            citationelementsjLabel.setBounds(new java.awt.Rectangle(15, 255,
	                    203, 29));
	            citationelementsjLabel.setFont(new java.awt.Font("Dialog",
	                    java.awt.Font.BOLD, 14));
	            citationelementsjLabel.setText(" Layout Elements ");
	            fontjLabel = new JLabel();
	            fontjLabel.setBounds(new Rectangle(15, 196, 35, 29));
	            fontjLabel.setText(" Font");
	            maxlengthendswithjLabel = new JLabel();
	            maxlengthendswithjLabel.setBounds(new Rectangle(630, 150, 121, 29));
	            maxlengthendswithjLabel.setText(" MaxLengthEndsWith");
	            maxlengthjLabel = new JLabel();
	            maxlengthjLabel.setBounds(new Rectangle(476, 152, 76, 27));
	            maxlengthjLabel.setText(" MaxLength");
	            endswithjLabel = new JLabel();
	            endswithjLabel.setBounds(new Rectangle(330, 151, 66, 27));
	            endswithjLabel.setText(" EndsWith");
	            startswithjLabel = new JLabel();
	            startswithjLabel
	                    .setBounds(new Rectangle(158, 152, 72, 28));
	            startswithjLabel.setText(" StartsWith");
	            delimiterjLabel = new JLabel();
	            delimiterjLabel.setBounds(new java.awt.Rectangle(17, 152, 57, 28));
	            delimiterjLabel.setText(" Delimiter");
	            parametersjLabel = new JLabel();
	            parametersjLabel
	                    .setBounds(new Rectangle(300, 105, 330, 30));
	            parametersjLabel.setFont(new java.awt.Font("Dialog",
	                    java.awt.Font.BOLD, 14));
	            parametersjLabel.setText("  Article Layout Parameters");
	            selectjLabel = new JLabel();
	            selectjLabel.setBounds(new java.awt.Rectangle(16, 61, 118, 16));
	            selectjLabel.setText(" Select Citation Type");
	            multiplejLabel = new JLabel();
	            multiplejLabel.setBounds(new java.awt.Rectangle(376, 29, 132, 16));
	            multiplejLabel.setText(" Multiple Citation Types");
	            namejLabel = new JLabel();
	            namejLabel.setBounds(new java.awt.Rectangle(96, 29, 38, 16));
	            namejLabel.setText(" Name");
	            // referencejLabel = new JLabel();
	            // referencejLabel.setBounds(new java.awt.Rectangle(151,151,68,29));
	            // referencejLabel.setText(" Reference");
	            validifjLabel = new JLabel();
	            validifjLabel.setBounds(new Rectangle(194, 196, 50, 29));
	            validifjLabel.setText("  ValidIF");
	            panel = new JPanel();
	            panel.setLayout(null);
	            panel.setBounds(0, 1, 900, 616);
	            panel.setName("panel1");
	            panel.add(getButton2(), null);
	            panel.add(namejLabel, null);
	            panel.add(getNamejTextField(), null);
	            panel.add(getSelectjCheckBox(), null);
	            panel.add(multiplejLabel, null);
	            panel.add(selectjLabel, null);
	            panel.add(getCitationjComboBox(), null);
	            panel.add(getAddtypejButton(), null);
	            panel.add(getRemovetypejButton(), null);
	            panel.add(parametersjLabel, null);
	            panel.add(delimiterjLabel, null);
	            panel.add(getDelimiterjTextField(), null);
	            panel.add(startswithjLabel, null);
	            panel.add(getStartswithjTextField(), null);
	            panel.add(endswithjLabel, null);
	            panel.add(maxlengthjLabel, null);
	            panel.add(getEndswithjTextField(), null);
	            panel.add(getMaxlengthjTextField(), null);
	            panel.add(maxlengthendswithjLabel, null);
	            panel.add(getMaxlengthendswithjTextField(), null);
	            panel.add(fontjLabel, null);
	            panel.add(getFontjComboBox(), null);
	            panel.add(citationelementsjLabel, null);
	            panel.add(getCitationjScrollPane(), null); // table of layoutElements!!!
	            panel.add(getAddbeforejButton(), null);
	            panel.add(getAddafterjButton(), null);
	            panel.add(getUpjButton(), null);
	            panel.add(getDownjButton(), null);
	            panel.add(getRemovejButton(), null);
	            panel.add(getValiadatejButton(), null);
	            panel.add(getSavejButton(), null);

	            // panel.add(getLayoutelementsjButton(), null);
	            // panel.add(referencejLabel, null);
	            // panel.add(getReferncejComboBox(), null);
	            panel.add(validifjLabel, null);
	            panel.add(getValidifjTextField(), null);

	        }
	        return panel;
	    }

	    /**
	     * This method initializes second panel (Second Panel)
	     *
	     * @return java.awt.Panel
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JPanel getPanel1() throws IOException, SAXException {
	        if (panel1 == null) {
	            citationelementsjLabel1 = new JLabel();
	            citationelementsjLabel1.setBounds(new java.awt.Rectangle(301, 256,
	                    203, 29));
	            citationelementsjLabel1.setFont(new java.awt.Font("Dialog",
	                    java.awt.Font.BOLD, 14));
	            citationelementsjLabel1.setText(" Layout Element consists of:");
	            fontjLabel = new JLabel();
	            fontjLabel.setBounds(new java.awt.Rectangle(415, 197, 35, 29));
	            fontjLabel.setText(" Font");
	            maxlengthendswithjLabel = new JLabel();
	            maxlengthendswithjLabel.setBounds(new java.awt.Rectangle(192, 196,
	                    121, 29));
	            maxlengthendswithjLabel.setText(" MaxLengthEndsWith");
	            maxlengthjLabel = new JLabel();
	            maxlengthjLabel.setBounds(new java.awt.Rectangle(16, 196, 76, 27));
	            maxlengthjLabel.setText(" MaxLength");
	            endswithjLabel = new JLabel();
	            endswithjLabel.setBounds(new java.awt.Rectangle(700, 150, 66, 27));
	            endswithjLabel.setText(" EndsWith");
	            startswithjLabel = new JLabel();
	            startswithjLabel
	                    .setBounds(new java.awt.Rectangle(540, 152, 76, 28));
	            startswithjLabel.setText(" StartsWith");
	            delimiterjLabel = new JLabel();
	            delimiterjLabel.setBounds(new java.awt.Rectangle(17, 152, 57, 28));
	            delimiterjLabel.setText(" Delimiter");
	            parametersjLabel = new JLabel();
	            parametersjLabel
	                    .setBounds(new java.awt.Rectangle(300, 105, 200, 30));
	            parametersjLabel.setFont(new java.awt.Font("Dialog",
	                    java.awt.Font.BOLD, 14));
	            parametersjLabel.setText("  Article Layout Parameters");
	            elementjLabel = new JLabel();
	            elementjLabel.setBounds(new java.awt.Rectangle(315, 31, 95, 16));
	            elementjLabel.setText(" Layout Element");
	            // multiplejLabel = new JLabel();
	            // multiplejLabel.setBounds(new java.awt.Rectangle(376,29,132,16));
	            // multiplejLabel.setText(" Multiple Citation Types");
	            namejLabel1 = new JLabel();
	            namejLabel1.setBounds(new java.awt.Rectangle(96, 29, 38, 16));
	            namejLabel1.setText(" Name");
	            referencejLabel = new JLabel();
	            referencejLabel.setBounds(new java.awt.Rectangle(151, 151, 68, 29));
	            referencejLabel.setText(" Reference");
	            validifjLabel = new JLabel();
	            validifjLabel.setBounds(new java.awt.Rectangle(386, 151, 50, 29));
	            validifjLabel.setText("  ValidIF");
	            panel1.setLayout(null);
	            panel1.setBounds(0, 1, 900, 616);
	            panel1.setName("panel1");
	            panel1.add(getButton3(), null);
	            panel1.add(namejLabel1, null);
	            panel1.add(getNamejTextField1(), null);
	            // panel1.add(getSelectjCheckBox(), null);
	            // panel1.add(multiplejLabel, null);
	            panel1.add(elementjLabel, null);
	            panel1.add(getCitationjComboBox1(), null);
	            panel1.add(getAddtypejButton1(), null);
	            panel1.add(getRemovetypejButton1(), null);
	            panel1.add(parametersjLabel, null);
	            panel1.add(delimiterjLabel, null);
	            panel1.add(getDelimiterjTextField1(), null);
	            panel1.add(startswithjLabel, null);
	            panel1.add(getStartswithjTextField1(), null);
	            panel1.add(endswithjLabel, null);
	            panel1.add(maxlengthjLabel, null);
	            panel1.add(getEndswithjTextField1(), null);
	            panel1.add(getMaxlengthjTextField1(), null);
	            panel1.add(maxlengthendswithjLabel, null);
	            panel1.add(getMaxlengthendswithjTextField1(), null);
	            panel1.add(fontjLabel, null);
	            panel1.add(getFontjComboBox1(), null);
	            panel1.add(citationelementsjLabel1, null);
	            panel1.add(getCitationjScrollPane1(), null);
	            panel1.add(getAddbeforejButton1(), null);
	            panel1.add(getAddafterjButton1(), null);
	            panel1.add(getUpjButton1(), null);
	            panel1.add(getDownjButton1(), null);
	            panel1.add(getRemovejButton1(), null);
	            panel1.add(getSavejButton1(), null);
	            panel1.add(referencejLabel, null);
	            panel1.add(getReferncejComboBox1(), null);
	            panel1.add(validifjLabel, null);
	            // panel1.add(getLayoutelementsjButton(), null);
	            panel1.add(getValidifjTextField1(), null);
	        }
	        return panel1;
	    }

	    /**
	     * This method initializes referncejComboBox for First Panel
	     *
	     * @return javax.swing.JComboBox
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JComboBox getReferncejComboBox() throws IOException, SAXException {
	        if (referncejComboBox == null) {
	            referncejComboBox = new JComboBox();
	            referncejComboBox.setBounds(new java.awt.Rectangle(226, 152, 151,
	                    28));
	            String name;

	            LayoutElementsCollection lec = LayoutElementsCollection
	                    .loadFromXml(pathToDefault + "LayoutElements.xml");

	            for (LayoutElement le : lec.getLayoutElements()) {
	                name = le.getName(); // put this in pull-down list
	                referncejComboBox.addItem(name);

	            }
	        }
	        return referncejComboBox;
	    }

	    /**
	     * This method initializes referncejComboBox for First Panel
	     *
	     * @return javax.swing.JComboBox
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JComboBox getCitationjComboBox2() throws IOException, SAXException {
	        if (citationjComboBox1 == null) {
	            citationjComboBox1 = new JComboBox();
	            citationjComboBox1.setBounds(new java.awt.Rectangle(226, 152, 151,
	                    28));
	            String name;

	            csc = CitationStylesCollection
	                    .loadFromXml("CitationStyles\\"+listofcitationstylesjComboBox.getSelectedItem()+"\\" + "CitationStyle.xml");

	            /*csc = CitationStylesCollection
	            .loadFromXml("New.xml");*/

	            for (CitationStyle cs : csc.getCitationStyles()) {
	                name = cs.getName(); // put this in pull-down list
	                citationjComboBox1.addItem(name);

	            }
	        }
	        return citationjComboBox1;
	    }

	    /**
	     * This method initializes referncejComboBox1 for Second Panel
	     *
	     * @return javax.swing.JComboBox
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JComboBox getReferncejComboBox1() throws IOException, SAXException {
	        if (referncejComboBox1 == null) {
	            referncejComboBox1 = new JComboBox();
	            referncejComboBox1.setBounds(new java.awt.Rectangle(226, 152, 151,
	                    28));
	            String name;

	            LayoutElementsCollection lec = LayoutElementsCollection
	                    .loadFromXml(pathToDefault + "LayoutElements.xml");

	            for (LayoutElement le : lec.getLayoutElements()) {
	                name = le.getName(); // put this in pull-down list
	                referncejComboBox1.addItem(name);

	            }
	        }
	        return referncejComboBox1;
	    }
	    
	    

	    /**
	     * This method initializes button for Frame
	     *
	     * @return java.awt.Button
	     */
	    private JButton getButton() {
	        if (editjButton == null) {
	            editjButton = new JButton();
	            editjButton.setBounds(new java.awt.Rectangle(370, 200, 77, 39));
	            editjButton.setText("Edit");
	            editjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    /*try {
	                        jContentPane.add(getPanel(), null);
	                    } catch (IOException e2) {

	                        e2.printStackTrace();
	                    } catch (SAXException e2) {

	                        e2.printStackTrace();
	                    }*/

	                	selectedCS = (String) listofcitationstylesjComboBox.getSelectedItem();

	                    if (selectedCS != null) {

	                        // panel1.setVisible(false);
	                        editjButton.setVisible(false);
	                        //CitationStyle_Default.setVisible(false);
	                        //CitationStyle_eDoc.setVisible(false);
	                        //CitationStyle_eSciDoc.setVisible(false);
	                        //edocjLabel.setVisible(false);
	                        //escidocjLabel.setVisible(false);
	                        //defaultjLabel.setVisible(false);
	                        listofcitationstylesjComboBox.setVisible(false);
	                        newjButton.setVisible(false);
	                        citationstyleselectionjLabel.setVisible(false);
	                        selectdatasourcejLabel.setVisible(false);
	                        selectxmlfilejLabel.setVisible(false);
	                        xmlfilejTextField.setVisible(false);
	                        selectjButton.setVisible(false);
	                        validatefilejButton.setVisible(false);
	                        //processesfilejButton.setVisible(false);
	                        viewoutputjLabel.setVisible(false);
	                        viewoutputjComboBox.setVisible(false);
	                        runoutputjButton.setVisible(false);
	                        panel.setVisible(true);

	                        
//	                        System.out.println(selectedCS);

	                        namejTextField.setText(selectedCS);

	                     
	                        String name;
	                        
	                        try {
	                            csc = CitationStylesCollection
	                                    .loadFromXml("CitationStyles\\"+selectedCS+"\\" + "CitationStyle.xml");
	                            /*csc = CitationStylesCollection
	                            .loadFromXml("New.xml");*/
	                            // System.out.println("csc="+csc);
	                        } catch (IOException e1) {
	                            e1.printStackTrace();
	                        } catch (SAXException e1) {
	                            e1.printStackTrace();
	                        }

	                        cs = csc.getCitationStyleByName(selectedCS);

	                        if (cs != null) {

	                            for (LayoutElement csld : cs
	                                    .getCsLayoutDefinitions()) {
	                                name = csld.getName(); // put this in pull-down
	                                // list
	                                citationjComboBox.addItem(name);

	                            }
	                        }

	                        if (listofcitationstylesjComboBox.getSelectedItem().equals("Default")) {
	                            selectjCheckBox.setEnabled(false);
	                            selectjLabel.setVisible(false);
	                            citationjComboBox.setVisible(false);
	                            addtypejButton.setVisible(false);
	                            removetypejButton.setVisible(false);
	                            selectjCheckBox.setSelected(false);
	                        } else {
	                            selectjCheckBox.setEnabled(true);
	                            selectjCheckBox.setFocusable(true);
	                            selectjLabel.setVisible(true);
	                            citationjComboBox.setVisible(true);
	                            addtypejButton.setVisible(true);
	                            removetypejButton.setVisible(true);
	                            selectjCheckBox.setSelected(true);
	                        }

	                    } else {
	                        // custom title, warning icon
	                        JOptionPane.showMessageDialog(frame,
	                                "Please select one Citation Style.",
	                                "Inane warning", JOptionPane.WARNING_MESSAGE);
	                    }
	                }
	            });
	        }
	        return editjButton;
	    }

	    /**
	     * This method initializes newjButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getNewjButton() {
	        if (newjButton == null) {
	            newjButton = new JButton();
	            newjButton.setBounds(new java.awt.Rectangle(470, 200, 77, 39));
	            newjButton.setText("New");
	            newjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {

	                    jContentPane.add(getAddnewcsjPanel(), null);

	                    addnewcsjPanel.setVisible(true);
	                    editjButton.setVisible(false);
	                    //CitationStyle_Default.setVisible(false);
	                    //CitationStyle_eDoc.setVisible(false);
	                    //CitationStyle_eSciDoc.setVisible(false);
	                    //edocjLabel.setVisible(false);
	                    //escidocjLabel.setVisible(false);
	                    //defaultjLabel.setVisible(false);
	                    listofcitationstylesjComboBox.setVisible(false);
	                    newjButton.setVisible(false);
	                    citationstyleselectionjLabel.setVisible(false);
	                    selectdatasourcejLabel.setVisible(false);
	                    selectxmlfilejLabel.setVisible(false);
	                    xmlfilejTextField.setVisible(false);
	                    selectjButton.setVisible(false);
	                    validatefilejButton.setVisible(false);
	                    //processesfilejButton.setVisible(false);
	                    viewoutputjLabel.setVisible(false);
	                    viewoutputjComboBox.setVisible(false);
	                    runoutputjButton.setVisible(false);
	                }
	            });
	        }
	        return newjButton;
	    }

	    /**
	     * This method initializes Cancel Button for First Panel
	     *
	     * @return java.awt.Button
	     */
	    private JButton getButton2() {
	        if (canceljButton == null) {
	            canceljButton = new JButton();
	            canceljButton.setBounds(28, 569, 74, 23);
	            canceljButton.setText("Cancel");
	            canceljButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            panel.setVisible(false);
	                            
	                            invokeStartPanel();
	                            
	                            citationjComboBox.removeAllItems();

	                        }
	                    });
	        }
	        return canceljButton;
	    }

	    /**
	     * This method initializes Back Button for Second Panel
	     *
	     * @return java.awt.Button
	     */
	    private JButton getButton3() {
	        if (backjButton == null) {
	            backjButton = new JButton();
	            backjButton.setBounds(28, 569, 74, 23);
	            backjButton.setText("Back");
	            backjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    // System.out.println("actionPerformed()Main-page");
	                    panel.setVisible(true);
	                    panel1.setVisible(false);

	                }
	            });
	        }
	        return backjButton;
	    }

	    /**
	     * This method initializes namejTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getNamejTextField() {
	        if (namejTextField == null) {
	            namejTextField = new JTextField();
	            namejTextField.setBounds(new java.awt.Rectangle(162, 27, 140, 20));
	            namejTextField.setEditable(false);

	        }
	        return namejTextField;
	    }

	    /**
	     * This method initializes namejTextField for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getNamejTextField1() {
	        if (namejTextField1 == null) {
	            namejTextField1 = new JTextField();
	            namejTextField1.setBounds(new java.awt.Rectangle(162, 27, 140, 20));
	        }
	        return namejTextField1;
	    }

	    /**
	     * This method initializes selectjCheckBox for First Panel
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getSelectjCheckBox() {
	        if (selectjCheckBox == null) {
	            selectjCheckBox = new JCheckBox();
	            selectjCheckBox.setBounds(new java.awt.Rectangle(329, 29, 21, 21));
	            // selectjCheckBox.setSelected(true);
	            selectjCheckBox
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            // System.out.println("actionPerformed()Main-page");
	                            JCheckBox jcb = (JCheckBox) e.getSource();
	                            // changed = true;
	                            // Determine status
	                            boolean isSel = jcb.isSelected();
	                            if (isSel) {
	                                // The checkbox is now deselected
	                                selectjLabel.setVisible(true);
	                                citationjComboBox.setVisible(true);
	                                addtypejButton.setVisible(true);
	                                removetypejButton.setVisible(true);

	                            } else {

	                                // The checkbox is now selected
	                                selectjLabel.setVisible(false);
	                                citationjComboBox.setVisible(false);
	                                addtypejButton.setVisible(false);
	                                removetypejButton.setVisible(false);
	                            }

	                            /*
	                             * panel.setVisible(false);
	                             * editjButton.setVisible(true);
	                             * edocjRadioButton.setVisible(true);
	                             * escidocjRadioButton.setVisible(true);
	                             * escidocjLabel.setVisible(true);
	                             * edocjLabel.setVisible(true);
	                             */
	                        }
	                    });
	        }
	        return selectjCheckBox;
	    }
	    
	    
	    

	    /**
	     * This method initializes citationjComboBox for First Panel
	     *
	     * @return javax.swing.JComboBox
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JComboBox getCitationjComboBox() throws IOException, SAXException {
	        if (citationjComboBox == null) {

	            citationjComboBox = new JComboBox();
	            citationjComboBox
	                    .setBounds(new java.awt.Rectangle(163, 59, 138, 25));
	            citationjComboBox.setEditable(true);
	            citationjComboBox
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {

	                            JComboBox cb = (JComboBox) e.getSource();
	                            citationSelection1 = (String) cb.getSelectedItem();

//	                            System.out.println("citationSelection1:"
//	                                    + citationSelection1);
	//
//	                           System.out.println(changed);
//	                           
	                           if(changed){

	                        	   //changed = false;
	                        	   int n = JOptionPane.showConfirmDialog(frame,
	                        			   "Would you like to Save the changes?",
	                        			   "Are you sure", JOptionPane.YES_NO_OPTION);
	                        	   if (n == 0) {
	                        		   getSavejButton();
	                        	   }
	                           }
	                           
	                           changed = false;

	                            //createCsldEditInterface(citationSelection1);

	                            parametersjLabel.setText(citationSelection1
	                                    + " Layout Parameters ");


	                            csld = cs.getElementByName(citationSelection1);
	                            
	                            if(citationSelection1!=null){
	                                le = csld.getElementByName(citationSelection1);
	                                p = le.getParametersAtDefault();
	                            }


	                            delimiterjTextField.setText(p.getDelimiter());
	                            validifjTextField.setText(p.getValidIf());
	                            startswithjTextField.setText(p.getStartsWith());
	                            endswithjTextField.setText(p.getEndsWith());
	                            maxlengthjTextField.setText(p.getMaxLength() + "");
	                            maxlengthendswithjTextField.setText(p
	                                    .getMaxLengthEndsWith());
	                            fontjComboBox.setSelectedItem(p.getFontStyleRef());

	                            ArrayList<LayoutElement> elems = (ArrayList<LayoutElement>) le
	                                    .getElementsAt("default");
	                            if (citationSelection1 != null) {

	                                int count = model.getRowCount();

	                                for (int i = 0; i < count; i++) {
	                                    model.removeRow(0);
	                                }

	                                tblElems.clear();
	                                
	                                int i = 1;

	                                for (LayoutElement elem : elems) {

	                                    Parameters elemp = elem.getParametersAtDefault();
                                    
                                    
	                                    model.insertRow(citationjTable
	                                            .getSelectedRow()
	                                            + i, new Object[] {
	                                            elem.getName(),
	                                            elem.getRef(),
	                                            elemp.getValidIf(),
	                                            elemp.getStartsWith(),
	                                            elemp.getEndsWith(),
	                                            elemp.getMaxLength(),
	                                            elemp.getMaxLengthEndsWith(),
	                                            elemp.getMaxCount(),
	                                            elemp.getMaxCountEndsWith(),
	                                            elemp.getDelimiter(),
	                                            elemp.getInternalDelimiter(),
	                                            elemp.getFontStyleRef() });
	                                    
	                                    tblElems.add(citationjTable.getSelectedRow()+i, elem );
	                                    i++;
	                                }
	                                changed = false;


	                            }

	                          }
	                    });

	        }
	        return citationjComboBox;
	    }

	    /**
	     * This method initializes citationjComboBox1 for Second Panel
	     *
	     * @return javax.swing.JComboBox
	     */
	    private JComboBox getCitationjComboBox1() {
	        if (elementjComboBox == null) {
	            String[] typeExamples = { "", "Article", "Book" };
	            elementjComboBox = new JComboBox(typeExamples);
	            elementjComboBox
	                    .setBounds(new java.awt.Rectangle(421, 30, 119, 28));
	            elementjComboBox.setEditable(true);
	            elementjComboBox
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            JComboBox cb = (JComboBox) e.getSource();
	                            elementSelection = (String) cb.getSelectedItem();
	                            // citationelementsjLabel.setText("Citation Elements
	                            // for "+ newSelection);
	                        }
	                    });
	        }
	        return elementjComboBox;
	    }

	    /**
	     * This method initializes addtypejButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddtypejButton() {
	        if (addtypejButton == null) {
	            addtypejButton = new JButton();
	            addtypejButton.setBounds(new java.awt.Rectangle(316, 61, 119, 28));
	            addtypejButton.setText("Add New Type");
	            addtypejButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            // citationjComboBox.addItem(citationSelection);
	                            panel.setVisible(false);
	                            listofcitationstylesjComboBox.setVisible(false);
	                            jContentPane.add(getNewtypejPanel(), null);

	                            newtypejPanel.setVisible(true);

	                        }
	                    });
	        }
	        return addtypejButton;
	    }

	    /**
	     * This method initializes Add Element for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddtypejButton1() {
	        if (addelementjButton == null) {
	            addelementjButton = new JButton();
	            addelementjButton
	                    .setBounds(new java.awt.Rectangle(556, 31, 130, 27));
	            addelementjButton.setText("Add Element");
	            addtypejButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            // System.out.println("actionPerformed()");
	                            elementjComboBox.addItem(elementSelection);
	                        }
	                    });
	        }
	        return addelementjButton;
	    }

	    /**
	     * This method initializes removetypejButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getRemovetypejButton() {
	        if (removetypejButton == null) {
	            removetypejButton = new JButton();
	            removetypejButton
	                    .setBounds(new java.awt.Rectangle(452, 62, 120, 27));
	            removetypejButton.setText("Remove Type");
	            removetypejButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {

	                            int n = JOptionPane.showConfirmDialog(frame,
	                                    "Would you like to delete?",
	                                    "Are you sure", JOptionPane.YES_NO_OPTION);
	                            if (n == 0) {


	                                cs.getCsLayoutDefinitions().remove(cs.getElementByName(citationSelection1));
	                                citationjComboBox
	                                .removeItem(citationSelection1);

	                            }
	                        }
	                    });
	        }
	        return removetypejButton;
	    }

	    /**
	     * This method initializes Remove Element Button for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getRemovetypejButton1() {
	        if (removeelementjButton == null) {
	            removeelementjButton = new JButton();
	            removeelementjButton.setBounds(new java.awt.Rectangle(700, 31, 130,
	                    27));
	            removeelementjButton.setText("Remove Element");
	            removeelementjButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {

	                            int n = JOptionPane.showConfirmDialog(frame,
	                                    "Would you like to delete?",
	                                    "Are you sure", JOptionPane.YES_NO_OPTION);
	                            if (n == 0) {
	                                elementjComboBox.removeItem(elementSelection);
	                            }
	                        }
	                    });
	        }
	        return removeelementjButton;
	    }

	    /**
	     * This method initializes delimiterjTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getDelimiterjTextField() {
	        if (delimiterjTextField == null) {
	            delimiterjTextField = new JTextField();
	            delimiterjTextField.setBounds(new java.awt.Rectangle(81, 152, 65,
	                    28));
	        }
	        return delimiterjTextField;
	    }

	    /**
	     * This method initializes delimiterjTextField1 for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getDelimiterjTextField1() {
	        if (delimiterjTextField1 == null) {
	            delimiterjTextField1 = new JTextField();
	            delimiterjTextField1.setBounds(new java.awt.Rectangle(81, 152, 65,
	                    28));
	        }
	        return delimiterjTextField1;
	    }

	    /**
	     * This method initializes startswithjTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getStartswithjTextField() {
	        if (startswithjTextField == null) {
	            startswithjTextField = new JTextField();
	            startswithjTextField.setBounds(new Rectangle(241, 153, 80, 28));
	        }
	        return startswithjTextField;
	    }

	    /**
	     * This method initializes startswithjTextField1 for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getStartswithjTextField1() {
	        if (startswithjTextField1 == null) {
	            startswithjTextField1 = new JTextField();
	            startswithjTextField1.setBounds(new java.awt.Rectangle(626, 151,
	                    64, 28));
	        }
	        return startswithjTextField1;
	    }

	    /**
	     * This method initializes endswithjTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getEndswithjTextField() {
	        if (endswithjTextField == null) {
	            endswithjTextField = new JTextField();
	            endswithjTextField.setBounds(new Rectangle(406, 152, 60, 26));
	        }
	        return endswithjTextField;
	    }

	    /**
	     * This method initializes endswithjTextField1 for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getEndswithjTextField1() {
	        if (endswithjTextField1 == null) {
	            endswithjTextField1 = new JTextField();
	            endswithjTextField1.setBounds(new java.awt.Rectangle(779, 151, 60,
	                    26));
	        }
	        return endswithjTextField1;
	    }

	    /**
	     * This method initializes maxlengthjTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getMaxlengthjTextField() {
	        if (maxlengthjTextField == null) {
	            maxlengthjTextField = new JTextField();
	            maxlengthjTextField.setBounds(new Rectangle(561, 153, 63, 26));
	        }
	        return maxlengthjTextField;
	    }

	    /**
	     * This method initializes maxlengthjTextField1 for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getMaxlengthjTextField1() {
	        if (maxlengthjTextField1 == null) {
	            maxlengthjTextField1 = new JTextField();
	            maxlengthjTextField1.setBounds(new java.awt.Rectangle(106, 196, 63,
	                    26));
	        }
	        return maxlengthjTextField1;
	    }

	    /**
	     * This method initializes maxlengthendswithjTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getMaxlengthendswithjTextField() {
	        if (maxlengthendswithjTextField == null) {
	            maxlengthendswithjTextField = new JTextField();
	            maxlengthendswithjTextField.setBounds(new Rectangle(762, 152, 122, 26));
	        }
	        return maxlengthendswithjTextField;
	    }

	    /**
	     * This method initializes maxlengthendswithjTextField1 for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getMaxlengthendswithjTextField1() {
	        if (maxlengthendswithjTextField1 == null) {
	            maxlengthendswithjTextField1 = new JTextField();
	            maxlengthendswithjTextField1.setBounds(new java.awt.Rectangle(329,
	                    195, 61, 26));
	        }
	        return maxlengthendswithjTextField1;
	    }

	    /**
	     * This method initializes fontjComboBox for First Panel
	     *
	     * @return javax.swing.JComboBox
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JComboBox getFontjComboBox() throws IOException, SAXException {
	        if (fontjComboBox == null) {
	            fontjComboBox = new JComboBox();

	            String name;

	            FontStylesCollection fsc = FontStylesCollection
	                    .loadFromXml("CitationStyles\\Default\\FontStyles.xml");

	            for (FontStyle fs : fsc.getFontStyles()) {
	                name = fs.getName(); // put this in pull-down list
	                fontjComboBox.addItem(name);

	            }
	            fontjComboBox.setBounds(new Rectangle(61, 197, 120, 28));
	            fontjComboBox
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {

	                            JComboBox cb = (JComboBox) e.getSource();
	                            fontSelection = (String) cb.getSelectedItem();
	                        }
	                    });
	        }
	        return fontjComboBox;
	    }

	    /**
	     * This method initializes fontjComboBox1
	     *
	     * @return javax.swing.JComboBox
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JComboBox getFontjComboBox1() throws IOException, SAXException {
	        if (fontjComboBox1 == null) {
	            fontjComboBox1 = new JComboBox();
	            String name;

	            FontStylesCollection fsc = FontStylesCollection
	                    .loadFromXml("CitationStyles\\Default\\FontStyles.xml");

	            for (FontStyle fs : fsc.getFontStyles()) {
	                name = fs.getName(); // put this in pull-down list
	                fontjComboBox1.addItem(name);

	            }
	            fontjComboBox1.setBounds(new java.awt.Rectangle(460, 196, 120, 28));
	            fontjComboBox1
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {

	                            JComboBox cb = (JComboBox) e.getSource();
	                            fontSelection1 = (String) cb.getSelectedItem();
	                        }
	                    });
	        }
	        return fontjComboBox1;
	    }

	    /**
	     * This method initializes citationjScrollPane for First Panel
	     *
	     * @return javax.swing.JScrollPane
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JScrollPane getCitationjScrollPane() throws IOException,
	            SAXException {
	        if (citationjScrollPane == null) {
	            citationjScrollPane = new JScrollPane();
	            citationjScrollPane.setBounds(new java.awt.Rectangle(15, 300, 841,
	                    196));
	            citationjScrollPane.setViewportView(getCitationjTable());
	        }
	        return citationjScrollPane;
	    }

	    /**
	     * This method initializes citationjScrollPane1 for Second Panel
	     *
	     * @return javax.swing.JScrollPane
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JScrollPane getCitationjScrollPane1() throws IOException,
	            SAXException {
	        if (citationjScrollPane1 == null) {
	            citationjScrollPane1 = new JScrollPane();
	            citationjScrollPane1.setBounds(new java.awt.Rectangle(15, 300, 841,
	                    196));
	            citationjScrollPane1.setViewportView(getCitationjTable1());
	        }
	        return citationjScrollPane1;
	    }

	    /**
	     * This method initializes citationjTable for First Panel
	     *
	     * @return javax.swing.JTable
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JTable getCitationjTable() throws IOException, SAXException {
	        if (citationjTable == null) {

	            citationjTable = new JTable(model);
	            //citationjTable.getTableHeader().setReorderingAllowed(false);
	            //citationjTable.getTableHeader().setResizingAllowed(false);
	            //model.addColumn("Id");
	            model.addColumn("Name");
	            model.addColumn("Reference");
	            model.addColumn("ValidIF");
	            model.addColumn("StartsWith");
	            model.addColumn("EndsWith");
	            model.addColumn("MaxLenght");
	            //model.addColumn("MaxLenght\nEndsWith");
	            model.addColumn("<html>MaxLenght<br>EndsWith");
	            model.addColumn("MaxCount");
	            //model.addColumn("<html>MaxCount<br>EndsWith""MaxCount\nEndsWith");
	            model.addColumn("<html>MaxCount<br>EndsWith");
	            model.addColumn("Delimiter");
	            //model.addColumn("Internal Delimiter");
	            model.addColumn("<html>Internal<br>Delimiter");
	            model.addColumn("Font");

	            String[]names = {"Name","Reference","ValidIf","StartsWith","EndsWith", "MaxLenght", 
	            		"MaxLenghtEndsWith", "MaxCount", "MaxCountEndsWith", "Delimiter", "InternalDelimiter", "Font"};
	            
	            
	            for (int i=0; i<names.length;colNames.add(i, names[i++]) ); 
	                        
	            JTableHeader header = citationjTable.getTableHeader();
	            Dimension dim = header.getPreferredSize();
	            dim.height *=2;
	            header.setPreferredSize(dim);

	            // citationjTable.getModel().addTableModelListener(new
	            // MyTableModelListener(citationjTable));

	                        
	            /*TableColumn column = null;
	            for (int i = 0; i < 12; i++) {
	                column = citationjTable.getColumnModel().getColumn(i);
	                if (i == 0){
	                    column.setMaxWidth(0);
	                }
	                else if (i == 6) {
	                    column.setPreferredWidth(40);
	                } else if (i == 8) {
	                    column.setPreferredWidth(40);
	                } else if (i == 10) {
	                    column.setPreferredWidth(40);
	                } else {
	                    column.setPreferredWidth(40);
	                }
	            }*/
	            
	            

	            TableColumn fontColumn = citationjTable.getColumnModel().getColumn(
	            		colNames.indexOf("Font"));
	            fontjComboBox2 = new JComboBox();
	            fontColumn.setCellEditor(new DefaultCellEditor(fontjComboBox2));

	            String name;

	            FontStylesCollection fsc = FontStylesCollection
	                    .loadFromXml("CitationStyles\\Default\\FontStyles.xml");

	            for (FontStyle fs : fsc.getFontStyles()) {
	                name = fs.getName(); // put this in pull-down list
	                fontjComboBox2.addItem(name);

	            }
	            fontColumn.setCellEditor(new DefaultCellEditor(fontjComboBox2));

	            /*
	             * TableColumn referenceColumn =
	             * citationjTable.getColumnModel().getColumn(1); referncejComboBox2 =
	             * new JComboBox(); referenceColumn.setCellEditor(new
	             * DefaultCellEditor(referncejComboBox2));
	             *
	             * LayoutElementsCollection lec =
	             * LayoutElementsCollection.loadFromXml("LayoutElements.xml");
	             *
	             * for ( LayoutElement le: lec.getLayoutElements() ) { name =
	             * le.getName(); // put this in pull-down list
	             * referncejComboBox2.addItem(name); }
	             */

	            citationjTable
	                    .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	            citationjTable.setBounds(new java.awt.Rectangle(0, 0, 900, 100));
	            citationjTable.addPropertyChangeListener("selectionModel",
	                    new java.beans.PropertyChangeListener() {
	                        public void propertyChange(
	                                java.beans.PropertyChangeEvent e) {

	                        }
	                    });

	            citationjTable.getModel().addTableModelListener(new javax.swing.event.TableModelListener(){

					public void tableChanged(TableModelEvent e) {
						int firstRow = e.getFirstRow();
			            int lastRow = e.getLastRow();
			            int mColIndex = e.getColumn();
			    
			            switch (e.getType()) {
			              case TableModelEvent.INSERT:
			            	  
			                // The inserted rows are in the range [firstRow, lastRow]
			                for (int r=firstRow; r<=lastRow; r++) {
			                    // Row r was inserted
			                	//System.out.println("Inserted");
			                }
			                break;
			              case TableModelEvent.UPDATE:
			                if (firstRow == TableModelEvent.HEADER_ROW) {
			                    if (mColIndex == TableModelEvent.ALL_COLUMNS) {
			                        // A column was added
			                    } else {
			                        // Column mColIndex in header changed
			                    }
			                } else {
			                    // The rows in the range [firstRow, lastRow] changed
//			                	System.out.println("Row Changed");
			                	
			                    for (int r=firstRow; r<=lastRow; r++) {
			                        // Row r was changed
			                    	changed = true;
			                        if (mColIndex == TableModelEvent.ALL_COLUMNS) {
			                            // All columns in the range of rows have changed
			                        } else {
			                            // Column mColIndex changed
			                        }
			                    }
			                }
			                break;
			              case TableModelEvent.DELETE:
			            	  
			                // The rows in the range [firstRow, lastRow] changed
			                for (int r=firstRow; r<=lastRow; r++) {
			                    // Row r was deleted
//			                	System.out.println("Deleted");
			                	changed = true;
			                }
			                break;
			            }
					}
						
					});
	        
	        }
	        return citationjTable;
	    }

	    /**
	     * This method initializes citationjTable1 for Second Panel
	     *
	     * @return javax.swing.JTable
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JTable getCitationjTable1() throws IOException, SAXException {
	        if (citationjTable1 == null) {
	            String[] columnNames = { "Name", "StartsWith", "EndsWith",
	                    "MaxLenght", "MaxCount", "Delimiter", "Internal Delimiter",
	                    "Font" };
	            Object[] data1 = { "authors", "title", "titleof", "vol", "issuenr",
	                    "pages", "date" };
	            Object[] data2 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };
	            Object[] data3 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };
	            Object[] data4 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };
	            Object[] data5 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };
	            Object[] data6 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            Object[] data7 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            Object[] data8 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            Object[] data9 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            Object[] data10 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            Object[] data11 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            Object[] data12 = { "       ", "       ", "       ", "       ",
	                    "       ", "       ", "       ", "       ", "       " };

	            citationjTable1 = new JTable(model1);
	            model1.addColumn("Name", data1);
	            model1.addColumn("Reference", data2);
	            model1.addColumn("ValidIF", data3);
	            model1.addColumn("StartsWith", data4);
	            model1.addColumn("EndsWith", data5);
	            model1.addColumn("MaxLenght", data6);
	            model1.addColumn("MaxLenght\n" + "EndsWith", data7);
	            model1.addColumn("MaxCount", data8);
	            model1.addColumn("MaxCount\n" + "EndsWith", data9);
	            model1.addColumn("Delimiter", data10);
	            model1.addColumn("Internal Delimiter", data11);
	            model1.addColumn("Font", data12);

	            TableColumn column = null;
	            for (int i = 0; i < 12; i++) {
	                column = citationjTable1.getColumnModel().getColumn(i);
	                if (i == 6) {
	                    column.setPreferredWidth(90); // sport column is bigger
	                } else if (i == 8) {
	                    column.setPreferredWidth(85); // sport column is bigger
	                } else if (i == 10) {
	                    column.setPreferredWidth(75); // sport column is bigger
	                } else {
	                    column.setPreferredWidth(40);
	                }
	            }

	            TableColumn fontColumn = citationjTable1.getColumnModel()
	                    .getColumn(11);
	            fontjComboBox2 = new JComboBox();
	            fontColumn.setCellEditor(new DefaultCellEditor(fontjComboBox2));

	            String name;

	            FontStylesCollection fsc = FontStylesCollection
	                    .loadFromXml("CitationStyles\\Default\\FontStyles.xml");

	            for (FontStyle fs : fsc.getFontStyles()) {
	                name = fs.getName(); // put this in pull-down list
	                fontjComboBox2.addItem(name);

	            }
	            fontColumn.setCellEditor(new DefaultCellEditor(fontjComboBox2));

	            TableColumn referenceColumn = citationjTable1.getColumnModel()
	                    .getColumn(1);
	            referncejComboBox2 = new JComboBox();
	            referenceColumn.setCellEditor(new DefaultCellEditor(
	                    referncejComboBox2));

	            LayoutElementsCollection lec = LayoutElementsCollection
	                    .loadFromXml(pathToDefault + "LayoutElements.xml");

	            for (LayoutElement le : lec.getLayoutElements()) {
	                name = le.getName(); // put this in pull-down list
	                referncejComboBox2.addItem(name);

	            }

	            citationjTable1
	                    .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	            citationjTable1.setBounds(new java.awt.Rectangle(0, 0, 1000, 100));
	            citationjTable1.addPropertyChangeListener("selectionModel",
	                    new java.beans.PropertyChangeListener() {
	                        public void propertyChange(
	                                java.beans.PropertyChangeEvent e) {
	                            // System.out.println("propertyChange(selectionModel)");

	                        }
	                    });

	        }
	        return citationjTable1;
	    }

	    /**
	     * This method initializes addbeforejButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddbeforejButton() {
	        if (addbeforejButton == null) {
	            addbeforejButton = new JButton();
	            addbeforejButton
	                    .setBounds(new java.awt.Rectangle(137, 497, 101, 32));
	            addbeforejButton.setText("Add Before");
	            addbeforejButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            if (citationjTable.getSelectedRow() != -1) {
	                            panel.setVisible(false);
	                            listofcitationstylesjComboBox.setVisible(false);
	                            try {
	                                jContentPane.add(
	                                        getAddnewcitationelementjPanel(), null);
	                            } catch (IOException e1) {
	                                e1.printStackTrace();
	                            } catch (SAXException e1) {
	                                e1.printStackTrace();
	                            }
	                            addnewcitationelementjPanel.setVisible(true);
	                            addbefore = true;
	                            }
	                             else {
//	                                  custom title, warning icon
	                                    JOptionPane.showMessageDialog(frame,
	                                        "Please select a row of the table to add before.",
	                                        "Inane warning",
	                                        JOptionPane.WARNING_MESSAGE);
	                                }

	                            /*
	                             * if(citationjTable.getSelectedRow() != -1)
	                             * model.insertRow(citationjTable.getSelectedRow(),new
	                             * Object[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", "
	                             * "});
	                             */
	                        }

	                    });
	        }
	        return addbeforejButton;
	    }

	    /**
	     * This method initializes addbeforejButton1 for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddbeforejButton1() {
	        if (addbeforejButton1 == null) {
	            addbeforejButton1 = new JButton();
	            addbeforejButton1.setBounds(new java.awt.Rectangle(137, 497, 101,
	                    32));
	            addbeforejButton1.setText("Add Before");
	            addbeforejButton1
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            // System.out.println("actionPerformed()");
	                            if (citationjTable1.getSelectedRow() != -1)
	                                model1.insertRow(citationjTable1
	                                        .getSelectedRow(), new Object[] {
	                                        "       ", "       ", "       ",
	                                        "       ", "       ", "       ",
	                                        "       ", "       ", "       ",
	                                        "       ", "       ", "       " });
	                        }
	                    });
	        }
	        return addbeforejButton1;
	    }

	    /**
	     * This method initializes addafterjButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddafterjButton() {
	        if (addafterjButton == null) {
	            addafterjButton = new JButton();
	            addafterjButton.setBounds(new java.awt.Rectangle(264, 498, 98, 32));
	            addafterjButton.setText("Add After");
	            addafterjButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            if (citationjTable.getSelectedRow() != -1) {
	                            panel.setVisible(false);
	                            listofcitationstylesjComboBox.setVisible(false);
	                            try {
	                                jContentPane.add(
	                                        getAddnewcitationelementjPanel(), null);
	                            } catch (IOException e1) {
	                                e1.printStackTrace();
	                            } catch (SAXException e1) {
	                                e1.printStackTrace();
	                            }
	                            addnewcitationelementjPanel.setVisible(true);
	                            addafter = true;
	                            }
	                             else {
//	                                  custom title, warning icon
	                                    JOptionPane.showMessageDialog(frame,
	                                        "Please select a row of the table to move down.",
	                                        "Inane warning",
	                                        JOptionPane.WARNING_MESSAGE);
	                                }
	                            /*
	                             * model.insertRow(citationjTable.getSelectedRow()+1,new
	                             * Object[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", "
	                             * "}); int i = citationjTable.getSelectedRow();
	                             */

	                        }
	                    });
	        }
	        return addafterjButton;
	    }

	    /**
	     * This method initializes addafterjButton1 for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddafterjButton1() {
	        if (addafterjButton1 == null) {
	            addafterjButton1 = new JButton();
	            addafterjButton1
	                    .setBounds(new java.awt.Rectangle(264, 498, 98, 32));
	            addafterjButton1.setText("Add After");
	            addafterjButton1
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            // System.out.println("actionPerformed()");
	                            model1.insertRow(
	                                    citationjTable1.getSelectedRow() + 1,
	                                    new Object[] { "       ", "       ",
	                                            "       ", "       ", "       ",
	                                            "       ", "       ", "       ",
	                                            "       ", "       ", "       ",
	                                            "       " });
	                        }
	                    });
	        }
	        return addafterjButton1;
	    }

	    /**
	     * This method initializes upjButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getUpjButton() {
	        if (upjButton == null) {
	            upjButton = new JButton();
	            upjButton.setBounds(new java.awt.Rectangle(390, 497, 97, 31));
	            upjButton.setText("Up");
	            upjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    if (citationjTable.getSelectedRow() != 0
	                            && citationjTable.getSelectedRow() != -1){
	                        model.moveRow(citationjTable.getSelectedRow(),
	                                citationjTable.getSelectedRow(), citationjTable
	                                        .getSelectedRow() - 1);
	                        LayoutElement le = tblElems.remove(citationjTable.getSelectedRow());
	                        tblElems.add(citationjTable.getSelectedRow() - 1, le);
	                        
	                        citationjTable.changeSelection( citationjTable.getSelectedRow() - 1, 0, false, false );
	                        citationjTable.scrollRectToVisible(
	                                citationjTable.getCellRect( citationjTable
	                                        .getSelectedRow() - 1, 0, true ) );

	                        //model.setC
	                    }
	                    else {
//	                      custom title, warning icon
	                        JOptionPane.showMessageDialog(frame,
	                            "Please select a row of the table to move up.",
	                            "Inane warning",
	                            JOptionPane.WARNING_MESSAGE);
	                    }

	                }
	            });
	        }
	        return upjButton;
	    }

	    /**
	     * This method initializes upjButton1 for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getUpjButton1() {
	        if (upjButton1 == null) {
	            upjButton1 = new JButton();
	            upjButton1.setBounds(new java.awt.Rectangle(390, 497, 97, 31));
	            upjButton1.setText("Up");
	            upjButton1.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    if (citationjTable1.getSelectedRow() != -1)
	                        model1.moveRow(citationjTable1.getSelectedRow(),
	                                citationjTable1.getSelectedRow(),
	                                citationjTable1.getSelectedRow() - 1);
	                }
	            });
	        }
	        return upjButton1;
	    }

	    /**
	     * This method initializes downjButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getDownjButton() {
	        if (downjButton == null) {
	            downjButton = new JButton();
	            downjButton.setBounds(new java.awt.Rectangle(511, 496, 100, 30));
	            downjButton.setText("Down");
	            downjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    if (citationjTable.getSelectedRow() !=-1 && citationjTable.getSelectedRow() != citationjTable.getRowCount()-1) {
	                        model.moveRow(citationjTable.getSelectedRow(),
	                                citationjTable.getSelectedRow(), citationjTable
	                                        .getSelectedRow() + 1);
	                        LayoutElement le = tblElems.remove(citationjTable.getSelectedRow());
	                        
	                        
	                        tblElems.add(citationjTable.getSelectedRow() + 1, le);
	                        
	                        citationjTable.changeSelection( citationjTable.getSelectedRow() + 1, 0, false, false );
	                        citationjTable.scrollRectToVisible(
	                                citationjTable.getCellRect( citationjTable
	                                        .getSelectedRow() + 1, 0, true ) );
	                        //changed = true;

	                    } else {
//	                      custom title, warning icon
	                        JOptionPane.showMessageDialog(frame,
	                            "Please select a row of the table to move down.",
	                            "Inane warning",
	                            JOptionPane.WARNING_MESSAGE);
	                    }
	                }
	            });
	        }
	        return downjButton;
	    }

	    /**
	     * This method initializes downjButton1 for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getDownjButton1() {
	        if (downjButton1 == null) {
	            downjButton1 = new JButton();
	            downjButton1.setBounds(new java.awt.Rectangle(511, 496, 100, 30));
	            downjButton1.setText("Down");
	            downjButton1.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    // System.out.println("actionPerformed()");
	                    // if(citationjTable1.getSelectedRow() >
	                    // citationjTable1.getSelectedRow()+1)
	                    model1.moveRow(citationjTable1.getSelectedRow(),
	                            citationjTable1.getSelectedRow(), citationjTable1
	                                    .getSelectedRow() + 1);
	                }
	            });
	        }
	        return downjButton1;
	    }

	    /**
	     * This method initializes removejButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getRemovejButton() {
	        if (removejButton == null) {
	            removejButton = new JButton();
	            removejButton.setBounds(new java.awt.Rectangle(634, 497, 100, 29));
	            removejButton.setText("Remove");
	            removejButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                        	tblElems.remove(citationjTable.getSelectedRow());
	                        	  /*citationjTable.changeSelection( citationjTable.getSelectedRow()+ 1, 0, false, false );
	                            citationjTable.scrollRectToVisible(
	                                    citationjTable.getCellRect( citationjTable
	                                            .getSelectedRow()+ 1, 0, true ) );*/
	                            if (citationjTable.getSelectedRow() != -1){
	                                model
	                                        .removeRow(citationjTable
	                                                .getSelectedRow());
	                            }
	                            else {
//	                              custom title, warning icon
	                                JOptionPane.showMessageDialog(frame,
	                                    "Please select a row to remove from table.",
	                                    "Inane warning",
	                                    JOptionPane.WARNING_MESSAGE);
	                            }
	                        }
	                    });
	        }
	        return removejButton;
	    }

	    /**
	     * This method initializes removejButton1 for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getRemovejButton1() {
	        if (removejButton1 == null) {
	            removejButton1 = new JButton();
	            removejButton1.setBounds(new java.awt.Rectangle(634, 497, 100, 29));
	            removejButton1.setText("Remove");
	            removejButton1
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            if (citationjTable1.getSelectedRow() != -1)
	                                model1.removeRow(citationjTable1
	                                        .getSelectedRow());
	                        }
	                    });
	        }
	        return removejButton1;
	    }

	    /**
	     * This method initializes valiadatejButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getValiadatejButton() {
	        if (valiadatejButton == null) {
	            valiadatejButton = new JButton();
	            valiadatejButton.setBounds(new java.awt.Rectangle(660,570,90,29));
	            valiadatejButton.setText("Validate");
	            valiadatejButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
//	                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()


	                }
	            });
	        }
	        return valiadatejButton;
	    }
	    /**
	     * This method initializes savejButton for First Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getSavejButton() {
	        if (savejButton == null) {
	            savejButton = new JButton();
	            savejButton.setBounds(new java.awt.Rectangle(766, 570, 76, 27));
	            savejButton.setText("Save");
	            savejButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    
	                    p = le.getParametersAtDefault();

	                    p.setDelimiter(delimiterjTextField.getText());
	                    p.setValidIf(validifjTextField.getText());
	                    p.setStartsWith(startswithjTextField.getText());
	                    p.setEndsWith(endswithjTextField.getText());
	                    String numX = maxlengthjTextField.getText();
	                    int num1 = numX == null
	                            || (numX != null && numX.equals("")) ? 0 : Integer
	                            .parseInt(numX);
	                    p.setMaxLength(num1);
	                    p.setMaxLengthEndsWith(maxlengthendswithjTextField
	                            .getText());
	                    
	                    p.setFontStyleRef((String)fontjComboBox.getSelectedItem());

	                    LayoutElement csld_new = new LayoutElement();

	                    csld_new.setParameters(p);
	                    csld_new.setName(csld.getName());

	                    ArrayList<LayoutElement> elems = new ArrayList<LayoutElement>();

	                 
	                    for (int i = 0; i < citationjTable.getRowCount(); i++) {

	                        String name = (String) model.getValueAt(i, colNames.indexOf("Name"));
	                        LayoutElement le = tblElems.get(i);
	                        
	                        
	                        //if le is repeatable - leave it how it is!!!
	                        if (!le._isRepeatable()) {
	                        	
	                         	Parameters p = new Parameters();

	                        	le.setName(name);
	                        	
	                        	p.setValidIf((String) model.getValueAt(i, colNames.indexOf("ValidIf")));
	                        	p.setStartsWith((String) model.getValueAt(i, colNames.indexOf("StartsWith")));
	                        	p.setEndsWith((String) model.getValueAt(i, colNames.indexOf("EndsWith")));

	                        	String numS = model.getValueAt(i, colNames.indexOf("MaxLenght")) + "";
	                        	int num = numS == null
	                        	|| (numS != null && numS.equals("")) ? 0
	                        			: Integer.parseInt(numS);
	                        	p.setMaxLength(num);

	                        	p.setMaxLengthEndsWith((String) model.getValueAt(i, colNames.indexOf("MaxLenghtEndsWith")));

	                        	numS = model.getValueAt(i, colNames.indexOf("MaxCount")) + "";
	                        	num = numS == null || (numS != null && numS.equals("")) ? 0
	                        			: Integer.parseInt(numS);
	                        	p.setMaxCount(num);

	                        	p.setMaxCountEndsWith((String) model.getValueAt(i, colNames.indexOf("MaxCountEndsWith")));

	                        	p.setDelimiter((String) model.getValueAt(i, colNames.indexOf("Delimiter")));
	                        	p.setInternalDelimiter((String) model.getValueAt(i, colNames.indexOf("InternalDelimiter")));

	                        	p.setFontStyleRef((String) model.getValueAt(i, colNames.indexOf("Font")));

	                        	le.setParameters(p);
	                        	
	                        	if (le.hasElementsAtDefault()) {
									le.setElements(le.getElementsAtDefault());
								}


	                        	try {
	                        		le.addPositionBundle();
	                        	} catch (Exception e1) {
	                        		// TODO Auto-generated catch block
	                        		e1.printStackTrace();
	                        	}
	                        }


	                        elems.add(le);

	                    }

	                    csld_new.setElements(elems);

	                    try {
	                        csld_new.addPositionBundle();
	                    } catch (Exception e2) {
	                        // TODO Auto-generated catch block
	                        e2.printStackTrace();
	                    }

	                    cs.replaceCsldByNameWith(csld.getName(), csld_new);


	                    try {
	                        csc.writeToXml("CitationStyles\\"+listofcitationstylesjComboBox.getSelectedItem()+"\\" + "CitationStyle.xml");
	                    } catch (IOException e1) {
	                        e1.printStackTrace();
	                    } catch (SAXException e1) {
	                        e1.printStackTrace();
	                    } catch (CitationStyleManagerException e1) {
	                        e1.printStackTrace();
	                    } 
	                    
	                    changed = false;
	                    
	                    /*
	                     * } }
	                     */
	                }
	            });
	        }
	        return savejButton;
	    }

	    /**
	     * This method initializes savejButton1 for Second Panel
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getSavejButton1() {
	        if (savejButton1 == null) {
	            savejButton1 = new JButton();
	            savejButton1.setBounds(new java.awt.Rectangle(766, 559, 76, 27));
	            savejButton1.setText("Save");
	            savejButton1.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {

//	                    System.out.println("Name" + namejTextField1.getText());
//	                    // System.out.println("Citation"+newSelection);
//	                    System.out.println("Delimiter"
//	                            + delimiterjTextField1.getText());
//	                    System.out.println("startswith"
//	                            + startswithjTextField1.getText());
//	                    System.out.println("endswith"
//	                            + endswithjTextField1.getText());
//	                    System.out.println("maxlength"
//	                            + maxlengthjTextField1.getText());
//	                    System.out.println("maxlengthendswith"
//	                            + maxlengthendswithjTextField1.getText());
//	                    System.out.println("" + fontSelection);

	                    Vector v = model1.getDataVector();
//	                    for (int i = 0; !v.isEmpty() && i < v.size(); i++) {
	//
//	                        System.out.println(v.get(i));
	//
//	                    }
	                }
	            });
	        }
	        return savejButton1;
	    }

	    /**
	     * This method initializes layoutelementsjButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getLayoutelementsjButton() {
	        if (layoutelementsjButton == null) {
	            layoutelementsjButton = new JButton();
	            layoutelementsjButton.setBounds(new java.awt.Rectangle(598, 557,
	                    137, 30));
	            layoutelementsjButton.setText("Layout Elements");
	            layoutelementsjButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            panel.setVisible(false);
	                            try {
	                                jContentPane.add(getPanel1(), null);
	                            } catch (IOException e1) {
	                                e1.printStackTrace();
	                            } catch (SAXException e1) {
	                                e1.printStackTrace();
	                            }
	                            panel1.setVisible(true);

	                        }
	                    });
	        }
	        return layoutelementsjButton;
	    }

	    /**
	     * This method initializes validifjTextField for First Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getValidifjTextField() {
	        if (validifjTextField == null) {
	            validifjTextField = new JTextField();
	            validifjTextField.setBounds(new Rectangle(255, 198, 556, 28));
	        }
	        return validifjTextField;
	    }

	    /**
	     * This method initializes validifjTextField1 for Second Panel
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getValidifjTextField1() {
	        if (validifjTextField1 == null) {
	            validifjTextField1 = new JTextField();
	            validifjTextField1.setBounds(new java.awt.Rectangle(451, 151, 74,
	                    28));
	        }
	        return validifjTextField1;
	    }

	    /**
		 * This method initializes addnewtypecsastemplatejComboBox	
		 * 	
		 * @return javax.swing.JComboBox	
	     * @throws IOException 
	     * @throws IllegalArgumentException 
		 */
		private JComboBox getAddnewtypecsastemplatejComboBox() {
			if (addnewtypecsastemplatejComboBox == null) {
				addnewtypecsastemplatejComboBox = new JComboBox();
				addnewtypecsastemplatejComboBox.setBounds(new Rectangle(135, 87, 166, 30));
	             // take only files!!!
	            String[] files = null;
				try {
					files = ProcessCitationStyles.getCitationStylesList();
				} catch (IllegalArgumentException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} 
	            for (int i = 0; i < files.length; i++) {
	            	addnewtypecsastemplatejComboBox.addItem(files[i]);
	            }
				addnewtypecsastemplatejComboBox
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent e) {
//								System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
								if(newtypejComboBox != null)
								{
									newtypejComboBox.removeAllItems();
								}
									CitationStylesCollection csc = null; 
								
						            try {
//						            	System.out.println("CitationStyles\\"+addnewtypecsastemplatejComboBox.getSelectedItem()+"\\" + "CitationStyle.xml");
										csc = CitationStylesCollection
										.loadFromXml("CitationStyles\\"+addnewtypecsastemplatejComboBox.getSelectedItem()+"\\" + "CitationStyle.xml");
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (SAXException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
						            String name;
						            ctc = csc.getCitationStyleByName((String)addnewtypecsastemplatejComboBox.getSelectedItem());

						            if (ctc != null) {

						                for (LayoutElement csld : ctc
						                        .getCsLayoutDefinitions()) {
						                    name = csld.getName(); 
						                newtypejComboBox.addItem(name);
						            }
						        }
								
							}
						});
			}
			return addnewtypecsastemplatejComboBox;
		}

	    /**
		 * This method initializes addlayoutelementsjList	
		 * 	
		 * @return javax.swing.JList	
		 */
		private JList getDefaultListModel() {
			if (addlayoutelementsjList == null) {
				
			}
			return addlayoutelementsjList;
		}

	    /**
		 * This method initializes addlayoutelementsjList	
		 * 	
		 * @return javax.swing.JList	
		 */
		private JList getAddlayoutelementsjList() {
			if (addlayoutelementsjList == null) {
				listModel = new DefaultListModel();
				addlayoutelementsjList = new JList(listModel);
				addlayoutelementsjList.setMinimumSize(new Dimension(100,100));
//				addlayoutelementsjList.setCellRenderer(new addLayotElementsListRenderer());
				addlayoutelementsjList.setCellRenderer(new DefaultListCellRenderer(){
					
					public Component getListCellRendererComponent(
							JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
						
						LayoutElement le = lec.getElementByName((String)value);

						
						if (isSelected) {
							setBackground(list.getSelectionBackground());
							if (le._isRepeatable() )  
								setForeground(Color.RED);
							else if (le.hasElementsAtDefault()) 
								setForeground(Color.GREEN);
							else
								setForeground(list.getSelectionForeground());
						} else {
							setBackground(list.getBackground());
							if (le._isRepeatable() )  
								setForeground(Color.RED);
							else if (le.hasElementsAtDefault()) 
								setForeground(Color.GREEN);
							else 
								setForeground(list.getForeground());
						}
							
						setText((String)value);



						
						return this;
					}
				}		
				);
				
			}
			addlayoutelementsjList.clearSelection();
			return addlayoutelementsjList;
		}


	    /**
		 * This method initializes addlayoutelementsjScrollPane	
		 * 	
		 * @return javax.swing.JScrollPane	
		 */
		private JScrollPane getAddLayoutElementsjScrollPane() {
			if (addLayoutElementsjScrollPane == null) {
				addLayoutElementsjScrollPane = new JScrollPane(getAddlayoutelementsjList());
				addLayoutElementsjScrollPane.setHorizontalScrollBar(null);
			}
			return addLayoutElementsjScrollPane;
		}
		
		
	    /**
		 * This method initializes addNewEmptyTypejButton	
		 * 	
		 * @return javax.swing.JButton	
		 */
		private JButton getAddNewEmptyTypejButton() { 
			if (addNewEmptyTypejButton == null) {
				addNewEmptyTypejButton = new JButton();
				addNewEmptyTypejButton.setText("Create Empty Type");
				addNewEmptyTypejButton.setLocation(new Point(166, 164));
				addNewEmptyTypejButton.setActionCommand("Add Empty Type");
				addNewEmptyTypejButton.setSize(new Dimension(147, 25));
				addNewEmptyTypejButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						System.out.println("addNewEmptyTypejButton is clicked"); // TODO Auto-generated Event stub actionPerformed()
//	HOW TO ADD A NEW PANEL:						
//						jContentPane.add(getMyNewjPanel(), null);
//						MyNewjPanel.setVisible(true);
//						newtypejPanel.setVisible(false);
						
						
					}
				});
			}
			return addNewEmptyTypejButton;
		}

		/**
		 * This method initializes listModel1	
		 * 	
		 * @return javax.swing.DefaultListModel	
		 */
		private DefaultListModel getListModel1() {
			if (listModel1 == null) {
				listModel1 = new DefaultListModel();
			}
			return listModel1;
		}

		public static void main(String[] args) throws IOException, SAXException {
	        new ReportingGUI();
	    }

	    /**
	     * This is the default constructor
	     *
	     * @throws SAXException
	     * @throws IOException
	     */
	    public ReportingGUI() throws IOException, SAXException {
	        super();
	        initialize();
	        panel.setVisible(false);
	        // panel1.setVisible(false);
	    }

	    /**
	     * This method initializes this
	     *
	     * @return void
	     * @throws SAXException
	     * @throws IOException
	     */
	    private void initialize() throws IOException, SAXException {
	        this.setSize(900, 650);
	        model = new DefaultTableModel() {
	            public boolean isCellEditable(int row, int col) {
//	            	change only nonRepeatable elements! 	        	
	            	return !((LayoutElement)tblElems.get(row))._isRepeatable();
	            }
	        };
	        model1 = new DefaultTableModel();
	        // model2 = new DefaultTableModel();
	        this.setVisible(true);
	        this.setContentPane(getJContentPane());
	        this.setTitle("eSciDoc Citation Style Manager");
	        this.addWindowListener(new java.awt.event.WindowAdapter() {
	            public void windowClosing(java.awt.event.WindowEvent e) {
//					commented to avoid the jboss crash	            	
	            }
	        });
	    }

	    /**
	     * This method initializes jContentPane
	     *
	     * @return javax.swing.JPanel
	     * @throws SAXException
	     * @throws IOException
	     */
	    private javax.swing.JPanel getJContentPane() throws IOException,
	            SAXException {
	        if (jContentPane == null) {
	        	
	        	lec = LayoutElementsCollection.loadFromXml(pathToDefault + "LayoutElements.xml");
	        		
	            citationstyleselectionjLabel = new JLabel();
	            citationstyleselectionjLabel.setBounds(new java.awt.Rectangle(350,15,212,31));
	            citationstyleselectionjLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
	            citationstyleselectionjLabel.setText(" Select Citation Style");
	            selectdatasourcejLabel = new JLabel();
	            selectdatasourcejLabel.setBounds(new java.awt.Rectangle(350,270,181,30));
	            selectdatasourcejLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
	            selectdatasourcejLabel.setText(" Select Data Source");
	            selectxmlfilejLabel = new JLabel();
	            selectxmlfilejLabel.setBounds(new java.awt.Rectangle(279,331,30,28));
	            selectxmlfilejLabel.setText("  File");
	            viewoutputjLabel = new JLabel();
	            viewoutputjLabel.setBounds(new java.awt.Rectangle(375,435,118,33));
	            viewoutputjLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
	            viewoutputjLabel.setText(" View Output");
	            buttonGroup = new ButtonGroup();
	            defaultjLabel = new JLabel();
	            // citationjComboBox1 = new JComboBox();
	            defaultjLabel.setBounds(new java.awt.Rectangle(400, 65, 144, 29));
	            // defaultjLabel.setText(" Default");
	            edocjLabel = new JLabel();
	            edocjLabel.setBounds(new java.awt.Rectangle(400, 112, 145, 31));
	            // edocjLabel.setText(" eDoc");
	            escidocjLabel = new JLabel();
	            escidocjLabel.setBounds(new java.awt.Rectangle(400, 157, 145, 31));
	            // escidocjLabel.setText(" eSciDoc");
	            jContentPane = new javax.swing.JPanel();
	            jContentPane.add(citationstyleselectionjLabel, null);
	            jContentPane.add(selectdatasourcejLabel, null);
	            jContentPane.add(selectxmlfilejLabel, null);
	            jContentPane.add(getXmlfilejTextField(), null);
	            jContentPane.add(getSelectjButton(), null);
	            jContentPane.add(getValidatefilejButton(), null);
	            //jContentPane.add(getProcessesfilejButton(), null);
	            jContentPane.add(viewoutputjLabel, null);
	            jContentPane.add(getViewoutputjComboBox(), null);
	            jContentPane.add(getRunoutputjButton(), null);
	            jContentPane.setLayout(null);
	            jContentPane.add(getPanel(), null);
	            jContentPane.add(getButton(), null);
	            jContentPane.add(getNewjButton(), null);
	            jContentPane.add(getListofcitationstylesjComboBox(), null);
	            //jContentPane.add(getCitationStyle_Default(), null);
	            //jContentPane.add(getCitationStyle_eDoc(), null);
	            //jContentPane.add(getCitationStyle_eSciDoc(), null);
	            //jContentPane.add(defaultjLabel, null);
	            //jContentPane.add(edocjLabel, null);
	            //jContentPane.add(escidocjLabel, null);
	            csc = CitationStylesCollection
	                    .loadFromXml("CitationStyles\\"+listofcitationstylesjComboBox.getSelectedItem()+"\\"+ "CitationStyle.xml");
	            /*csc = CitationStylesCollection
	            .loadFromXml("New.xml");*/
	            String name;
	            int i = 1;
	            int b = 20;
	            for (CitationStyle cs : csc.getCitationStyles()) {
	                name = cs.getName(); // put this in pull-down list
	                if (i == 1)
	                    defaultjLabel.setText(name);
	                if (i == 2)
	                    edocjLabel.setText(name);
	                if (i == 3)
	                    escidocjLabel.setText(name);
	                // citationjComboBox1.addItem(name);
	                i++;
	                /*
	                 * jrb = new JRadioButton(); buttonGroup.add(jrb);
	                 * jrb.setBounds(new java.awt.Rectangle(440,126+b,21,21));
	                 * jContentPane.add(jrb); jl = new JLabel(name);
	                 * jl.setBounds(new java.awt.Rectangle(495,122+b,145,31));
	                 * jContentPane.add(jl); b+=48;
	                 */

	            }
	            // jContentPane.add(getCitationjComboBox2(), null);
	        }
	        return jContentPane;
	    }

    
    
	    /**
	     * This method initializes listofcitationstylesjComboBox
	     *
	     * @return javax.swing.JComboBox
	     * @throws IOException 
	     * @throws IllegalArgumentException 
	     */
	    private JComboBox getListofcitationstylesjComboBox() throws IllegalArgumentException, IOException {
	        if (listofcitationstylesjComboBox == null) {
	            listofcitationstylesjComboBox = new JComboBox();
	            listofcitationstylesjComboBox.setBounds(new Rectangle(377, 61, 140, 31));
	        //  String name;
	            
	            String[] styleNames = ProcessCitationStyles.getCitationStylesList(); 
	            for (int i = 0; i < styleNames.length; i++) {
	                listofcitationstylesjComboBox.addItem(styleNames[i]);
	            }
	            // setting of value by default (only for testing!!!)---
	            listofcitationstylesjComboBox.setSelectedItem("APA");
	            // ----------------------------------------------------
	        }
	        return listofcitationstylesjComboBox;
	    }

	    /**
	     * This method initializes xmlfilejTextField
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getXmlfilejTextField() {
	        if (xmlfilejTextField == null) {
	            xmlfilejTextField = new JTextField();
	            xmlfilejTextField.setBounds(new java.awt.Rectangle(319,330,234,28));
	            // only for testing !!!! -------------------------
	            //xmlfilejTextField.setText("1_DataSource_sub.xml");
	            // -----------------------------------------------
	        }
	        return xmlfilejTextField;
	    }

	    /**
	     * This method initializes selectjButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getSelectjButton() {
	        if (selectjButton == null) {
	            selectjButton = new JButton();
	            selectjButton.setBounds(new java.awt.Rectangle(559,332,76,25));
	            selectjButton.setText("Select");
	            selectjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {

	                    JFileChooser m_fileChooser = new JFileChooser();
	                    //... Open a file dialog.
	                    int retval = m_fileChooser.showOpenDialog(ReportingGUI.this);
	                    if (retval == JFileChooser.APPROVE_OPTION) {
	                        //... The user selected a file, process it.
	                        File file = m_fileChooser.getSelectedFile();
	                        //... Update user interface.
	                        try {
								xmlfilejTextField.setText(file.getCanonicalPath());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

	                    }
	                }
	            });
	        }
	        return selectjButton;
	    }

	    /**
	     * This method initializes validatefilejButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getValidatefilejButton() {
	        if (validatefilejButton == null) {
	            validatefilejButton = new JButton();
	            validatefilejButton.setBounds(new java.awt.Rectangle(392,377,90,29));
	            validatefilejButton.setText(" Validate");
	            validatefilejButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {

	                    File xmlfile = new File(xmlfilejTextField.getText());

	                    File xsdfile = new File(CITATION_ELEMENTS_XML_SCHEMA_FILE);
	                    
	                    if (!xsdfile.exists()) 
	                    	JOptionPane.showMessageDialog(frame,
	                    			"File is not exists: \n" + CITATION_ELEMENTS_XML_SCHEMA_FILE);
	                    else 
	                    	try {


	                    		SAXBuilder builder = new SAXBuilder(
	                    				"org.apache.xerces.parsers.SAXParser", true );

	                    		builder.setFeature(
	                    				"http://apache.org/xml/features/validation/schema", true);

	                    		builder.setProperty(
	                    				"http://apache.org/xml/properties/schema"
	                    				+ "/external-noNamespaceSchemaLocation",
	                    				xsdfile.toURL().toString() );
	                    		builder.build( xmlfile );
//	                    		default title and icon
	                    		JOptionPane.showMessageDialog(frame,
	                    		"XML file is Valid");

	                    	}catch (Exception e1){
//	                    		default title and icon
	                    		JOptionPane.showMessageDialog(frame,
	                    				"XML file is not Valid: \n" + e1.toString());

	                    	}
	                }
	            }
	            );
	        }
	        return validatefilejButton;
	    }

	    /**
	     * This method initializes processesfilejButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getProcessesfilejButton() {
	        if (processesfilejButton == null) {
	            processesfilejButton = new JButton();
	            processesfilejButton.setBounds(new java.awt.Rectangle(453,376,87,31));
	            processesfilejButton.setText("Process");
	            processesfilejButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
//	                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
	                }
	            });
	        }
	        return processesfilejButton;
	    }

	    /**
	     * This method initializes viewoutputjComboBox
	     *
	     * @return javax.swing.JComboBox
	     */
	    private JComboBox getViewoutputjComboBox() {
	        if (viewoutputjComboBox == null) {
	            String[] dataSource = { "View", "PDF", "RTF" };
	            viewoutputjComboBox = new JComboBox(dataSource);
	            //viewoutputjComboBox.setSelectedIndex(1);
	            viewoutputjComboBox.setBounds(new java.awt.Rectangle(367,496,59,30));
	            viewoutputjComboBox.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    JComboBox cb = (JComboBox) e.getSource();
	                    outPutFormat = (String) cb.getSelectedItem();
	                }
	            });
	        }
	        return viewoutputjComboBox;
	    }

	    /**
	     * This method initializes runoutputjButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getRunoutputjButton() {
	        if (runoutputjButton == null) {
	            runoutputjButton = new JButton();
	            runoutputjButton.setBounds(new java.awt.Rectangle(444,496,60,28));
	            runoutputjButton.setText("Run");
	            runoutputjButton.setName("");
	            runoutputjButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
//	                    System.out.println("actionPerformed()"); // TODO

	                    String csName = (String) listofcitationstylesjComboBox.getSelectedItem();
	                    String dsFile = xmlfilejTextField.getText();
	                    
//	                    System.out.println("csName "+csName );
//	                    System.out.println("xmlfile "+xmlfilejTextField.getText() );
//	                    System.out.println("outPutFormat "+outPutFormat );

	                    long start = System.currentTimeMillis();

	                    try {

//	                    	JRProperties.setProperty(JRProperties.COMPILER_KEEP_JAVA_FILE, String.valueOf(true)); 

	                    	ProcessCitationStyles pcs = new ProcessCitationStyles();
	                    	
	                    	pcs.KEEP_OLD_SCRIPTLETS = true;
	                    	System.err.println("Here!5");

	                    	pcs.compileReport(csName);
	                    	pcs.fillReport(csName, outPutFormat, dsFile);
	                    	pcs.viewReport(csName, outPutFormat); 
	                    	
	                        System.err.println("Processing time : " + (System.currentTimeMillis() - start));
	                            
	                    }
	                    catch (Exception ee)
	                    {
	                        JOptionPane.showMessageDialog(frame, ee
//	                                ee.printStackTrace(); 
	                                );

	                    }

	                }
	            });
	        }
	        return runoutputjButton;
	    }


	    /**
	     * This method initializes edocjRadioButton
	     *
	     * @return javax.swing.JRadioButton
	     */
	    private JRadioButton getCitationStyle_Default() {
	        if (CitationStyle_Default == null) {
	            CitationStyle_Default = new JRadioButton();
	            CitationStyle_Default.setBounds(new java.awt.Rectangle(370, 68,
	                    21, 21));
	            buttonGroup.add(CitationStyle_Default);

	        }
	        return CitationStyle_Default;
	    }

	    /**
	     * This method initializes escidocjRadioButton
	     *
	     * @return javax.swing.JRadioButton
	     */
	    private JRadioButton getCitationStyle_eDoc() {
	        if (CitationStyle_eDoc == null) {
	            CitationStyle_eDoc = new JRadioButton();
	            CitationStyle_eDoc.setBounds(new java.awt.Rectangle(370, 116, 21,
	                    21));
	            buttonGroup.add(CitationStyle_eDoc);

	        }
	        return CitationStyle_eDoc;
	    }

	    /**
	     * This method initializes escidocjRadioButton
	     *
	     * @return javax.swing.JRadioButton
	     */
	    private JRadioButton getCitationStyle_eSciDoc() {
	        if (CitationStyle_eSciDoc == null) {
	            CitationStyle_eSciDoc = new JRadioButton();
	            CitationStyle_eSciDoc.setBounds(new java.awt.Rectangle(370, 164,
	                    21, 21));
	            buttonGroup.add(CitationStyle_eSciDoc);

	        }
	        return CitationStyle_eSciDoc;
	    }

	    /**
	     * This method initializes addnewcitationelementjPanel
	     *
	     * @return javax.swing.JPanel
	     * @throws SAXException
	     * @throws IOException
	     */
	    private JPanel getAddnewcitationelementjPanel() throws IOException,
	            SAXException {
	        if (addnewcitationelementjPanel == null) {

	            GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
	            gridBagConstraints52.gridx = 0;
	            gridBagConstraints52.gridwidth = 3;
	            gridBagConstraints52.anchor = GridBagConstraints.WEST;
	            gridBagConstraints52.gridy = 12;
	            infoMessage2jLabel = new JLabel();
	            infoMessage2jLabel.setText("This element has 3rd level of nesting");
	            infoMessage2jLabel.setForeground(Color.green);
	            infoMessage2jLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
	            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
	            gridBagConstraints22.gridx = 0;
	            gridBagConstraints22.gridwidth = 3;
	            gridBagConstraints22.anchor = GridBagConstraints.WEST;
	            gridBagConstraints22.insets = new Insets(0, 0, 0, 0);
	            gridBagConstraints22.gridy = 11;
	            infoMessage1jLabel = new JLabel();
	            infoMessage1jLabel.setText("Repeatable elements are not editable for the moment!");
	            infoMessage1jLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
	            infoMessage1jLabel.setPreferredSize(new Dimension(260, 30));
	            infoMessage1jLabel.setHorizontalAlignment(SwingConstants.LEADING);
	            infoMessage1jLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
	            infoMessage1jLabel.setForeground(Color.red);
	            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
	            gridBagConstraints12.fill = GridBagConstraints.BOTH;
	            gridBagConstraints12.gridy = 1;
	            gridBagConstraints12.weightx = 1.0;
	            gridBagConstraints12.weighty = 1.0;
	            gridBagConstraints12.gridwidth = 3;
	            gridBagConstraints12.gridx = 0;
	            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
	            gridBagConstraints10.gridx = 1;
	            gridBagConstraints10.gridy = 2;
	            
//	            jCheckBoxLabel[1] = new JLabel();
//	            yearjLabel = new JLabel();
//	            yearjLabel.setText("year");
	            
	            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
	            gridBagConstraints9.gridx = 0;
	            gridBagConstraints9.gridy = 7;
	            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
	            gridBagConstraints8.gridx = 1;
	            gridBagConstraints8.gridy = 3;
	            
//	            jCheckBoxLabel[2] = new JLabel();
//	            pagejLabel = new JLabel();
//	            pagejLabel.setText("page");
	            
	            GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
	            gridBagConstraints71.gridx = 0;
	            gridBagConstraints71.gridy = 6;
	            GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
	            gridBagConstraints61.gridx = 1;
	            gridBagConstraints61.gridy = 4;
	            
//	            jCheckBoxLabel[3] = new JLabel();
//	            publisherjLabel = new JLabel();
//	            publisherjLabel.setText("publisher");
	            
	            GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
	            gridBagConstraints51.gridx = 0;
	            gridBagConstraints51.gridy = 5;
	            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
	            gridBagConstraints41.gridx = 1;
	            gridBagConstraints41.gridwidth = 2;
	            gridBagConstraints41.anchor = GridBagConstraints.EAST;
	            gridBagConstraints41.gridy = 9;
	            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
	            gridBagConstraints31.gridx = 0;
	            gridBagConstraints31.anchor = GridBagConstraints.WEST;
	            gridBagConstraints31.gridy = 9;
	            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
	            gridBagConstraints21.gridx = 1;
	            gridBagConstraints21.gridy = 6;
	            
//	            jCheckBoxLabel[4] = new JLabel();
//	            placeofeventjLabel = new JLabel();
//	            placeofeventjLabel.setText("placeofevent");
	            
	            
	            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
	            gridBagConstraints11.gridx = 1;
	            gridBagConstraints11.gridy = 5;
	            
//	            jCheckBoxLabel[5] = new JLabel();
//	            dateofeventjLabel = new JLabel();
//	            dateofeventjLabel.setText("dateofevent");
	            
	            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
	            gridBagConstraints7.gridx = 1;
	            gridBagConstraints7.gridy = 7;
	            
//	            jCheckBoxLabel[6] = new JLabel();
//	            nameofeventjLabel = new JLabel();
//	            nameofeventjLabel.setText("nameofevent");
	            
	            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
	            gridBagConstraints6.gridx = 1;
	            gridBagConstraints6.gridy = 1;
	            
//	            jCheckBoxLabel[7] = new JLabel();
//	            typecitationjLabel = new JLabel();
//	            // typecitationjLabel.setText("type");
	            
	            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
	            gridBagConstraints5.gridx = 0;
	            gridBagConstraints5.gridy = 4;
	            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
	            gridBagConstraints4.gridx = 0;
	            gridBagConstraints4.gridy = 3;
	            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
	            gridBagConstraints3.gridx = 0;
	            gridBagConstraints3.gridy = 2;
	            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
	            gridBagConstraints2.gridx = 0;
	            gridBagConstraints2.gridy = 1;
	            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
	            gridBagConstraints1.gridx = 1;
	            gridBagConstraints1.gridwidth = 2;
	            gridBagConstraints1.anchor = GridBagConstraints.EAST;
	            gridBagConstraints1.gridy = 0;
	            GridBagConstraints gridBagConstraints = new GridBagConstraints();
	            gridBagConstraints.gridx = 0;
	            gridBagConstraints.gridy = 0;
	            

	            
	            addnewcitationelementjPanel = new JPanel();
	            addnewcitationelementjPanel.setLayout(new GridBagLayout());
	            addnewcitationelementjPanel.setBounds(new Rectangle(300, 3, 265, 300));
	            addnewcitationelementjPanel.add(getSelectalljButton(), gridBagConstraints);
	            addnewcitationelementjPanel.add(getDeselectalljButton(), gridBagConstraints1);
	            addnewcitationelementjPanel.add(getCanceselectionljButton(), gridBagConstraints31);
	            addnewcitationelementjPanel.add(getAddtocitationstylejButton(), gridBagConstraints41);
	            
//	            addnewcitationelementjPanel.add(getFirstjCheckBox(),
//	                    gridBagConstraints2);
//	            addnewcitationelementjPanel.add(getSecondjCheckBox(),
//	                    gridBagConstraints3);
//	            addnewcitationelementjPanel.add(getThirdjCheckBox(),
//	                    gridBagConstraints4);
//	            addnewcitationelementjPanel.add(getFourthjCheckBox(),
//	                    gridBagConstraints5);
////	            addnewcitationelementjPanel.add(jCheckBoxLabel[1],
////	                    gridBagConstraints6);
////	            addnewcitationelementjPanel.add(jCheckBoxLabel[2],
////	                    gridBagConstraints7);
////	            addnewcitationelementjPanel.add(jCheckBoxLabel[3],
////	                    gridBagConstraints11);
////	            addnewcitationelementjPanel.add(jCheckBoxLabel[4],
////	                    gridBagConstraints21);
	            addnewcitationelementjPanel.add(getAddLayoutElementsjScrollPane(), gridBagConstraints12);
//	            addnewcitationelementjPanel.add(getAddlayoutelementsjList(), gridBagConstraints12);
	            addnewcitationelementjPanel.add(infoMessage1jLabel, gridBagConstraints22);
	            addnewcitationelementjPanel.add(infoMessage2jLabel, gridBagConstraints52);
	            
	            
//	            addnewcitationelementjPanel.add(getFifthjCheckBox(),
//	                    gridBagConstraints51);
////	            addnewcitationelementjPanel.add(jCheckBoxLabel[5],
////	                    gridBagConstraints61);
//	            addnewcitationelementjPanel.add(getSixthjCheckBox(),
//	                    gridBagConstraints71);
////	            addnewcitationelementjPanel.add(jCheckBoxLabel[6], gridBagConstraints8);
//	            addnewcitationelementjPanel.add(getSeventhjCheckBox(),
//	                    gridBagConstraints9);
//	            addnewcitationelementjPanel.add(jCheckBoxLabel[7], gridBagConstraints10);

//	            String name;

	            // System.out.println("lec=" + lec);

//	            int i = 1;
//	            for (LayoutElement le : lec.getLayoutElements()) {
//	            	if (i>=jCheckBox.length) 
//	            		break;
//	                name = le.getName(); // put this in pull-down list
//	                // referncejComboBox.addItem(name);
	//
//	            	jCheckBox[i].setText(name);
	//
//	                i++;
//	                
	//
//	            }
	            for (LayoutElement le : lec.getLayoutElements()) {
	                listModel.addElement(le.getName());
	                }

	        }
	        addlayoutelementsjList.clearSelection();

	        return addnewcitationelementjPanel;
	    }

	    /**
	     * This method initializes selectalljButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getSelectalljButton() {
	        if (selectalljButton == null) {
	            selectalljButton = new JButton();
	            selectalljButton.setText("Select All");
	            selectalljButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            /*jCheckBox[1].setSelected(true);
	                            jCheckBox[2].setSelected(true);
	                            jCheckBox[3].setSelected(true);
	                            jCheckBox[4].setSelected(true);
	                            jCheckBox[5].setSelected(true);
	                            jCheckBox[6].setSelected(true);
	                            jCheckBox[7].setSelected(true);*/
	                        	addlayoutelementsjList.setSelectionInterval(0,addlayoutelementsjList.getModel().getSize()-1);
//	                        	System.out.println(addlayoutelementsjList.getModel().getSize());
	                        }
	                    });
	        }
	        return selectalljButton;
	    }

	    /**
	     * This method initializes deselectalljButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getDeselectalljButton() {
	        if (deselectalljButton == null) {
	            deselectalljButton = new JButton();
	            deselectalljButton.setText("Deselect All");
	            deselectalljButton.setPreferredSize(new Dimension(147, 26));
	            deselectalljButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                          /*  jCheckBox[1].setSelected(false);
	                            jCheckBox[2].setSelected(false);
	                            jCheckBox[3].setSelected(false);
	                            jCheckBox[4].setSelected(false);
	                            jCheckBox[5].setSelected(false);
	                            jCheckBox[6].setSelected(false);
	                            jCheckBox[7].setSelected(false);*/
	                        	addlayoutelementsjList.clearSelection();
	                        }
	                    });
	        }
	        return deselectalljButton;
	    }

	    /**
	     * This method initializes firstjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getFirstjCheckBox() {
	        if (jCheckBox[1] == null) {
	        	jCheckBox[1] = new JCheckBox();
	        }
	        return jCheckBox[1];
	    }

	    /**
	     * This method initializes secondjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getSecondjCheckBox() {
	        if (jCheckBox[2] == null) {
	        	jCheckBox[2] = new JCheckBox();
	        }
	        return jCheckBox[2];
	    }

	    /**
	     * This method initializes thirdjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getThirdjCheckBox() {
	        if (jCheckBox[3] == null) {
	        	jCheckBox[3] = new JCheckBox();
	        }
	        return jCheckBox[3];
	    }

	    /**
	     * This method initializes fourthjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getFourthjCheckBox() {
	        if (jCheckBox[4] == null) {
	        	jCheckBox[4] = new JCheckBox();
	        }
	        return jCheckBox[4];
	    }

	    /**
	     * This method initializes canceljButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getCanceselectionljButton() {
	        if (canceselectionljButton == null) {
	            canceselectionljButton = new JButton();
	            canceselectionljButton.setText("Cancel");
	            canceselectionljButton.setHorizontalTextPosition(SwingConstants.CENTER);
	            canceselectionljButton.setMnemonic(KeyEvent.VK_UNDEFINED);
	            canceselectionljButton.setPreferredSize(new Dimension(87, 26));
	            canceselectionljButton.setHorizontalAlignment(SwingConstants.CENTER);
	            canceselectionljButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            panel.setVisible(true);
	                            addnewcitationelementjPanel.setVisible(false);
	                            /*jCheckBox[1].setSelected(false);
	                            jCheckBox[2].setSelected(false);
	                            jCheckBox[3].setSelected(false);
	                            jCheckBox[4].setSelected(false);
	                            jCheckBox[5].setSelected(false);
	                            jCheckBox[6].setSelected(false);
	                            jCheckBox[7].setSelected(false);*/
	                        }
	                    });
	        }
	        return canceselectionljButton;
	    }
	    
//	    private JCheckBox[] getLayoutElementsCHKBX(){
//	    	
//	    	JCheckBox[] chbList = new JCheckBox[lec.getLayoutElements().size()];
//	    	int i=0;
//	    	for(LayoutElement le : lec.getLayoutElements()){
//	    		
//	    	}
	    		

	    private void processChkbx(int num) {

	    	if (jCheckBox[num].isSelected()) {
	    		String name = jCheckBox[num].getText(); 
	            LayoutElement le = lec.getElementByName(name);
	            
//	            System.out.println("Name:" + name);
//	            System.out.println("lec--->" + lec);

	            Parameters p = le.getParametersAtDefault();
	            if (addbefore) {
	                if (citationjTable.getSelectedRow() != -1)
	                    model
	                            .insertRow(
	                                    citationjTable
	                                            .getSelectedRow(),
	                                    new Object[] {
	                                            name,
	                                           le.getRef(),
	                                            p.getValidIf(),
	                                            p
	                                                    .getStartsWith(),
	                                            p.getEndsWith(),
	                                            p
	                                                    .getMaxLength()
	                                                    + "",
	                                            p
	                                                    .getMaxLengthEndsWith(),
	                                            p.getMaxCount()
	                                                    + "",
	                                            p
	                                                    .getMaxCountEndsWith(),
	                                            p
	                                                    .getDelimiter(),
	                                            p
	                                                    .getInternalDelimiter(),
	                                            p
	                                                    .getFontStyleRef() });
	                
	                tblElems.add(citationjTable.getSelectedRow(), le);
	                
	                // addbefore = false;
	                jCheckBox[num].setSelected(false);
	                citationjTable.changeSelection( citationjTable.getSelectedRow() - 1, 0, false, false );
	                citationjTable.scrollRectToVisible(
	                        citationjTable.getCellRect( citationjTable
	                                .getSelectedRow() - 1, 0, true ) );

	            }

	            if (addafter) {
	                model.insertRow(citationjTable
	                        .getSelectedRow() + 1,
	                        new Object[] { 
	                				
	                				name,
	                				le.getRef(),
	                                p.getValidIf(),
	                                p.getStartsWith(),
	                                p.getEndsWith(),
	                                p.getMaxLength() + "",
	                                p.getMaxLengthEndsWith(),
	                                p.getMaxCount() + "",
	                                p.getMaxCountEndsWith(),
	                                p.getDelimiter(),
	                                p.getInternalDelimiter(),
	                                p.getFontStyleRef() });
	                tblElems.add(citationjTable.getSelectedRow()+1, le);
	               
	                // addafter = false;
	                jCheckBox[num].setSelected(false);
	                citationjTable.changeSelection( citationjTable.getSelectedRow() + 1, 0, false, false );
	                citationjTable.scrollRectToVisible(
	                        citationjTable.getCellRect( citationjTable
	                                .getSelectedRow() + 1, 0, true ) );
	            }

	        }
	    	
	    }
	    
	    private void processLayoutElementList() {

	    	Object[] list = addlayoutelementsjList.getSelectedValues();
	    	
	    	for (int i = 0; i < list.length; i++) {
				String name = (String)list[i];
	            LayoutElement le = lec.getElementByName(name);
	            
//	            System.out.println("Name:" + name);
//	            System.out.println("lec--->" + lec);

	            Parameters p = le.getParametersAtDefault();
	            if (addbefore) {
	                if (citationjTable.getSelectedRow() != -1)
	                    model
	                            .insertRow(
	                                    citationjTable
	                                            .getSelectedRow(),
	                                    new Object[] {
	                                            name,
	                                           le.getRef(),
	                                            p.getValidIf(),
	                                            p
	                                                    .getStartsWith(),
	                                            p.getEndsWith(),
	                                            p
	                                                    .getMaxLength()
	                                                    + "",
	                                            p
	                                                    .getMaxLengthEndsWith(),
	                                            p.getMaxCount()
	                                                    + "",
	                                            p
	                                                    .getMaxCountEndsWith(),
	                                            p
	                                                    .getDelimiter(),
	                                            p
	                                                    .getInternalDelimiter(),
	                                            p
	                                                    .getFontStyleRef() });
	                
	                tblElems.add(citationjTable.getSelectedRow(), le);
//	                System.out.println("222Add elem at position:" + citationjTable.getSelectedRow() + "; name=" + name);

	                
	                // addbefore = false;
//	                jCheckBox[num].setSelected(false);
	                citationjTable.changeSelection( citationjTable.getSelectedRow() - 1, 0, false, false );
	                citationjTable.scrollRectToVisible(
	                        citationjTable.getCellRect( citationjTable
	                                .getSelectedRow() - 1, 0, true ) );

	            }

	            if (addafter) {
	                model.insertRow(citationjTable
	                        .getSelectedRow() + 1,
	                        new Object[] { 
	                				
	                				name,
	                				le.getRef(),
	                                p.getValidIf(),
	                                p.getStartsWith(),
	                                p.getEndsWith(),
	                                p.getMaxLength() + "",
	                                p.getMaxLengthEndsWith(),
	                                p.getMaxCount() + "",
	                                p.getMaxCountEndsWith(),
	                                p.getDelimiter(),
	                                p.getInternalDelimiter(),
	                                p.getFontStyleRef() });
	                tblElems.add(citationjTable.getSelectedRow()+1, le);
	                
//	                System.out.println("111Add elem at position:" + (citationjTable.getSelectedRow()+1) + "; name=" + name);
	               
	                // addafter = false;
//	                jCheckBox[num].setSelected(false);
	                citationjTable.changeSelection( citationjTable.getSelectedRow() + 1, 0, false, false );
	                citationjTable.scrollRectToVisible(
	                        citationjTable.getCellRect( citationjTable
	                                .getSelectedRow() + 1, 0, true ) );
	            }

	        }
	    }
	    

	    /**
	     * This method initializes addtocitationstylejButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddtocitationstylejButton() {
	        if (addtocitationstylejButton == null) {
	            addtocitationstylejButton = new JButton();
	            addtocitationstylejButton.setText("Add to Citation Style");
	            addtocitationstylejButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            panel.setVisible(true);
	                            addnewcitationelementjPanel.setVisible(false);
	                            changed = true;
	                            
	                          /*  ArrayList<LayoutElement> elems = (ArrayList<LayoutElement>) le
	                            .getElementsAt("default");
	                    if (citationSelection1 != null) {

	                        int count = model.getRowCount();

	                        for (int i = 0; i < count; i++) {
	                            model.removeRow(0);
	                        }

	                        int i = 1;

	                        for (LayoutElement elem : elems) {


	                            Parameters elemp = elem.getParametersAt("default");*/
	                            
	 
	                            
	                            processLayoutElementList();
//	                            processChkbx(1);
//	                            processChkbx(2);
//	                            processChkbx(3);
//	                            processChkbx(4);
//	                            processChkbx(5);
//	                            processChkbx(6);
//	                            processChkbx(7);


	                            addbefore = false;
	                            addafter = false;
	                        }
	                    });
	        }
	        return addtocitationstylejButton;
	    }

	    /**
	     * This method initializes fifthjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getFifthjCheckBox() {
	        if (jCheckBox[5] == null) {
	        	jCheckBox[5] = new JCheckBox();
	        }
	        return jCheckBox[5];
	    }

	    /**
	     * This method initializes sixthjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getSixthjCheckBox() {
	        if (jCheckBox[6] == null) {
	        	jCheckBox[6] = new JCheckBox();
	        }
	        return jCheckBox[6];    
	    }

	    /**
	     * This method initializes seventhjCheckBox
	     *
	     * @return javax.swing.JCheckBox
	     */
	    private JCheckBox getSeventhjCheckBox() {
	        if (jCheckBox[7] == null) {
	        	jCheckBox[7] = new JCheckBox();
	        }
	        return jCheckBox[7];    
	    }

	    /**
	     * This method initializes newtypejPanel
	     *
	     * @return javax.swing.JPanel
	     */
	    private JPanel getNewtypejPanel() {
	        if (newtypejPanel == null) {
	            addnewtypecsastemplatejLabel = new JLabel();
	            addnewtypecsastemplatejLabel.setBounds(new Rectangle(51, 89, 65, 22));
	            addnewtypecsastemplatejLabel.setText("  Select CS");
	            addnewtypejLabel = new JLabel();
	            addnewtypejLabel.setBounds(new Rectangle(104, 12, 137, 27));
	            addnewtypejLabel.setFont(new Font("Dialog", Font.BOLD, 18));
	            addnewtypejLabel.setText(" Add New Type");
	            newtypeselectjLabel = new JLabel();
	            newtypeselectjLabel.setText("Use Type as Template");
	            newtypeselectjLabel.setBounds(new Rectangle(3, 130, 125, 16));
	            newtypenamejLabel = new JLabel();
	            newtypenamejLabel.setText("Name");
	            newtypenamejLabel.setBounds(new Rectangle(73, 58, 33, 16));
	            newtypejPanel = new JPanel();
	            newtypejPanel.setLayout(null);
	            newtypejPanel.setBounds(new Rectangle(300, 64, 332, 256));
	            newtypejPanel.add(newtypenamejLabel, null);
	            newtypejPanel.add(getNewtypenamejTextField(), null);
	            newtypejPanel.add(newtypeselectjLabel, null);
	            newtypejPanel.add(getNewtypejComboBox(), null);
	            newtypejPanel.add(getNewtypecanceljButton(), null);
	            newtypejPanel.add(getNewtypeokjButton(), null);
	            newtypejPanel.add(addnewtypejLabel, null);
	            newtypejPanel.add(addnewtypecsastemplatejLabel, null);
	            newtypejPanel.add(getAddnewtypecsastemplatejComboBox(), null);
	            newtypejPanel.add(getAddNewEmptyTypejButton(), null);
	        }
	        if (newtypenamejTextField!=null) {
	        	newtypenamejTextField.setText(null);
			}
	        return newtypejPanel;
	    }

	    /**
	     * This method initializes newtypenamejTextField
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getNewtypenamejTextField() {
	        if (newtypenamejTextField == null) {
	            newtypenamejTextField = new JTextField();
	            newtypenamejTextField.setBounds(new Rectangle(136, 55, 165, 20));
	        } 
	        return newtypenamejTextField;
	    }

	    /**
	     * This method initializes newtypejComboBox
	     *
	     * @return javax.swing.JComboBox
	     */
	    private JComboBox getNewtypejComboBox() {
	        if (newtypejComboBox == null) {
	            newtypejComboBox = new JComboBox();

	            newtypejComboBox.setBounds(new Rectangle(134, 125, 167, 25));
	            newtypejComboBox.setEditable(true);
	            
	         /*   CitationStylesCollection csc = null; 
	            try {
					csc = CitationStylesCollection
					.loadFromXml(pathToDefault + "CitationStyle.xml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            String name;
	            ctc = csc.getCitationStyleByName("Default");

	            if (ctc != null) {

	                for (LayoutElement csld : ctc
	                        .getCsLayoutDefinitions()) {
	                    name = csld.getName(); 
	                newtypejComboBox.addItem(name);
	            }
	        }*/
	        }
	        return newtypejComboBox;
	    }

	    /**
	     * This method initializes newtypecanceljButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getNewtypecanceljButton() {
	        if (newtypecanceljButton == null) {
	            newtypecanceljButton = new JButton();
	            newtypecanceljButton.setText("Cancel");
	            newtypecanceljButton.setBounds(new Rectangle(7, 164, 73, 26));
	            newtypecanceljButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {
	                            panel.setVisible(true);
	                            newtypejPanel.setVisible(false);
	                        }
	                    });
	        }
	        return newtypecanceljButton;
	    }

	    /**
	     * This method initializes newtypeokjButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getNewtypeokjButton() {
	        if (newtypeokjButton == null) {
	            newtypeokjButton = new JButton();
	            newtypeokjButton.setText("Create");
	            newtypeokjButton.setLocation(new Point(87, 164));
	            newtypeokjButton.setSize(new Dimension(72, 26));
	            newtypeokjButton
	                    .addActionListener(new java.awt.event.ActionListener() {
	                        public void actionPerformed(java.awt.event.ActionEvent e) {

	                            if (!newtypenamejTextField.getText().equals("")) {
	                                panel.setVisible(true);
	                                newtypejPanel.setVisible(false);
	                                if (newtypenamejTextField != null) {
	                                	LayoutElement le = ctc.getElementByName((String)newtypejComboBox.getSelectedItem());
	                                	LayoutElement lenew = (LayoutElement) le.clone();
	                                    lenew.setName(newtypenamejTextField.getText());

	                                        if (cs.getElementByName(lenew.getName())==null  ) {
	                                            citationjComboBox.addItem(lenew.getName());
	                                            cs.addCsLayoutDefinition(lenew);
	                                        }
	                                    /*System.out.println("old le="+
	                                            lec
	                                            .getElementByName((String) newtypejComboBox
	                                                    .getSelectedItem()));
	                                    System.out.println("le(cloned)="+ lenew);*/

	                                    citationjComboBox.setSelectedItem(newtypenamejTextField
	                                                    .getText());

	                                    //System.out.println("="+ newtypenamejTextField.getText());
	                                    
	                                }
	                                changed = true;
	                            } else {
	                                JOptionPane.showMessageDialog(frame,
	                                        "Please enter the New Type name",
	                                        "Inane warning",
	                                        JOptionPane.WARNING_MESSAGE);
	                            }
	                            
	                        }
	                    });
	        }
	        return newtypeokjButton;
	    }

	    /**
	     * This method initializes addnewcsjPanel
	     *
	     * @return javax.swing.JPanel
	     */
	    private JPanel getAddnewcsjPanel() {
	        if (addnewcsjPanel == null) {
	            addnewcitationstylejLabel1 = new JLabel();
	            addnewcitationstylejLabel1.setText(" Add new Citation Style");
	            addnewcitationstylejLabel1.setFont(new Font("Dialog", Font.BOLD, 18));
	            addnewcitationstylejLabel1.setBounds(new Rectangle(46, 8, 214, 22));
	            usecsastemplatejLabel = new JLabel();
	            usecsastemplatejLabel.setText("Use CS as Template");
	            usecsastemplatejLabel.setBounds(new Rectangle(7, 64, 114, 16));
	            newcsnamejLabel = new JLabel();
	            newcsnamejLabel.setText("New CS Name");
	            newcsnamejLabel.setBounds(new Rectangle(24, 42, 80, 16));
	            addnewcsjPanel = new JPanel();
	            addnewcsjPanel.setLayout(null);
	            addnewcsjPanel.setBounds(new Rectangle(361, 46, 303, 135));
	            addnewcsjPanel.add(newcsnamejLabel, null);
	            addnewcsjPanel.add(getNewcsnamejTextField(), null);
	            addnewcsjPanel.add(usecsastemplatejLabel, null);
	            addnewcsjPanel.add(getSelectcsastemplatejComboBox(), null);
	            addnewcsjPanel.add(getAddnewcscancejButton(), null);
	            addnewcsjPanel.add(getAddnewcscreatejButton(), null);
	            addnewcsjPanel.add(addnewcitationstylejLabel1, null);
	        }
	        return addnewcsjPanel;
	    }

	    /**
	     * This method initializes newcsnamejTextField
	     *
	     * @return javax.swing.JTextField
	     */
	    private JTextField getNewcsnamejTextField() {
	        if (newcsnamejTextField == null) {
	            newcsnamejTextField = new JTextField();
	            newcsnamejTextField.setBounds(new Rectangle(129, 40, 151, 20));
	        }
	        return newcsnamejTextField;
	    }

	    /**
	     * This method initializes selectcsastemplatejComboBox
	     *
	     * @return javax.swing.JComboBox
	     */
	    private JComboBox getSelectcsastemplatejComboBox() {
	        if (selectcsastemplatejComboBox == null) {
	            selectcsastemplatejComboBox = new JComboBox();
	            selectcsastemplatejComboBox.setBounds(new Rectangle(129, 60, 151, 25));
	            File templPath = new File("CitationStyles\\");
	            // take only files!!!
	           String[] files = templPath.list(
	                       new FilenameFilter() {
	                           public boolean accept(File dir, String name){
	                               return (new File(dir, name)).isDirectory();
	                           }
	                       }
	                   );
	           for (int i = 0; i < files.length; i++) {
	        	   selectcsastemplatejComboBox.addItem(files[i]);
	           }
	            /*String name;
	            for (CitationStyle cs : csc.getCitationStyles()) {
	                name = cs.getName(); // put this in pull-down list
	                selectcsastemplatejComboBox.addItem(name);
	            }*/
	        }
	        return selectcsastemplatejComboBox;
	    }

	    /**
	     * This method initializes addnewcscancejButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddnewcscancejButton() {
	        if (addnewcscancejButton == null) {
	            addnewcscancejButton = new JButton();
	            addnewcscancejButton.setText("Cancel");
	            addnewcscancejButton.setBounds(new Rectangle(28, 85, 73, 26));
	            addnewcscancejButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {

	                    addnewcsjPanel.setVisible(false);
	                    invokeStartPanel();
	                }
	            });
	        }
	        return addnewcscancejButton;
	    }


	    private void invokeStartPanel(){
	        editjButton.setVisible(true);
	        listofcitationstylesjComboBox.setVisible(true);
	        newjButton.setVisible(true);
	        citationstyleselectionjLabel.setVisible(true);
	        selectdatasourcejLabel.setVisible(true);
	        selectxmlfilejLabel.setVisible(true);
	        xmlfilejTextField.setVisible(true);
	        selectjButton.setVisible(true);
	        validatefilejButton.setVisible(true);
	        viewoutputjLabel.setVisible(true);
	        viewoutputjComboBox.setVisible(true);
	        runoutputjButton.setVisible(true);
	    }
	    
	    /**
	     * This method initializes addnewcscreatejButton
	     *
	     * @return javax.swing.JButton
	     */
	    private JButton getAddnewcscreatejButton() {
	        if (addnewcscreatejButton == null) {
	            addnewcscreatejButton = new JButton();
	            addnewcscreatejButton.setText("Create");
	            addnewcscreatejButton.setBounds(new Rectangle(158, 85, 72, 26));
	            addnewcscreatejButton.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent e) {
	                    String newName = newcsnamejTextField.getText();
	                    String templName = (String)selectcsastemplatejComboBox.getSelectedItem();
	                    
	                    
	    	            String[] styleNames = null;
						try {
							styleNames = ProcessCitationStyles.getCitationStylesList();
						} catch (IllegalArgumentException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						} catch (IOException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						} 
						
						
	                    if (
	                    		!newName.equals("") && 
	                    		!Utils.findInList(styleNames, newName)
	                    	)
	                    	 {
	                    
	                   
//	                    cs = csc.getCitationStyleByName(templName);
//	                    /*System.out.println("old cs="+
//	                            csc.getCitationStyleByName
//	                            ((String) selectcsastemplatejComboBox
//	                                    .getSelectedItem()));*/
	//
	//
//	                        csnew = (CitationStyle) cs.clone();

	                        //System.out.println("cs(cloned)="+ csnew);
	                        File path = new File (".");
	                        try {
	                            path = new File(path.getCanonicalPath());
	                        } catch (IOException e2) {
	                            // TODO Auto-generated catch block
	                            e2.printStackTrace();
	                        }
	                        File cspath = new File(path + "\\CitationStyles");
	                        
	                        ProcessCitationStyles pcs = new ProcessCitationStyles(); 
	                        try {
	                        	pcs.createNewCitationStyle(cspath, templName, newName);
							} catch (JRException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							namejTextField.setText(newName);
//	                        csnew.setName(newName);
	                        //System.out.println("csnew="+csnew);

	                        listofcitationstylesjComboBox.addItem(newName);
	                        
	                        try {
								csc = CitationStylesCollection.loadFromXml("CitationStyles\\"+ newName +"\\" + "CitationStyle.xml");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SAXException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                        
							listofcitationstylesjComboBox.setSelectedItem(newName);
							
//							CitationStyle csnew = csc.getCitationStyleByName(newName);
//							
	//
//	                            if (csnew != null) {
	//
//	                                for (LayoutElement csld : csnew
//	                                        .getCsLayoutDefinitions()) {
//	                                    citationjComboBox.addItem(csld.getName());
//	                                }
//	                            }
	                            
	                            

//	                            if (selectcsastemplatejComboBox.getSelectedItem().equals("Default")) {
//	                                selectjCheckBox.setEnabled(false);
//	                                selectjLabel.setVisible(false);
//	                                citationjComboBox.setVisible(false);
//	                                addtypejButton.setVisible(false);
//	                                removetypejButton.setVisible(false);
//	                                selectjCheckBox.setSelected(false);
//	                            } else {
//	                                selectjCheckBox.setEnabled(true);
//	                                selectjCheckBox.setFocusable(true);
//	                                selectjLabel.setVisible(true);
//	                                citationjComboBox.setVisible(true);
//	                                addtypejButton.setVisible(true);
//	                                removetypejButton.setVisible(true);
//	                                selectjCheckBox.setSelected(true);
	//
//	                            }
	                        
	                            addnewcsjPanel.setVisible(false);
//	                            panel.setVisible(true);
	                            
	                            // invoke start panel!!!!!
	                            invokeStartPanel();
	                            // end of invoke start panel!!!!!


	                    }else {
	                        JOptionPane.showMessageDialog(frame,
	                                "Please enter the New CS name",
	                                "Inane warning",
	                                JOptionPane.WARNING_MESSAGE);
	                    }

	                }
	            });
	        }
	        return addnewcscreatejButton;
	    }

	}
