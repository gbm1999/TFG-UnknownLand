package model;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import org.bukkit.util.noise.CombinedNoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import persistence.WorldMapProvider;
import view.LevelRenderer;

public class World  implements Serializable {

    private String id;
    public int[][][] map;
    Player player;
    LevelRenderer lv;
    private TextureRegion[][] tiles;
    private ArrayList<Enemy> creatures;
    private Map<Coord, ItemStack> items;
    private int width;
    private int height;
    private float timeElapsed = 0;
    long range = 1234567L;
    Random r = new Random();

    private final long seed = (long)(r.nextDouble()*range);
    private int score;

    public World(String id, int SIZE)
    {
        tiles = TextureRegion.split(new Texture("tiles.png"), Material.SIZE, Material.SIZE);
        creatures = new ArrayList<>();
        player = new Player(1000, 1000);
        lv = new LevelRenderer(player);
        width = SIZE * 8;
        height = SIZE;
        this.id = id;
        map = new int[5][SIZE][SIZE * 8];
        items = new ConcurrentHashMap<>();;

        WorldMapProvider worldMapProvider = new WorldMapProvider();
        worldMapProvider= worldMapProvider.loadMap(id, SIZE, this);
        map = worldMapProvider.map;
    }


    public World generateRandomMap(String id, int SIZE) {

        map = new int[2][height][width];
        Random rng = new Random(seed);

        // Paso 1: generar nuevo mapa de alturas del terreno
        CombinedNoiseGenerator noise1 = new CombinedNoiseGenerator(this);
        CombinedNoiseGenerator noise2 = new CombinedNoiseGenerator(this);
        OctaveGenerator noise3 = new PerlinOctaveGenerator(rng, 6);
        OctaveGenerator noise = new PerlinOctaveGenerator(rng, 8);

        System.out.println("Generando superficie del mundo...");
        int count = (int) Math.floor(Math.random() * (12 - 2 + 1) + 2);
        Boolean activateSand = false;
        for (int x = 0; x < width; x++) {
            double heightLow = noise1.noise(x * 1.3, x * 1.3) / 6.0 - 4.0;
            double heightHigh = noise2.noise(x * 1.3, x * 1.3) / 5.0 + 6.0;
            double heightResult;
            if (noise3.noise(x, x, 0.5, 2) / 8.0 > 0.0)
                heightResult = heightLow;
            else
                heightResult = Math.max(heightHigh, heightLow);
            heightResult /= 2.0;
            if (heightResult < 0.0)
                heightResult = heightResult * 8.0 / 10.0;
            int heightBlock = (int) Math.floor(heightResult + 62);

            if(Math.random() > 0.95){
                activateSand = true;
            }
            if(activateSand && count > 5){
                map[1][heightBlock][x] = Material.SAND.getId();
                count--;
            }
            else{
                map[1][heightBlock][x] = Material.GRASS.getId();
                count = (int) Math.floor(Math.random() * (12 - 2 + 1) + 2);
                activateSand = false;
            }

            System.out.println("Generando entidades e items...");
            for (int z=height - 1; z>=0 && z > heightBlock ; z--) {
                double dirtThickness = noise.noise(x, z, 0.8, 2.0) / 24 - 4;
                double stoneTransition = heightBlock - dirtThickness +5;
                if (z==SIZE - 1) map[1][z][x] = Material.Granite.getId();
                else if (z > stoneTransition)
                    map[1][z][x] = Material.STONE.getId();
                else
                    map[1][z][x] = Material.DIRT.getId();
            }
            if (rng.nextDouble() < 0.2) // generamos Monster (75%) o Animal (25%) de las veces
                creatures.add(new JellyEnemy(x, getMaxLocationAtX(x), 1 ));
            else if (rng.nextDouble() > 0.7)
                creatures.add(new CactusEnemy(x, getMaxLocationAtX(x), 1 , 1, 1));
            else if (rng.nextDouble() > 0.8){
                try {
                    items.put(new Coord(x, getMaxLocationAtX(x)), new ItemStack(Material.EGG, new Random().nextInt(10)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("Generando cuevas");
        int SCALE = 20;
        int OCTAVES = 4;
        double PERSISTENCE = 0.5;
        double LACUNARITY = 2.0;
        double PROBABILITY = 0.3;
        PerlinWorm perlinWorm = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY, Material.SKY.getId(), PROBABILITY);

        int[][] grid = perlinWorm.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == Material.SKY.getId() && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 7) {
                    map[1][z][x] = 0;
                }
            }
        }
        System.out.println ("Generando lava");
        SCALE = 10;
        PROBABILITY = 0.55;
        perlinWorm = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY, Material.LAVA.getId(), PROBABILITY);

        grid = perlinWorm.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == Material.LAVA.getId() && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 3 && map[1][z][x] != 7) {
                    map[1][z][x] = Material.LAVA.getId();
                    if(Math.random() > 0.88){
                        map[1][z][x] = Material.OBSIDIAN.getId();
                    }
                }
            }
        }
        System.out.println ("Generando menas");
        SCALE = 4;
        OCTAVES = 2;
        PERSISTENCE = 0.0001;
        LACUNARITY = 0.0000001;
        PROBABILITY = 0.6;
        PerlinWorm perlinWormOres = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY, Material.IRON.getId(), PROBABILITY);

        grid = perlinWormOres.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == Material.IRON.getId() && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 3 && map[1][z][x] != 7) {
                    map[1][z][x] = Material.IRON.getId();
                }
            }
        }

        perlinWormOres = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY, Material.COPPER.getId(), PROBABILITY);

        grid = perlinWormOres.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == Material.COPPER.getId() && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 3 && map[1][z][x] != 7) {
                    map[1][z][x] = Material.COPPER.getId();
                }
            }
        }

        perlinWormOres = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY, Material.COAL.getId(), PROBABILITY);

        grid = perlinWormOres.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == Material.COAL.getId() && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 3 && map[1][z][x] != 7) {
                    map[1][z][x] = Material.COAL.getId();
                }
            }
        }

        PROBABILITY = 0.8;
        perlinWormOres = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY, Material.DIAMOND.getId(), PROBABILITY);

        grid = perlinWormOres.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == Material.DIAMOND.getId() && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 3 && map[1][z][x] != 7) {
                    map[1][z][x] = Material.DIAMOND.getId();
                }
            }
        }

        System.out.println ("Generando jugador");
        player.setX(2);
        player.setY(getMaxLocationAtX((int)player.getX()));
        for (int x = 0; x < grid.length; x++) {
            creatures.add(new JellyEnemy(x, getEmptyLocationAtX(x),  1));
        }
        creatures.add(new FirstBoss(2, getMaxLocationAtX((int)player.getX()), 1 ));
        return this;
    }
    public int getMaxLocationAtX(int x){
        int maxLocation = -1;
        for (int layer = getLayers() -1 ; layer >= 0 ; layer--) {
            for (int row = getHeight() - 1; row >= 0; row--) {
                    Material type = this.getMaterialByCoordinate(layer, x, row);
                    if (type != null && type.getId() != 0){
                        maxLocation = row + 1;
                        return maxLocation;
                    }
            }
        }
      return maxLocation;
    }

    public int getEmptyLocationAtX(int x){
        int maxLocation = getMaxLocationAtX(x) - 2;
        for (int layer = getLayers() -1 ; layer >= 0 ; layer--) {
                for (int row = maxLocation; row >= 0; row--) {
                    Material type = this.getMaterialByCoordinate(layer, x, row);
                    if (type != null && type.getId() == 0){
                        maxLocation = row;
                        return maxLocation;
                    }
            }
        }
        return 0;
    }

    public boolean doesRectCollideWithMap(float x, float y, int width, int height) {
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight())
            return true;

        for (int layer = getLayers() -1 ; layer >= 0 ; layer--) {
                Material type = getMaterialByCoordinate(layer, (int)x, (int)y);
                if (type != null && type.isCollidable())
                    return true;
        }

        return false;
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int layer = 0; layer < getLayers(); layer++) {
            for (int row = 0; row < getHeight(); row++) {
                for (int col = 0; col < getWidth(); col++) {
                    Material type = this.getMaterialByCoordinate(layer, col, row);
                    if (type != null && !type.isEnemy() && type.getId() != 0)
                        batch.draw(tiles[0][type.getId() - 1], col * Material.SIZE, row * Material.SIZE);
                    if (type != null && type.getId() == 21)
                        batch.draw(tiles[0][11 - 1], col * Material.SIZE, row * Material.SIZE);
                }
            }
        }
        lv.render(player,creatures,items,  batch);
        batch.end();

    }

    public void update(float delta) {
        timeElapsed += delta;
    }

    public void dispose() {}

    public Player getPlayer() {
        return player;
    }

    public String getId() {
        return id;
    }

    public Material getMaterialByCoordinate(int layer, int col, int row) {
        if (col < 0 || col >= getWidth() || row < 0 || row > getHeight() || getHeight() - 1 - row < 0)
            return null;

        return Material.getMaterialById(map[layer][getHeight() - 1 - row ][col]);
    }

    public Material getMaterialByLocation(int layer, float x, float y) {
        return this.getMaterialByCoordinate(layer, (int) (x / Material.SIZE), (int) (y / Material.SIZE));
    }

    public void eraseBlock(float x, float y) throws Exception {
        int col = (int) (x / Material.SIZE);
        int row = (int) (y / Material.SIZE);
        if (col < 0 || col >= getWidth() || row < 0 || row > getHeight() || getHeight() - 1 - row < 0)
            throw new Exception();
        else {
            map[1][getHeight() - 1 - row ][col] = 0;

        }
    }
    public void putBlock(float x, float y) throws Exception {
        int col = (int) (x / Material.SIZE);
        int row = (int) (y / Material.SIZE);
        if (col < 0 || col >= getWidth() || row < 0 || row > getHeight() || getHeight() - 1 - row < 0)
            throw new Exception();
        else {
            map[1][getHeight() - 1 - row ][col] = 6;

        }
    }
    public int getWidth() {
        return map[0][0].length;
    }
    public int getHeight() {
        return map[0].length;
    }
    public int getLayers() {
        return map.length;
    }
    public int getPixelWidth() {
        return this.getWidth() * Material.SIZE;
    }

    public int getPixelHeight() {
        return this.getHeight() * Material.SIZE;
    }
    public long getSeed() {
        return seed;
    }


    public static class PerlinWorm {
        private final int[][] grid;


        public PerlinWorm(int width, int height, double scale, int octaves, double persistence, double lacunarity, int Value, double probability) {
            this.grid = new int[width][height];

            Random rng = new Random();
            double offsetX = rng.nextDouble() * 1000;
            double offsetY = rng.nextDouble() * 1000;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double noiseValue = 0;
                    double amplitude = 1;
                    double frequency = 1;
                    for (int i = 0; i < octaves; i++) {
                        double sampleX = (x + offsetX) / scale * frequency;
                        double sampleY = (y + offsetY) / scale * frequency;
                        double perlinValue = noise(sampleX, sampleY);
                        noiseValue += perlinValue * amplitude;
                        amplitude *= persistence;
                        frequency *= lacunarity;
                    }
                    if (noiseValue > probability) {
                        this.grid[x][y] = Value;
                    }
                }
            }
        }

        private double noise(double x, double y) {
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;
            x -= Math.floor(x);
            y -= Math.floor(y);
            double u = fade(x);
            double v = fade(y);
            int A = p[X] + Y;
            int AA = p[A], AB = p[A + 1];
            int B = p[X + 1] + Y;
            int BA = p[B], BB = p[B + 1];
            return lerp(v, lerp(u, grad(p[AA], x, y), grad(p[BA], x - 1, y)),
                    lerp(u, grad(p[AB], x, y - 1), grad(p[BB], x - 1, y - 1)));
        }

        private double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double y) {
            int h = hash & 3;
            double u = h == 0 ? x : h == 1 ? y : x + y;
            double v = h == 0 ? y : h == 1 ? x : y - x;
            return ((hash & 4) == 0 ? 1 : -1) * (u + v);
        }

        private static final int[] p = new int[512];
        static {
            Random rng = new Random();
            for (int i = 0; i < 256; i++) {
                p[i] = p[i + 256] = rng.nextInt(256);
            }
        }

        public int[][] getGrid() {
            return this.grid;
        }
    }

    public int getScore() {
        return score;
    }

    public void increaseScore(int amount){
        score += amount;
    }

    public ArrayList<Enemy> getEnemyList(){
        return creatures;
    }

    public Map<Coord, ItemStack> getItems() {
        return items;
    }


    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("map", map);
        json.writeValue("player", player);
        json.writeValue("LevelRenderer", lv);
        json.writeValue("tiles", tiles);
        json.writeValue("width", width);
        json.writeValue("height", height);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        id = json.readValue("id", String.class, jsonData);
        map = json.readValue("map", int[][][].class, jsonData);
        player = json.readValue("player", Player.class, jsonData);
        lv = json.readValue("LevelRenderer", LevelRenderer.class, jsonData);
        tiles = json.readValue("tiles", TextureRegion[][].class, jsonData);
        width = json.readValue("width", int.class, jsonData);
        width = json.readValue("width", int.class, jsonData);
    }

    public static class Coord {
        private float x;
        private float y;

        Coord(float x, float y){
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
