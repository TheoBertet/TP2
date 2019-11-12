package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static java.lang.System.currentTimeMillis;

public class Food {
    private double posX;
    private double posY;
    private int size;
    private String urlFood = "file:resources/icons/food.png";
    private String urlRottenFood = "file:resources/icons/rottenFood.png";
    private ImageView imageView;
    private long creationTime = System.currentTimeMillis();
    private long outdatingTime;

    private boolean outdated;

    public Food(double x, double y, boolean outdated, int size, long outdatingTime) {
        posX = x;
        posY = y;
        this.size = size;
        this.outdated = outdated;
        this.outdatingTime = outdatingTime;

        // Création de l'icone
        imageView = new ImageView(new Image(urlFood));
        imageView.setX(posX);
        imageView.setY(posY);
        imageView.setFitHeight(this.size);
        imageView.setFitWidth(this.size);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public ImageView getImageView() { return imageView; }

    public boolean isOutdated() {
        return outdated;
    }

    public void outdate(long currentTime) {
        if(currentTime - creationTime > (outdatingTime*1000)) {
            imageView.setImage(new Image(urlRottenFood));
            outdated = true;
        }
    }
}
