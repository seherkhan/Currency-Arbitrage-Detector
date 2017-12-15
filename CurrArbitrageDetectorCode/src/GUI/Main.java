package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.InputStream;


public class Main extends Application {

    private Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage=primaryStage;
        enterScreen1();

        primaryStage.show();

    }
    public void enterScreen1() throws Exception{
        //System.out.println("enterScreen1");
        Screen1Controller screen1 = (Screen1Controller) replaceSceneContent("Screen1.fxml");
        screen1.setApp(this);
    }

    public void enterScreen2() throws Exception{
        stage.setX(100);
        stage.setY(50);
        //System.out.println("enterScreen3");
        Screen2Controller screen2 = (Screen2Controller) replaceSceneContent("Screen2.fxml");
        screen2.setApp(this);
    }

    public void enterScreen3() throws Exception{
        //System.out.println("enterScreen3");
        Screen3Controller screen3 = (Screen3Controller) replaceSceneContent("Screen3.fxml");
        screen3.setApp(this);
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource(fxml));
        Pane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    public Stage getStage(){
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
