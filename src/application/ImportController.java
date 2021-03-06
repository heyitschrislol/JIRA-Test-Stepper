package application;

import java.io.BufferedReader;
//import com.sun.tools.javac.util.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImportController implements Initializable {

	ObservableList<Step> randomsteps = FXCollections.observableArrayList();
	ObservableList<Step> teststeps = FXCollections.observableArrayList();
	ObservableList<FormattedItem> jsonsteps = FXCollections.observableArrayList();
	ArrayList<String> jsontext = new ArrayList<>();
	StringBuilder htmltext = new StringBuilder();
	StringBuilder jsontexty = new StringBuilder();
	int readCSVcount = 0;
	int remQuotescount = 0;
	int openFilecount = 0;
	int jsonStepscount = 0;
	int sortStepscount = 0;
	File file;
	File jsonfile;
	File htmlfile;



	@FXML
	private SplitPane mainsplit;
	@FXML
	private Pane exportpane;
	@FXML
	private Pane tableviewpane;
	@FXML
	private Pane editorpane;
	@FXML
	private Pane textpane;
	@FXML
	private Button openfilebtn;
	@FXML
	private Button exportjsonbtn;
	@FXML
	private Button exporthtmlbtn;
	@FXML
	private Button clearallbtn;
	@FXML
	private Button showtablebtn;
	@FXML
	private Button tableviewbtn;
	@FXML
	private Button jsonviewbtn;
	@FXML
	private Button htmlrawviewbtn;
	@FXML
	private Button htmltableviewbtn;
	@FXML
	private ToolBar previewbar;
	@FXML
	private Label exportlbl;
	@FXML
	private Label openfilelbl;
	@FXML
	private TextField filepathtxtfld;
	@FXML
	private TextArea textarea;
	@FXML
	private HTMLEditor htmleditor;
	@FXML
	private TableView<Step> stepstable = new TableView<Step>();
	@FXML
	private TableColumn<Step, String> stepnumcol;
	@FXML
	private TableColumn<Step, String> stepcol;
	@FXML
	private TableColumn<Step, String> datacol;
	@FXML
	private TableColumn<Step, String> resultcol;

	public ImportController() {
		stepnumcol = new TableColumn("#");
		stepnumcol.setCellValueFactory(new PropertyValueFactory<Step, String>("stepnum"));
		stepcol = new TableColumn("Step");
		stepcol.setCellValueFactory(new PropertyValueFactory<Step, String>("step"));
		datacol = new TableColumn("Data");
		datacol.setCellValueFactory(new PropertyValueFactory<Step, String>("data"));
		resultcol = new TableColumn("Expected Result");
		resultcol.setCellValueFactory(new PropertyValueFactory<Step, String>("result"));

	}

	@SuppressWarnings("unchecked")
	@FXML
	public void openFileHandler() throws IOException {
		clearAllHandler();
		
		try {
			FileChooser chooser = new FileChooser();
			chooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("Text Files", "*.txt"));
			chooser.setInitialDirectory(new File(System.getProperty("user.home")));

			file = chooser.showOpenDialog(new Stage()).getAbsoluteFile();
			if (file != null) {
				openfilelbl.setTextFill(Color.web("3dff77"));
				openfilelbl.setText("CSV file opened successfully");
				jsonsteps.clear();
				teststeps.clear();
				filepathtxtfld.clear();
				String path = "///" + file.getAbsolutePath();
				ArrayList<String> rawlines = readCSV(new File(path));
				ArrayList<String> steplines = removeQuotes(rawlines);
				randomsteps = sortSteps(steplines);
				teststeps = randomsteps;
//				testTestSteps();
				stepstable.setItems(teststeps);
				if (stepstable.getColumns().isEmpty()) {
					stepstable.getColumns().addAll(stepnumcol, stepcol, datacol, resultcol);
				}
				convertToJSON();
				convertToHTML();
//				testExportJSON();
//				testJSONArray();
				filepathtxtfld.setText(file.getPath());
				exportjsonbtn.setDisable(false);
				clearallbtn.setDisable(false);
				exporthtmlbtn.setDisable(false);
				previewbar.setDisable(false);
			}
		} catch (FileNotFoundException e) {
			openfilelbl.setTextFill(Color.web("fd1a4a"));
			openfilelbl.setText(e.getMessage());
			
		} catch (InvalidFileTypeException i) {
			openfilelbl.setTextFill(Color.web("fd1a4a"));
			openfilelbl.setText(i.getMessage());
			throw new InvalidFileTypeException(file.getName(), "*.csv");
			
		}
		
	}
	@FXML
	public void exportJSONHandler() {
		int size;
		size = jsonsteps.size();

		FileChooser chooser = new FileChooser();
		chooser.setInitialFileName("jsonsteps.json");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("JSON Files", "*.json"));
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = chooser.showSaveDialog(new Stage());
		if (file != null) {
			File dir = file.getParentFile();// gets the selected directory
			chooser.setInitialDirectory(dir);
			try {
				// Create file
				FileWriter stream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(stream);
				out.newLine();
//				for (int i = 0; i < size; i++) {
//					out.write(jsontext.get(i));
//					out.newLine();
//				}
				out.write(jsontexty.toString());
				jsonfile = file;
				// Close the output stream
				out.close();
				exportlbl.setTextFill(Color.web("3dff77"));
				exportlbl.setText("Steps exported as JSON file successfully!");
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
				exportlbl.setTextFill(Color.web("fd1a4a"));
				exportlbl.setText("JSON export failed. " + e.getMessage());
			}
		}
	}
	@FXML
	public void exportHTMLHandler() {
		tableviewpane.setDisable(true);
		tableviewpane.setVisible(false);
		textpane.setDisable(false);
		textpane.setVisible(true);
		textarea.setDisable(false);
		textarea.setVisible(true);
		FileChooser chooser = new FileChooser();
		chooser.setInitialFileName("htmlstepsTBL.html");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("HTML Files", "*.html"), new ExtensionFilter("Text Files", "*.txt"));
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = chooser.showSaveDialog(new Stage());
		if (file != null) {
			File dir = file.getParentFile();// gets the selected directory
			chooser.setInitialDirectory(dir);
			try {
				// Create file
				FileWriter stream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(stream);
				out.newLine();
				out.write(htmltext.toString());
				jsonfile = file;
				// Close the output stream
				out.close();
				exportlbl.setTextFill(Color.web("3dff77"));
				exportlbl.setText("Steps exported as HTML file successfully!");
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
				exportlbl.setTextFill(Color.web("fd1a4a"));
				exportlbl.setText("HTML export failed. " + e.getMessage());
			}
		}
	}
	@FXML
	public void showTableViewHandler() {
		/*
		 * TURN ON
		*/
		tableviewpane.setDisable(false);
		tableviewpane.setVisible(true);
		
		
		/*
		 * TURN OFF
		*/
		textpane.setDisable(true);
		textpane.setVisible(false);
		textarea.setDisable(true);
		textarea.setVisible(false);
		editorpane.setDisable(true);
		editorpane.setVisible(false);
		htmleditor.setDisable(true);
		htmleditor.setVisible(false);
	}
	public void showJSONHandler() {
	/*
	 * TURN ON
	*/
		textpane.setDisable(false);
		textpane.setVisible(true);
		textarea.setDisable(false);
		textarea.setVisible(true);
		textarea.setText(htmltext.toString());
//		for (String s : jsontext) {
//			textarea.setText(textarea.getText() + "\n" + s);
//		}
		textarea.setText(jsontexty.toString());

	/*
	 * TURN OFF
	*/
		tableviewpane.setDisable(true);
		tableviewpane.setVisible(false);
		editorpane.setDisable(true);
		editorpane.setVisible(false);
		htmleditor.setDisable(true);
		htmleditor.setVisible(false);
	}
	public void showHTMLHandler() {
	/*
	 * TURN ON
	 */
		textpane.setDisable(false);
		textpane.setVisible(true);
		textarea.setDisable(false);
		textarea.setVisible(true);
		textarea.setText(htmltext.toString());
	
	/*
	 * TURN OFF
	 */
		tableviewpane.setDisable(true);
		tableviewpane.setVisible(false);
		editorpane.setDisable(true);
		editorpane.setVisible(false);
		htmleditor.setDisable(true);
		htmleditor.setVisible(false);
	}
	@FXML
	public void showTableHandler() {
		/*
		 * TURN ON
		*/
		editorpane.setDisable(false);
		editorpane.setVisible(true);
		htmleditor.setDisable(false);
		htmleditor.setVisible(true);
		htmleditor.setHtmlText(htmltext.toString());
		
		/*
		 * TURN OFF
		*/
		tableviewpane.setDisable(true);
		tableviewpane.setVisible(false);
		textpane.setDisable(true);
		textpane.setVisible(false);
		textarea.setDisable(true);
		textarea.setVisible(false);
	}
	@FXML
	public void clearAllHandler() {
		openfilelbl.setText("");
		exportlbl.setText("");
		if (file != null) {
			filepathtxtfld.setText("");
		}
		if (randomsteps.size() > 0) {
			randomsteps.clear();
		}
		if (teststeps.size() > 0) {
			teststeps.clear();
		}
		if (jsonsteps.size() > 0) {
			jsonsteps.clear();
		}
		if (jsontext.size() > 0) {
			jsontext.clear();
		}
		if (jsontexty.length() > 0) {
			jsontexty.delete(0, jsontexty.length());
		}
		if (stepstable.getItems().size() > 0) {
			stepstable.getItems().clear();
		}
		exporthtmlbtn.setDisable(true);
		exportjsonbtn.setDisable(true);
		clearallbtn.setDisable(true);
	}

	
	
		/* 
		 * --------------------------------------------------------------------------------------------------------
		 * -----------------------------------------------------------G O S E---------------------------------------------------
		 * -- T H E S E   M E T H O D S   A R E   T H E   G O L D E N   G O S E    R I G H T   H E R E--------------------------------
		 * ---------------------------------------------------------------G O S E----------------------------------------------------
		 * ----------------------------------------------------------------------------------------------------------------------------
		*/ 
	
	
	/*
	 * Method to read the data from the source CSV file, filet it, and place the result in an ArrayList<String>
	 */
	public ArrayList<String> readCSV(File path) throws IOException {
		String line = "";
		StringBuilder sb = new StringBuilder();
		ArrayList<String> rawlines = new ArrayList<String>();
		String[] lines;
		BufferedReader br = new BufferedReader(new FileReader(path));
		int i = 0;
		while ((line = br.readLine()) != null) {
			if (line.matches("Step@Data@Expected Result")) {
				continue;
			}
			if (i == 0) {
				sb.append(line);
				i++;
				continue;
			}
			if (line.contains("@")) {
				sb.append("@" + line);
			} else {
				sb.append("%%" + line);
			}
		}
		lines = sb.toString().split("@");
		for (String s : lines) {
			if (s.isBlank()) {
				s = "--";
			}
			rawlines.add(s);
			i++;
		}
		br.close();
//		testReadCSV(rawlines);
		return rawlines;
	}
	
	/*
	 * Method to remove quotation marks from the extracted data since those become problematic when converting the data to JSON format
	 */
	public ArrayList<String> removeQuotes(ArrayList<String> rawlines) {
		String formattedtext = "";
		ArrayList<String> formattedlines = new ArrayList<>();
		for (String s : rawlines) {
			formattedtext = s;
			if (s.contains("\"")) {
				formattedtext = s.replace('\"', ' ');
			}
			formattedlines.add(formattedtext);
		}
//		testRemoveQuotes(formattedlines);
		return formattedlines;
	}
	
	/*
	 * Method that sorts the extracted data into 2 separate custom classes: Step and FormattedItem. Each Step and each FormattedItem is 
	 * then stored in a class-level ObservableList that holds its particular class type.
	 */
	public ObservableList<Step> sortSteps(ArrayList<String> rawlines) {
		int index = 0;
		int count = 0;
		Step step;
		String[] pholder = new String[3];
		for (int i = 0; i < rawlines.size(); i++) {
			pholder[index] = rawlines.get(i);
			if (index == 2) {
				index = 0;
				count++;
				step = new Step(String.valueOf(count), pholder[0], pholder[1], pholder[2]);
				teststeps.add(step);
				jsonsteps.add(new FormattedItem(step));
			} else {
				index++;
			}
		}
//		testSortSteps(teststeps);
		return teststeps;
	}
	
	/*
	 * Method that converts the ObservableList of FormattedItems to JSON text and places each line in a class-level ArrayList
	 */
	public void convertToJSON() {
		int index = 0;
		int size;
		int sizeb;
		FormattedItem item;
		// populate the ObservableList<FormattedItem> with the sorted steps from the loaded csv
//		for (Step s : teststeps) {
//			jsonsteps.add(new FormattedItem(s));
//		}
		size = jsonsteps.size();
		sizeb = teststeps.size();
		jsontexty.append("[" + "\n");
//		jsontext.add("[");
		for (int i = 0; i < size; i++) {
			item = jsonsteps.get(i);
			if ((size - 1) != i) {
//				jsontext.add("{");
				jsontexty.append("{"  + "\n");
				jsontexty.append(item.getStepnumber().getKey() + " : " + item.getStepnumber().getValue() + "," + "\n");
				jsontexty.append(item.getStep().getKey() + " : " + item.getStep().getValue() + "," + "\n");
				jsontexty.append(item.getData().getKey() + " : " + item.getData().getValue() + "," + "\n");
				jsontexty.append(item.getResult().getKey() + " : " + item.getResult().getValue() + "," + "\n");
				jsontexty.append("}" + "\n");
//				jsontext.add(item.getStepnumber().getKey() + " : " + item.getStepnumber().getValue() + ",");
//				jsontext.add(item.getStep().getKey() + " : " + item.getStep().getValue() + ",");
//				jsontext.add(item.getData().getKey() + " : " + item.getData().getValue() + ",");
//				jsontext.add(item.getResult().getKey() + " : " + item.getResult().getValue());
				jsontext.add("},");
			} else {
				jsontexty.append("{"  + "\n");
				jsontexty.append(item.getStepnumber().getKey() + " : " + item.getStepnumber().getValue() + "," + "\n");
				jsontexty.append(item.getStep().getKey() + " : " + item.getStep().getValue() + "," + "\n");
				jsontexty.append(item.getData().getKey() + " : " + item.getData().getValue() + "," + "\n");
				jsontexty.append(item.getResult().getKey() + " : " + item.getResult().getValue() + "," + "\n");
				jsontexty.append("}"  + "\n");
				jsontexty.append("]");

				
//				jsontext.add("{");
//				jsontext.add(item.getStepnumber().getKey() + " : " + item.getStepnumber().getValue() + ",");
//				jsontext.add(item.getStep().getKey() + " : " + item.getStep().getValue() + ",");
//				jsontext.add(item.getData().getKey() + " : " + item.getData().getValue() + ",");
//				jsontext.add(item.getResult().getKey() + " : " + item.getResult().getValue());
//				jsontext.add("}");
//				jsontext.add("]");
			}
		}
	}
	public void convertToHTML() {
		StringBuilder htmlbuilder = new StringBuilder();
		htmlbuilder.append("<table style = 'font-size: 10px'>" + "\n"
				+ "<thead>" + "\n"
				+ "<tr style = 'border: 1px solid black'>" + "\n"
				+ "<th style = 'border: 1px solid black'>#</th>" + "\n"
				+ "<th style = 'border: 1px solid black'>Step</th>" + "\n"
				+ "<th style = 'border: 1px solid black'>Data</th>" + "\n"
				+ "<th style = 'border: 1px solid black'>Expected Results</th>" + "\n"
				+ "</tr>" + "\n"
				+ "</thead>" + "\n"
				+ "<tbody>");
		for (FormattedItem f : jsonsteps) {
			htmlbuilder.append(f.htmlify());
		}
		htmlbuilder.append("</tr>" + "\n"
							+ "</tbody>" + "\n"
							+ "</table>");
		htmltext = htmlbuilder;

	}
	public void clearTable() {
		if (stepstable.getItems().size() > 0) {
			stepstable.getItems().clear();
		}
	}
	
	
	
	
	
	// ------------------------------------------
	// --
	// -- T E S T E R S --
	// --
	// ------------------------------------------
	public void testTestSteps() {
		int index = 1;
		System.out.println( "C O M P L E T E D   T E S T   S T E P S   C H E C K");
		System.out.println( "-------------------------------------------------");
		for (int i = 0; i < teststeps.size(); i++) {
			openFilecount++;
		}
		System.out.println("The currently-executing OpenFileHandler has run the primary formatting methods and has generated a pristine, ordered list of Steps for the TableView to populate with");
		System.out.println("the resulting observablelist  called 'teststeps' is size: " + openFilecount);
		System.out.println("---------");

	}
	public void testSortSteps(ObservableList<Step> sortedsteps) {
		System.out.println( "S O R T   S T E P S   P H A S E");
		System.out.println( "-------------------------------");
		for (int i = 0; i < sortedsteps.size(); i++) {
			sortStepscount++;
		}
		System.out.println("The 'sortSteps()' method has finished running, a loop through the data items of the passed ArrayList takes 3 sequential values at a time and converts them into a single Step");
		System.out.println("the resulting steps are added to an observablelist called 'teststeps' which is size: " + sortStepscount);
		System.out.println("---------");

	}
	public void testRemoveQuotes(ArrayList<String> dequotedlines) {
		System.out.println( "R E M O V E   Q U O T A T I O N S   P H A S E");
		System.out.println( "---------------------------------------------");
		for (int i = 0; i < dequotedlines.size(); i++) {
			remQuotescount++;
		}
		System.out.println("The 'removeQuotes' method has finished running. This method looped through each data item within an ArrayList and removed any double-quotation marks found in the text, replacing the character with whitespace");
		System.out.println("the resulting arraylist called 'formattedlines' is size: " + (remQuotescount));
		System.out.println("---------");
	}
	public void testExportJSON() {
		System.out.println( "E X P O R T   J S O N   P H A S E");
		System.out.println( "---------------------------------------------");
		for (int i = 0; i < jsonsteps.size(); i++) {
			jsonStepscount++;
		}
		System.out.println("The 'exportJSONHandler' method has finished running, the List of Steps was converted to a list of FormattedSteps and written to a JSON file");
		System.out.println("the resulting observablelist called 'jsonsteps' is size: " + (jsonStepscount));
		System.out.println("---------");
	}
	public void testJSONArray() {
		System.out.println( "A R R A Y   J S O N   P H A S E");
		System.out.println( "---------------------------------------------");
		System.out.println("The 'exportJSONHandler' method has finished running, the List of Steps was converted to a list of FormattedSteps and written to a JSON file");
		System.out.println("---------");
		for (int i = 0; i < jsontext.size(); i++) {
			System.out.println(jsontext.get(i));
		}
		
	}
	public void testReadCSV(ArrayList<String> rawlines) {
		System.out.println( "R E A D   C S V   P H A S E");
		System.out.println( "---------------------------------------------");
		for (int i = 0; i < rawlines.size(); i++) {
			readCSVcount++;
		}
		System.out.println("The 'readCSV' method has finished running. The csv file read and converted with the 'Step/Data/Expected Result' header removed");
		System.out.println("the resulting arraylist called 'rawlines' is size: " + (readCSVcount));
		System.out.println("---------");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	

}
