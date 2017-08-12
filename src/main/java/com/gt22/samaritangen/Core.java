package com.gt22.samaritangen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Core extends Application {
	private static Controller controller;
	public static void main(String[] args) {
		Application.launch(Core.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(Core.class.getResource("/GUI.fxml"));
		Parent root = loader.load();
		controller = loader.getController();
		stage.setScene(new Scene(root));
		stage.setTitle("Samaritan image generator by gt22");
		stage.show();
	}

	public static Controller getController() {
		return controller;
	}
}
