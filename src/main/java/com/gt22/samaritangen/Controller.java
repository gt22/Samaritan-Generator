package com.gt22.samaritangen;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.jooq.lambda.Unchecked;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
	private static Timeline gifTimeline = new Timeline(700);
	private static Image[] animationFrames;
	public VBox root;
	public ImageView samaritan;
	public TextField message;
	public CheckBox gif;
	public CheckBox type;
	public CheckBox offset;
	public Button save;
	public ComboBox<SamaritanGenerator.SamaritanColor> colors;
	public TextField filename;
	public Label res;
	public TextField delay;
	//Used by 'type' to remember previous state of 'gif'
	private boolean gifWasSelected = false;

	private ChangeListener<Boolean> createCompetingListener(CheckBox competing) {
		return (v, oldVal, newVal) -> {
			if (newVal) {
				if(!gif.isDisable()) {
					gifWasSelected = gif.isSelected(); //If 'type' selected record current state of gif for future resetting
				}
				competing.setSelected(false);
			}
			gif.setSelected(newVal || gifWasSelected); //If 'type' selected force gif selecting, when unselected return to previous state
			if(!competing.isSelected()) {
				gif.setDisable(newVal);
			}
		};
	}

	@FXML
	public void initialize() {
		addListeners();
		
		gifTimeline.cycleCountProperty().setValue(Animation.INDEFINITE);
		
		colors.setItems(FXCollections.observableArrayList(SamaritanGenerator.SamaritanColor.values()));
		colors.setValue(SamaritanGenerator.SamaritanColor.BLACK);
		
		samaritan.setFitWidth(800);
		samaritan.setFitHeight(450);
		samaritan.setImage(SwingFXUtils.toFXImage(SamaritanGenerator.createSamaritanImage("   ", colors.getValue()), null));
	}
	
	private void addListeners() {
		type.selectedProperty().addListener(createCompetingListener(offset));
		offset.selectedProperty().addListener(createCompetingListener(type));
		gif.selectedProperty().addListener((v, oldVal, newVal) -> delay.setDisable(!newVal));
		save.setOnMouseClicked(e -> {
			String path = filename.getText();
			if (!path.isEmpty()) {
				String ext = gif.isSelected() ? ".gif" : ".png";
				if (!path.endsWith(ext)) {
					path += ext;
				}
				File out = new File(path);
				try {
					if (!out.exists()) {
						//noinspection ResultOfMethodCallIgnored
						out.createNewFile();
					}
					if (animationFrames != null) {
						BufferedImage[] images = MiscUtils.ArrayUtils.map(animationFrames, i -> SwingFXUtils.fromFXImage(i, null), BufferedImage[]::new);
						GifSequenceWriter writer = new GifSequenceWriter(new FileImageOutputStream(out), images[0].getType(), delay.getText().isEmpty() ? 0 : Integer.parseInt(delay.getText()), true);
						MiscUtils.ArrayUtils.forEach(images, Unchecked.consumer(writer::writeToSequence));
						writer.writeToSequence(SamaritanGenerator.createSamaritanImage("   ", colors.getValue(), false));
						writer.close();
					} else {
						ImageIO.write(SwingFXUtils.fromFXImage(samaritan.getImage(), null), "PNG", out);
					}
					res.setStyle("-fx-color: green");
					res.setText("Saved to " + out.getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
					res.setStyle("-fx-color: red");
					res.setText("Something went wrong: " + e1.getLocalizedMessage());
				}
			} else {
				res.setStyle("-fx-color: red");
				res.setText("Filename not specified");
			}
		});
		
		delay.textProperty().addListener((v, oldVal, newVal) -> {
			if(!newVal.matches("^\\d.$")) {
				delay.setText(newVal.replaceAll("\\D", ""));
			}
		});

		ChangeListener<Object> updater = (v, oldVal, newVal) -> updateImage();
		message.textProperty().addListener(updater);
		colors.valueProperty().addListener(updater);
		gif.selectedProperty().addListener(updater);
		type.selectedProperty().addListener(updater);
		offset.selectedProperty().addListener(updater);
		delay.textProperty().addListener(updater);
	}

	private void updateImage() {
		SamaritanGenerator.SamaritanColor color = colors.getValue();
		String msg = message.getText().trim().toUpperCase();
		animationFrames = null;
		gifTimeline.stop();
		if (gif.isSelected()) {
			updateGif(msg.isEmpty() ? new Image[]{} : type.isSelected() ? SamaritanGenerator.generateSamaritanTypingMessage(msg, color) : SamaritanGenerator.generateSamaritanGif(msg, color, offset.isSelected()));
		} else {
			samaritan.setImage(SwingFXUtils.toFXImage(SamaritanGenerator.createSamaritanImage(msg, color), null));
		}
	}

	private void updateGif(Image[] frames) {
		animationFrames = frames;
		ObservableList<KeyFrame> tlFrames = gifTimeline.getKeyFrames();
		tlFrames.clear();
		int delay = this.delay.getText().isEmpty() ? 0 : Integer.parseInt(this.delay.getText());
		MiscUtils.ArrayUtils.forEach(frames, (f, i) -> tlFrames.add(new KeyFrame(Duration.millis(i * delay), e -> samaritan.setImage(f))));
		tlFrames.add(new KeyFrame(Duration.millis(frames.length * delay), e -> samaritan.setImage(SwingFXUtils.toFXImage(SamaritanGenerator.createSamaritanImage("   ", colors.getValue(), false), null))));
		tlFrames.add(new KeyFrame(Duration.millis((frames.length + 1) * delay), e -> {
			//Empty frame to allow previous to show up before restarting animation
		}));
		gifTimeline.playFromStart();
	}

}
