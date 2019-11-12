package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.util.Duration;
import model.Food;
import model.Pigeon;
import model.Simulation;

public class mainViewController extends Application implements Initializable {
    @FXML
    public AnchorPane mainPane_AnchorPane;  // Root's object
    @FXML
    public Button launch_BTN;               // Launching button
    @FXML
    public ImageView launchButton_IV;
    @FXML
    public TextField foodTimeout_TF;
    @FXML
    public TextField nbPigeons_TF;
    @FXML
    public Pane mainPane_PNL;
    @FXML
    public TextField itemSize_TF;

    private String urlImgPlay = "file:resources/icons/play.png";
    private String urlImgStop = "file:resources/icons/stop.png";
    private Simulation simulation = null;
    private boolean simulationLaunched = false;
    private int nbFrames = 0;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialise le controller avec son fichier FXML de configuration.
     * Initialise également le nombre de fois que la mainLoop (thread répété dans la fenêtre principale) est exécutée.
     * (Ici, elle est infinie ; Jusqu'à fermeture de l'application)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainLoop.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Processus démarré au lancement de la fenêtre principal.
     * Prépare le chargement du fichier FXML et configure la fenêtre principale.
     * @param primaryStage  Le Stage JavaFX de la fenêtre principale
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Preparing the loading
        FXMLLoader loader = new FXMLLoader();
        String fxmlDocPath = "src/main/java/view/mainView.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        // Root's Loading
        mainPane_AnchorPane = loader.load(fxmlStream);

        // Showing
        Scene scene = new Scene(mainPane_AnchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pigeon Simulator");
        primaryStage.show();
    }

    /**
     * Fonction principale exécutée en continue lors du lancement de l'application, gère la fenêtre avec l'affichage du
     * plateau de jeu.
     */
    private Timeline mainLoop = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            // Actualise la liste de nourriture pour les pigeons
            simulation.refreshFood();
            // Probabilité d'effrayer les pigeons (20% toutes les 50 frames)
            if(nbFrames%50 == 49) {
                System.out.println("Maybe a bark...");
                simulation.dogBarking(false);
            }
            // Récupère la position des Pigeons + Rafraichit l'affichage du plateau
            refreshScreen();
            // Incrémentation du nombre de frames
            nbFrames++;
        }
    }));

    /**
     * Fonction permettant de nettoyer le plateau et d'afficher la nouvelle position des pions. (Pigeons et nourriture)
     */
    public void refreshScreen() {
        mainPane_PNL.getChildren().clear();
        for(Pigeon pigeon:simulation.getPigeonList()) {
            mainPane_PNL.getChildren().add(pigeon.getImageView());
        }
        for(Food food:simulation.getFoodList()) {
            mainPane_PNL.getChildren().add(food.getImageView());
        }
    }

    /**
     * Fonction handler du bouton de la fenêtre permettant de lancer la simulation.
     * @param actionEvent   Lorsque l'utilisateur clique sur le bouton lié
     */
    public void launchSimulation(ActionEvent actionEvent) {
        System.out.println("Simulation launched");
        if(!simulationLaunched) {
            // LANCEMENT DE LA SIMULATION
            launchSimulation();
            launchButton_IV.setImage(new Image(urlImgStop));
            launch_BTN.setText("Arrêter la simulation");
            simulationLaunched = true;
        } else {
            // ARRET DE LA SIMULATION
            stopSimulation();
            launchButton_IV.setImage(new Image(urlImgPlay));
            launch_BTN.setText("Démarrer la simulation");
            simulationLaunched = false;
        }
    }

    /**
     * Fonction démarrant la simulation
     */
    public void launchSimulation() {
        // Récupération de la taille des éléments en pixels
        int itemSize;
        try {
            itemSize = Integer.parseInt(itemSize_TF.getText());
        } catch(NumberFormatException e) {
            throw new NumberFormatException("Taille des éléments incorrecte.");
        }

        // Récupération du nombre de pigeons à afficher
        int nbPigeons = 0;
        try {
            nbPigeons = Integer.parseInt(nbPigeons_TF.getText());
        } catch(NumberFormatException e) {
            throw new NumberFormatException("Nombre de pigeons incorrect.");
        }

        // Récupération de temps de péremption de la nourriture
        long outdatingTime = 0;
        try {
            outdatingTime = Long.parseLong(foodTimeout_TF.getText());
        } catch(NumberFormatException e) {
            throw new NumberFormatException("Temps de péremption de la nourriture incorrect.");
        }

        // Création de la simulation
        simulation = new Simulation(mainPane_PNL.getWidth(), mainPane_PNL.getHeight(), itemSize, nbPigeons, 5, outdatingTime);

        // Bloquer l'interface
        itemSize_TF.setDisable(true);
        nbPigeons_TF.setDisable(true);
        foodTimeout_TF.setDisable(true);

        //Launch simulation frames
        mainLoop.play();
    }

    /**
     * Fonction permettant à l'utilisateur de placer de la nourriture sur le sol
     */
    public void dropPizza(MouseEvent event) {
        if(simulationLaunched) {
            simulation.addFood(event.getX(), event.getY());
        }
    }

    public void manualBark() {
        if(simulationLaunched) {
            simulation.dogBarking(true);
        }
    }

    /**
     * Fonction stoppant la simulation.
     */
    public void stopSimulation() {
        simulation.shutDownApp();
        mainPane_PNL.getChildren().clear();

        // Débloquer l'interface
        itemSize_TF.setDisable(false);
        nbPigeons_TF.setDisable(false);
        foodTimeout_TF.setDisable(false);

        //Stop simulation frames
        mainLoop.stop();
    }

}
