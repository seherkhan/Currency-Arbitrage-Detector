package GUI;

import GraphAndAlgo.ForexGraph;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Created by Seher Khan on 11/29/2017.
 */
public class Screen3Controller implements Initializable{
    private Main application;
    private ForexGraph g;

    @FXML private ChoiceBox<String> sourceSelector;
    @FXML private ChoiceBox<String> destinationSelector;

    @FXML private TextField detectField;
    @FXML private TextArea cycleField;
    @FXML private TextField iaField;

    @FXML private Button iaBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        g=Screen1Controller.getGraph();
        sourceSelector.setItems(FXCollections.observableArrayList(g.getCurrenciesFull()));
        destinationSelector.setItems(FXCollections.observableArrayList(g.getCurrenciesFull()));
        destinationSelector.setDisable(true);
        detectField.setEditable(false);
        cycleField.setEditable(false);
        cycleField.setWrapText(true);
        iaField.setEditable(false);
        iaBtn.setDisable(true);

        sourceSelector.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(detectField.getText()!=null)
                    detectField.setText(null);
                if(cycleField.getText()!=null)
                    cycleField.setText(null);
                if(!destinationSelector.isDisabled())
                    destinationSelector.setDisable(true);
                if(!iaBtn.isDisabled())
                    iaBtn.setDisable(true);
                g.resetGraph();
            }
        });

        destinationSelector.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(iaBtn.isDisabled())
                    iaBtn.setDisable(false);
                if(iaField.getText()!=null)
                    iaField.setText(null);
            }
        });


    }
    @FXML public void detectPressed(){

        if(sourceSelector.getValue()==null) System.out.println("Select a currency!");
        else{
            if(g.getNWCycle()!=null)
                g.resetGraph();
            if(g.detectNWCycle(sourceSelector.getSelectionModel().getSelectedIndex())){
            detectField.setText("Yes");
            String str="";
            LinkedList<Integer> cycle = g.getNWCycle();
            for(Integer i : cycle)
                str+=g.getCurrencies()[i]+">";
            cycleField.setText(str);

            if(destinationSelector.isDisabled()){
                destinationSelector.setDisable(false);
            }
            destinationSelector.setValue(null);
            if(!iaBtn.isDisable())
                iaBtn.setDisable(true);

        }
        else{
            detectField.setText("No");
        }
        }
    }



    public void setApp(Main application){
        this.application = application;
    }


    @FXML
    public void iaPressed(){
        if(g.infiniteArbitragePossible(destinationSelector.getSelectionModel().getSelectedIndex())){
            iaField.setText("Yes");
        }
        else{
            iaField.setText("No");
        }
    }


    @FXML
    public void restartPressed(){
        g=null;
        try {
            application.enterScreen1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
