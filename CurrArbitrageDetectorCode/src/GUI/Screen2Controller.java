package GUI;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 * Created by Seher Khan on 12/4/2017.
 */
public class Screen2Controller implements Initializable{

    private String[] currencies;
    @FXML private GridPane gridPane;
    private Main application;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencies = Screen1Controller.getGraph().getCurrencies();

        gridPane.setHgap(10);
        gridPane.setVgap(10);


        //first row
        for(int c=1; c<currencies.length+1;c++){
            Label currCode2 = new Label(currencies[c-1]);
            GridPane.setConstraints(currCode2,c,0);
            currCode2.setAlignment(Pos.CENTER);
            gridPane.getChildren().add(currCode2);
        }

        //subsequent rows
        for(int r=1; r<currencies.length+1;r++){
            Label currCode1 = new Label(currencies[r-1]);
            GridPane.setConstraints(currCode1,0,r);
            currCode1.setAlignment(Pos.CENTER);
            gridPane.getChildren().add(currCode1);
            for(int c=1;c<currencies.length+1;c++){
                if(r!=c){
                    CheckBox cb = new CheckBox();
                    if(Screen1Controller.getGraph().hasEdge(r-1,c-1))
                        cb.setSelected(true);
                    GridPane.setConstraints(cb,c,r);
                    cb.setAlignment(Pos.CENTER);
                    gridPane.getChildren().add(cb);
                }
            }
        }
    }

    @FXML
    private void createPressed() throws Exception {
        for(Node child : gridPane.getChildren()){
            if((child instanceof CheckBox)&&!((CheckBox) child).isSelected()){
                //System.out.println(child);
                int base = GridPane.getRowIndex(child)-1; //base
                int other = GridPane.getColumnIndex(child)-1; //other
                //System.out.println("base "+base);
                //System.out.println("other "+other);
                Screen1Controller.getGraph().removeEdge(base,other);

            }
        }
        System.out.println("Updated Graph: ");
        Screen1Controller.getGraph().displayGraph();
        application.enterScreen3();

    }

    @FXML
    private void restartPressed() throws Exception {
        application.enterScreen1();
    }

    @FXML private void deselectAllPressed(){
        for(Node child : gridPane.getChildren()){
            if((child instanceof CheckBox)&&((CheckBox) child).isSelected())
                ((CheckBox) child).setSelected(false);
        }
    }
    @FXML private void selectAllPressed(){
        for(Node child : gridPane.getChildren()){
            if((child instanceof CheckBox)&&!((CheckBox) child).isSelected())
                ((CheckBox) child).setSelected(true);
        }
    }

    public void setApp(Main application){
        this.application = application;
    }
}
