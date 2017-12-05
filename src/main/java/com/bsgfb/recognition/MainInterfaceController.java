package com.bsgfb.recognition;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main interface of the application
 *
 * @author GOXR3PLUS
 */
public class MainInterfaceController extends BorderPane {

    @FXML
    private Button start;

    @FXML
    private Button pause;

    @FXML
    private Button resume;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea infoArea;

    @FXML
    private TextField expression;

    @FXML
    private TextField answer;

    @FXML
    private Label resultStatus;

    private Random random = new Random();

    // -----------------------------------------

    private SpeechRecognizer speechRecognition = new SpeechRecognizer();

    /**
     * Constructor
     */
    public MainInterfaceController() {

        // FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainInterfaceController.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, " FXML can't be loaded!", ex);
        }

    }

    private void selectNextExpression() {
        int i = random.nextInt(expressions.size());
        String key = (String) expressions.keySet().toArray()[i];
        expression.setText(key);
    }

    private void startR() {
        statusLabel.setText("Status : [Running]");
        selectNextExpression();
        speechRecognition.startSpeechRecognition();
        resultStatus.setText("");
        answer.setText("");
    }

    private void pauseR() {
        statusLabel.setText("Status : [Paused]");
        speechRecognition.ignoreSpeechRecognitionResults();
    }

    private void resumeR() {
        statusLabel.setText("Status : [Running]");
        selectNextExpression();
        speechRecognition.stopIgnoreSpeechRecognitionResults();
        answer.clear();
        resultStatus.setText("");
    }

    /**
     * Called as soon as .fxml is initialised
     */
    @FXML
    private void initialize() {
        // start
        start.disableProperty().bind(speechRecognition.speechRecognizerThreadRunningProperty());
        start.setOnAction(a -> {
            startR();
        });

        // stop
        pause.disableProperty().bind(speechRecognition.ignoreSpeechRecognitionResultsProperty().or(start.disabledProperty().not()));
        pause.setOnAction(a -> {
            pauseR();
        });

        // restart
        resume.disableProperty().bind(speechRecognition.ignoreSpeechRecognitionResultsProperty().not());
        resume.setOnAction(a -> {
            resumeR();
        });

        //Bind the SpeechRecognitionText to InfoArea
/*
        infoArea.textProperty().bind(Bindings.createStringBinding(
                () -> speechRecognition.getSpeechRecognitionResultProperty().get() + " \n " + infoArea.getText(),
                speechRecognition.getSpeechRecognitionResultProperty())
        );
        */

        answer.textProperty().bind(Bindings.createStringBinding(() -> {
            String value = expressions.get(expression.getText());
            String text = speechRecognition.getSpeechRecognitionResultProperty().get();
            System.out.println("Value: [" + value + "] Text: [" + text + "]");

            // infoArea.setText(text + "\n" + infoArea.getText());

            if (text != null && !Objects.equals(text, "") && text.contains(value)) {
                System.out.println("Result: [" + value + "]");
                pauseR();
                resultStatus.setText("That's right!");
                return value;
            }

            System.out.println("Result: [empty]");
            return "";
        }, speechRecognition.getSpeechRecognitionResultProperty()));

//        infoArea.textProperty().bind(Bindings.createStringBinding(
//                () -> speechRecognition.getSpeechRecognitionResultProperty().get() + " \n " + infoArea.getText())
//        );
    }

    private static Map<String, String> expressions = createExpressions();

    private static Map<String, String> createExpressions() {
        String[] numbers = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty"};

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < numbers.length; i++) {
            String str = 0 + " + " + i;
            System.out.println("[" + str + "] result [" + numbers[i] + "]");
            map.put(str, numbers[i]);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String str = i + " + " + j;
                System.out.println("[" + str + "] result [" + numbers[i + j] + "]");
                map.put(str, numbers[i + j]);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                String str = i + " * " + j;
                System.out.println("[" + str + "] result [" + numbers[i * j] + "]");
                map.put(str, numbers[i * j]);
            }
        }

        return map;
    }
}
