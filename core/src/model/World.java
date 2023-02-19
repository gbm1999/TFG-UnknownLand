package model;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.bukkit.util.noise.CombinedNoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import java.util.Random;

import persistence.WorldMapProvider;

public class World {

    private String id;
    public int[][][] map;
    private TextureRegion[][] tiles;
    private int width;
    private int height;
    long range = 1234567L;
    Random r = new Random();

    private final long seed = (long)(r.nextDouble()*range);

    public World(String id, int SIZE)
    {
        tiles = TextureRegion.split(new Texture("tiles.png"), Material.SIZE, Material.SIZE);
        width = SIZE * 8;
        height = SIZE;
        this.id = id;
        map = new int[5][SIZE][SIZE * 8];

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
            map[1][heightBlock][x] = Material.GRASS.getId();

            for (int z=height - 1; z>=0 && z > heightBlock ; z--) {
                double dirtThickness = noise.noise(x, z, 0.8, 2.0) / 24 - 4;
                double stoneTransition = heightBlock - dirtThickness +5;
                if (z==SIZE - 1) map[1][z][x] = Material.Granite.getId();
                else if (z > stoneTransition)
                    map[1][z][x] = Material.STONE.getId();
                else // if (y <= dirtTransition)
                    map[1][z][x] = Material.DIRT.getId();
            }
        }

        int numCuevas = SIZE * SIZE * 256 / 8192;
        int lengthCave1 = (int) ((Math.random() * (10 - 9)) + 9);
        int lengthCave2 = (int) ((Math.random() * (7 - 2)) + 2);

        System.out.print("Generando cuevas");
        for (int cueva = 1; cueva<numCuevas; cueva++) {
            int heightCave = (int) ((Math.random() * (12 - 5)) + 5);
            for (int x = (width / lengthCave1); x < (width / lengthCave2); x++) {
                double heightLow = noise1.noise(x * 1.8, x * 1.8) / 8.0 - 5.0;
                double heightHigh = noise2.noise(x * 1.6, x * 1.6) / 4.0 + 3.0;
                double heightResult;
                if (noise3.noise(x, x, 0.7, 2) / 8.0 > 0.0)
                    heightResult = heightLow;
                else
                    heightResult = Math.max(heightHigh, heightLow);
                heightResult /= 2.0;
                if (heightResult < 0.0)
                    heightResult = heightResult * 8.0 / 10.0;

                int heightBlock = (int) Math.floor(heightResult + (double)heightCave);
                heightBlock = Math.abs(heightBlock);
                if(heightBlock > 10 && heightBlock < height -12 ){
                    map[1][heightBlock][x] = Material.IRON.getId();
                    //System.out.println("z: " + heightBlock + z + " x: " + x);
                }

            }
        }
        return this;
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int layer = 0; layer < getLayers(); layer++) {
            for (int row = 0; row < getHeight(); row++) {
                for (int col = 0; col < getWidth(); col++) {
                    Material type = this.getMaterialByCoordinate(layer, col, row);
                    if (type != null)
                        batch.draw(tiles[0][type.getId() - 1], col * Material.SIZE, row * Material.SIZE);
                }
            }
        }
        batch.end();
    }

    public void update(float delta) {
        update(delta);
    }

    public void dispose() {}

    public Material getMaterialByCoordinate(int layer, int col, int row) {
        if (col < 0 || col >= getWidth() || row < 0 || row > getHeight())
            return null;

        return Material.getMaterialById(map[layer][getHeight() - 1 - row ][col]);
    }

    public Material getMaterialByLocation(int layer, float x, float y) {
        return this.getMaterialByCoordinate(layer, (int) (x / Material.SIZE), (int) (y / Material.SIZE));
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
    public long getSeed() {
        return seed;
    }
}
