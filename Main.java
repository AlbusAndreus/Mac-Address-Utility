package sample;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Main extends Application {
    List<NetworkInterface>  networkInterfaces = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("MAC Address Utility");
        GridPane gp = new GridPane();

        Label address = new Label("Address");
        GridPane.setConstraints(address, 0,0);

        TextField Address = new TextField();
        GridPane.setConstraints(Address, 0,1);

        Label selectHardware = new Label("Select Hardware");
        GridPane.setConstraints(selectHardware, 1,0);

        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();

        while(networkInterfaceEnumeration.hasMoreElements()){
            NetworkInterface iface = networkInterfaceEnumeration.nextElement();


            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            String name = iface.getName();

            while(addresses.hasMoreElements()){
                InetAddress addr = addresses.nextElement();
                if(!addr.isLoopbackAddress()){
                    networkInterfaces.add(iface);
                    //netintCount++;
                    //System.out.println(name + addr);
                }

            }
        }
        FilteredList<NetworkInterface> filteredList = new FilteredList<NetworkInterface>(FXCollections.observableList(networkInterfaces));


        ComboBox SelectHardware = new ComboBox(filteredList);
        GridPane.setConstraints(SelectHardware, 1,1);

        Button getAddress = new Button("Get Address");
        GridPane.setConstraints(getAddress, 2,2);
        getAddress.setOnAction(event->{
            int index = SelectHardware.getSelectionModel().getSelectedIndex();
            NetworkInterface e = networkInterfaces.get(index);

            InetAddress ip = e.getInetAddresses().nextElement();
            String Mac = getMacAddress(ip);
            Address.setText(Mac);
        });

        gp.getChildren().addAll(Address, address, getAddress, SelectHardware, selectHardware);
        primaryStage.setScene(new Scene(gp, 300, 275));
        primaryStage.show();
    }
    private static String getMacAddress(InetAddress ip) {
        String address = null;
        try {

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            address = sb.toString();

        } catch (SocketException ex) {

            ex.printStackTrace();

        }

        return address;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
