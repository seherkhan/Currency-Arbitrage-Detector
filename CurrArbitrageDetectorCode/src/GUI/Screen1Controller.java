package GUI;

import GraphAndAlgo.ForexGraph;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;


public class Screen1Controller implements Initializable {
    private Main application;
    private LinkedList<String> currenciesFull;
    private static ForexGraph g;

    @FXML private ChoiceBox<File> filePicker;
    @FXML private ListView<String> currSelector;

    @FXML private Button fetch;

    @FXML private Button selectAll;

    @FXML private Button deselectAll;

    @FXML private Button redefineEdges;

    @FXML private Button create;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String path = getJARPath();
        populateFileList(path);

        String finalPath = path;
        fetch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Running Script... Restart program when script is done running.");
                try {
                    Runtime.getRuntime().exec(finalPath +"\\forex_scraper.exe", null, new File(finalPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });

        currSelector.setDisable(true);
        filePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!currSelector.isDisabled())
                    currSelector.setDisable(true);
            }
        });
        selectAll.setDisable(true);
        deselectAll.setDisable(true);
        redefineEdges.setDisable(true);
        create.setDisable(true);
        try {
            populateCurrencyList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        g=null;
    }

    private void populateCurrencyList() throws IOException {
        currenciesFull = new LinkedList<>();
        InputStream is = this.getClass().getResourceAsStream("currencylist_forex_data.csv");
        /*
        if File is being access from resources folder
        //parameter:
        //File dataFile
        String file="resources/currencylist_forex_data.csv";
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DataInputStream in = new DataInputStream(fstream);
        */

        DataInputStream in = new DataInputStream(is);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        //Read File Line By Line
        String str = br.readLine();
        while(str!=null && str.length()!=0){
            currenciesFull.add(str.split(",")[0]);
            str=br.readLine();
        }
        br.close();

        //System.out.println(FXCollections.observableArrayList(currenciesFull));
        currSelector.setItems(FXCollections.observableArrayList(currenciesFull));
        currSelector.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML private void selectFilePressed() throws IOException {
        if(filePicker.getValue() == null)
            System.out.println("Pick a file.");
        else{
            //populateCurrencyList(filePicker.getValue());
            currSelector.setDisable(false);
            selectAll.setDisable(false);
            deselectAll.setDisable(false);
            redefineEdges.setDisable(false);
            create.setDisable(false);
        }
    }

    @FXML private void deselectAllPressed(){
        currSelector.getSelectionModel().clearSelection();
    }
    @FXML private void selectAllPressed(){
        currSelector.getSelectionModel().selectAll();
    }

    @FXML private void redefineEdgesPressed() throws IOException {
        //System.out.println(currSelector.getSelectionModel().getSelectedItems());
        //System.out.println(currSelector.getSelectionModel().getSelectedIndices());
        if(filePicker.getValue() == null)
            System.out.println("Pick a file.");
        else{
            File file = filePicker.getValue();
            if(currSelector.getSelectionModel().getSelectedIndices().size()<3)
                System.out.println("Select at least three items!");

            else{
                try{
                    g = new ForexGraph(file,currSelector.getSelectionModel().getSelectedIndices());
                    //g.displayGraph();

                    application.enterScreen2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @FXML private void createPressed() throws IOException {
        //System.out.println(currSelector.getSelectionModel().getSelectedItems());
        //System.out.println(currSelector.getSelectionModel().getSelectedIndices());
        if(filePicker.getValue() == null)
            System.out.println("Pick a file.");
        else{
            File file = filePicker.getValue();
            if(currSelector.getSelectionModel().getSelectedIndices().size()<3)
                System.out.println("Select at least three items!");

            else{
                try{
                    g = new ForexGraph(file,currSelector.getSelectionModel().getSelectedIndices());
                    //g.displayGraph();
                    application.enterScreen3();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Called in Screen2 and Screen3
     * @return
     */
    public static ForexGraph getGraph(){
        return g;
    }

    /**
     * Called in Main. Required to change screens.
     * @param application
     */
    public void setApp(Main application){
        this.application = application;
    }

    private String getJARPath(){
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
            if(path.contains(".jar"))
                path=path.substring(0,path.length()-24)+"data";
            else
                path=path.substring(0,path.length()-21)+"data";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }
    private void populateFileList(String path){
        //filePicker.setItems(FXCollections.observableArrayList((new File("resources/data")).listFiles())); //if file in resources folder

        filePicker.setItems(FXCollections.observableArrayList((new File(path)).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }})));
    }

}


