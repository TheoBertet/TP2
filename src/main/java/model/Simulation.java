package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulation {
    /* --- Environment --- */
    private List<Food> foodList;
    private List<Pigeon> pigeonList;
    private ExecutorService executorService;
    private double width;
    private double height;
    private int itemSize;
    private long outdatingTime;
    private String urlFood = "file:resources/icons/food.png";

    /* --- Constructors --- */
    public Simulation(double w, double h, int itemSize, int nbThreads, int nbFood, long outdatingTime) {
        foodList = new ArrayList<Food>();
        pigeonList = new ArrayList<Pigeon>();
        executorService = Executors.newFixedThreadPool(nbThreads);
        width = w;
        height = h;
        this.itemSize = itemSize;
        this.outdatingTime = outdatingTime;

        for(int index = 0; index < nbThreads; index++) {
            addPigeon();
        }

        for(int index = 0; index < nbFood; index++) {
            addFood();
        }
    }

    /* --- Methods --- */
    /**
     * Fonction addFood
     * Posée aléatoirement par l'application
     */
    public void addFood() {
        Food food = new Food(Math.random()*(width-itemSize), Math.random()*(height-itemSize), false, itemSize, outdatingTime);
        addFood(food);
    }

    /**
     * Fonction addFood
     * Posée manuellement par l'utilisateur
     * @param posX  Position sur l'axe X de la nourriture à poser
     * @param posY  Position sur l'axe Y de la nourriture à poser
     */
    public void addFood(double posX, double posY) {
        Food food = new Food(posX, posY, false, itemSize, outdatingTime);
        addFood(food);
    }

    /**
     * Fonction addFood
     * @param food  Nourriture à ajouter à la liste des nourritures de la simulation.
     * @throws IllegalArgumentException     Si la nourriture est déjà présente dans la simulation. (Évite les doublons)
     */
    public void addFood(Food food) throws IllegalArgumentException {
        if(foodList.indexOf(food) < 0) {
            foodList.add(food);
        } else {
            throw new IllegalArgumentException("La nourriture ajoutée existe déjà dans cette simulation.");
        }
    }

    /**
     * Fonction removeFood
     * @param food  Nourriture à retirer de la liste des nourritures de la simulation.
     *              Ne provoque pas d'erreur si la nourriture n'existe pas.
     */
    public void removeFood(Food food) {
        foodList.remove(food);
    }

    /**
     * Fonction addPigeon
     */
    public void addPigeon() {
        Pigeon pigeon = new Pigeon(Math.random()*(width-itemSize), Math.random()*(height-itemSize), itemSize);
        addPigeon(pigeon);
        executorService.execute(pigeonList.get(pigeonList.size()-1));
    }

    /**
     * Fonction addPigeon
     * @param pigeon Pigeon à ajouter à la liste des pigeons de la simulation.
     * @throws IllegalArgumentException     Si le pigeon est déjà présent dans la simulation. (Évite les doublons)
     */
    public void addPigeon(Pigeon pigeon) {
        if(pigeonList.indexOf(pigeon) < 0) {
            pigeonList.add(pigeon);
        } else {
            throw new IllegalArgumentException("Le pigeon ajouté existe déjà dans cette simulation.");
        }
    }

    /**
     * Fonction removePigeon
     * @param pigeon  Pigeon à retirer de la liste des pigeons de la simulation.
     *                Ne provoque pas d'erreur si le pigeon n'existe pas.
     */
    public void removePigeon(Pigeon pigeon) {
        pigeonList.remove(pigeon);
    }


    public void refreshFood() {
        boolean refreshNeeded = false;

        for(Food food : foodList) {
            food.outdate(System.currentTimeMillis());
            if(food.isOutdated()) {
                refreshNeeded = true;
            }
        }

        for(Pigeon pigeon : pigeonList) {
            // Si la liste de nourriture des pigeons n'est pas initialisée, alors on l'initialise
            if(pigeon.getFoodList() == null) {
                refreshNeeded = true;
                break;
            }

            // Si la liste de la simulation et la liste des pigeons est différente, on actualise
            if (!compareFoodLists(pigeon.getFoodList())) {
                refreshNeeded = true;
            }
        }

        if(refreshNeeded) {
            for(Pigeon pigeon : pigeonList) {
                pigeon.setFoodList(foodList);
            }
        }
    }

    /**
     *  Fonction permettant de comparer la liste de nourriture de la simulation et la liste de nourriture des pigeons
     * @param foodListToCompare
     * @return  True si les deux listes sont similaires ; False sinon
     */
    public boolean compareFoodLists(List<Food> foodListToCompare) {
        if(foodListToCompare.size() != foodList.size())
            return false;
        return true;
    }

    public void dogBarking(boolean forced) {
        double probability = Math.random();
        if(probability > 0.9 || forced) {
            System.out.println("Dog is barking. WafWafWaf ! (Prob.:"+probability+")");
            // 20% de probabilité
            for(Pigeon pigeon : pigeonList) {
                pigeon.affraid();
            }
        }
    }

    /**
     * Fonction shutDownApp
     */
    public void shutDownApp() {
        for(Pigeon pigeon : pigeonList) {
            pigeon.stop();
        }
        executorService.shutdownNow();
        pigeonList.clear();
        foodList.clear();
    }

    /* --- Getters --- */
    public List<Food> getFoodList() {
        return foodList;
    }

    public List<Pigeon> getPigeonList() {
        return pigeonList;
    }
}
