package tpo4_gk_s23161.tpo4_gk_s23161;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Label notificationLabel;

    private ClientHandler client;
    private ArrayList<String> subscribedServices;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        client = ClientHandler.start();
        String[] data = client.sendMessage("INIT:").split(":");

        if (!data[0].equals("NONE"))
            choiceBox.getItems().addAll(data);

        choiceBox.setOnAction(this::OnChoose);
        subscribedServices = new ArrayList<>();

    }



    public void Subscribe(){

        if (choiceBox.getSelectionModel().isEmpty()){
            notificationLabel.setText("Choose service!");
            return;
        }

        subscribedServices.add(choiceBox.getSelectionModel().getSelectedItem());

        String response = client.sendMessage("GET_NOTIFICATION:" + choiceBox.getSelectionModel().getSelectedItem());
        if (response.equals("NONE"))
            notificationLabel.setText("Subscribed!");
        else
            notificationLabel.setText(response);
    }

    public void Unsubscribe(){

        if (choiceBox.getSelectionModel().isEmpty()){
            notificationLabel.setText("Choose service!");
            return;
        }

        subscribedServices.remove(choiceBox.getSelectionModel().getSelectedItem());
        notificationLabel.setText("Unsubscribed!");
    }

    public void OnHover(){

        String[] data;
        if (!choiceBox.getSelectionModel().isEmpty() && subscribedServices.contains(choiceBox.getSelectionModel().getSelectedItem())) {
            data = client.sendMessage("UPDATE:" + choiceBox.getSelectionModel().getSelectedItem()).split(":");

            if (choiceBox.getItems().size() == data.length || data[0].equals("NONE"))
                return;


            for (int i = 0; i < data.length-1; i++) {
                if (!choiceBox.getItems().contains(data[i]))
                    choiceBox.getItems().add(data[i]);
            }

            notificationLabel.setText(data[data.length-1]);
        }
        else {
            data = client.sendMessage("UPDATE").split(":");

            if (choiceBox.getItems().size() == data.length || data[0].equals("NONE"))
                return;

            choiceBox.getItems().clear();
            choiceBox.getItems().addAll(data);
        }

    }

    public void OnChoose(ActionEvent event){

        if (!subscribedServices.contains(choiceBox.getSelectionModel().getSelectedItem())) {
            notificationLabel.setText("Not Subscribed");
            return;
        }

        notificationLabel.setText(client.sendMessage("GET_NOTIFICATION:" + choiceBox.getSelectionModel().getSelectedItem()));
    }
}
