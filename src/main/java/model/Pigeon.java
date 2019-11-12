package model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Pigeon implements Runnable {
    private static int nbPigeons = 0;
    private int nbPigeon;

    private double posX;
    private double posY;
    private int size;
    private String urlPigeon = "file:resources/icons/bird.png";
    private String urlPigeonAffraid = "file:resources/icons/birdAffraid.png";
    private ImageView imageView;

    private double distanceView = 300;
    private double distanceEat = 10;

    private boolean isAffraid = false;
    private double xToRun = 0;
    private double yToRun = 0;

    private List<Food> foodList = null;


    public Pigeon(double x, double y, int size) {
        // Initialisation des variables
        posX = x;
        posY = y;
        this.size = size;

        // Création de l'icone
        imageView = new ImageView(new Image(urlPigeon));
        imageView.setX(posX);
        imageView.setY(posY);
        imageView.setFitHeight(this.size);
        imageView.setFitWidth(this.size);

        nbPigeon = nbPigeons;
        nbPigeons++;

        // Configuration du thread
        pigeonLoop.setCycleCount(Timeline.INDEFINITE);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public int getSize() { return size; }

    public ImageView getImageView() { return imageView; }

    @Override
    public void run() {
        pigeonLoop.play();
    }

    public void stop() {
        pigeonLoop.stop();
    }

    private Timeline pigeonLoop = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            lookForFood();
        }
    }));

    public void lookForFood() {
        double distanceWithFood = -1;
        double foodPosX = 0;
        double foodPosY = 0;
        int foodTargetted = -1;

        if(isAffraid) {
            System.out.println("Pigeon #"+nbPigeon+" is affraid ! (dist:" + processDistance(xToRun, yToRun) +", "+ (processDistance(xToRun, yToRun)>10) +")");
            if(processDistance(xToRun, yToRun) > 10.0) {
                moveTo(xToRun, yToRun);
                refreshImage();
            } else {
                System.out.println("Pigeon #"+nbPigeon+" is no longer affraid.");
                isAffraid = false;
                imageView.setImage(new Image(urlPigeon));
            }
        } else {
            if (foodList != null) {
                for (Food food : foodList) {
                    if (!food.isOutdated()) {
                        double distance = processDistance(food.getPosX(), food.getPosY());
                        if (distance < distanceView && (distance < distanceWithFood || distanceWithFood == -1)) {
                            distanceWithFood = distance;
                            foodPosX = food.getPosX();
                            foodPosY = food.getPosY();
                            foodTargetted = foodList.indexOf(food);
                        }
                    }
                }
            }

            if (distanceWithFood != -1) {
                if (distanceWithFood < distanceEat) {
                    eatFood(foodTargetted);
                } else {
                    moveTo(foodPosX, foodPosY);
//                System.out.println("Best distance for Pigeon#"+nbPigeon+" ("+posX+","+posY+") is "+distanceWithFood);
                }
                refreshImage();
            }
        }
    }

    public double processDistance(double xToGo, double yToGo) {
        return Math.sqrt(Math.pow((xToGo - posX), 2.0) + Math.pow((yToGo - posY), 2.0));
    }

    /**
     *      x   <---- Pigeon
     *      |\
     *      | \
     *      |  \
     *      +---x  <-- Nourriture
     *
     * On cherche à déplacer le pigeon vers la nourriture.
     *
     * @param foodPosX
     * @param foodPosY
     */
    public void moveTo(double foodPosX, double foodPosY) {
        double deltaX = foodPosX - posX;
        double deltaY = foodPosY - posY;
        double valMax = Math.abs(deltaX)>Math.abs(deltaY) ? Math.abs(deltaX) : Math.abs(deltaY);

        posX += deltaX/valMax;
        posY += deltaY/valMax;
    }

    /**
     * Fonction eatFood
     * Lorsqu'un pigeon atteint de la nourriture respectant sa distance limite pour manger, il supprime l'objet et grossit un peu
     * @param foodToEat Index de la nourriture à retirer de la liste
     */
    public synchronized void eatFood(int foodToEat) {
        foodList.remove(foodToEat);
        System.out.println("Pigeon #"+nbPigeon+" is eating. Nomnomnomnom");
        size++;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void refreshImage() {
        imageView.setX(posX);
        imageView.setY(posY);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
    }

    public void affraid() {
        isAffraid = true;
        imageView.setImage(new Image(urlPigeonAffraid));
        double xSign = 1;
        double ySign = 1;
        if(Math.random() > 0.5) { xSign = -1; }
        if(Math.random() > 0.5) { ySign = -1; }
        xToRun = posX+Math.random()*60*xSign;
        yToRun = posY+Math.random()*60*ySign;
    }
}
