package View;

import Controller.Utils.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Lobby extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String newPath = Pathto("/FXML/Lobby.fxml");
        URL newUrl = new URL("file:"+newPath);

        Parent root = FXMLLoader.load(newUrl);
        Scene scene = new Scene(root);

        primaryStage.setTitle("show me a Point");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private static String Pathto(String File){
        URL url = Util.class.getResource("");
        String path = url.getPath();
        String newPath = path.substring(0,path.lastIndexOf('/'));
        newPath = newPath.substring(0,newPath.lastIndexOf('/'));
        newPath = newPath.substring(0,newPath.lastIndexOf('/'));
        newPath += File;
        return newPath;
    }


    public void onStart(String... args){
        Application.launch(args);
    }
}
