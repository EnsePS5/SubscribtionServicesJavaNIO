package tpo4_gk_s23161.tpo4_gk_s23161;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("AppController.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Services - Client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    @Override
    public void stop() throws IOException {
        ClientHandler.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}