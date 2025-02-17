package com.np.brickbreaker;

public class StoreItem {
    private String id;
    private String name;
    private String description;
    private int cost;
    private String image;
    public boolean purchased;

    public StoreItem(String id, String name, String description, int cost, String image,boolean purchased) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.image = image;
        this.purchased=purchased;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }

    public String getImage() {
        return image;
    }
}
