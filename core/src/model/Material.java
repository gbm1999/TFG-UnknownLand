package model;

import java.util.HashMap;

public enum Material {

    GRASS(1, true, "Grass", 0),
    DIRT(2, true, "Dirt", 0),
    SKY(3, true, "Sand", 0),
    LAVA(4, true, "Lava", 0),
    CLOUD(5, true, "Cloud", 0),
    STONE(6, true, "Stone", 0),
    SAND(7, true, "Sand", 0),
    COPPER(8, true, "Copper", 0),
    IRON(9, true, "Iron", 0),
    OBSIDIAN(10, true, "Obsidian", 0),
    DIAMOND(11, true, "Diamond", 0),
    COAL(12, true, "Coal", 0),
    Granite(13, true, "Granite", 0);

    public static final int SIZE = 16;

    private int id;
    private boolean collidable;
    private String symbol;
    private double value;

    Material(int id, boolean collidable, String symbol, double value){
        this.id = id;
        this.collidable = collidable;
        this.symbol = symbol;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getValue() {
        return value;
    }

    public boolean isBlock(){
        return (this.id <= 5);
    }

    public boolean isWeapon(){
        return (this.id > 5 && this.id <= 8);
    }

    public boolean isArmor(){
        return (this.id > 8 && this.id <= 13);
    }

    public boolean isEdible(){
        return (this.id > 13 && this.id <= 18);
    }
    public boolean isTool(){
        return (this.id > 18);
    }
    private static HashMap<Integer, Material> tileMap;

    static {
        tileMap = new HashMap<Integer, Material>();
        for (Material material : Material.values()){
            tileMap.put(material.getId(), material);
        }
    }

    public static Material getMaterialById (int id){
        return tileMap.get(id);
    }
}
