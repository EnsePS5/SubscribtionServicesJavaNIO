package tpo4_gk_s23161.tpo4_gk_s23161;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminAppController implements Initializable {

    @FXML
    private TextField textField;
    @FXML
    private ChoiceBox<String> choiceCurrentServicesBox;


    private ClientHandler admin;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        admin = ClientHandler.start();
        String[] data = admin.sendMessage("INIT:").split(":");

        if (!data[0].equals("NONE"))
            choiceCurrentServicesBox.getItems().addAll(data);

    }



    public void notifyClients(){

        if (textField.getText().isEmpty()){
            textField.setText("Please type in notification text before sending");
            return;
        }else if (choiceCurrentServicesBox.getSelectionModel().isEmpty()){
            textField.setText("Choose service to notify in choiceBox");
            return;
        }

        admin.sendMessage("NOTIFY:" +
                choiceCurrentServicesBox.getSelectionModel().getSelectedItem() + ":" + textField.getText());


        textField.setText("Notified!");

    }

    public void addNewService(){

        if (textField.getText().isEmpty()){
            textField.setText("Please type in service name you want to add");
            return;
        }

        if (admin.sendMessage("ADD:" + textField.getText()).equals("REJECTED"))
            textField.setText("Current service already exists!");

        OnHover();
    }

    public void removeService(){

        if (choiceCurrentServicesBox.getSelectionModel().isEmpty()){
            textField.setText("Choose service to remove in choiceBox!");
            return;
        }

        admin.sendMessage("REMOVE:" + choiceCurrentServicesBox.getSelectionModel().getSelectedItem());
        OnHover();
    }

    public void OnHover(){
        String[] data = admin.sendMessage("UPDATE").split(":");

        if (choiceCurrentServicesBox.getItems().size() == data.length || data[0].equals("NONE"))
            return;

        choiceCurrentServicesBox.getItems().clear();
        choiceCurrentServicesBox.getItems().addAll(data);
    }

}
