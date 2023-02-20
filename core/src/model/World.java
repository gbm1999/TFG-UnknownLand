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

        System.out.println("Generando cuevas");
        int SCALE = 30;
        int OCTAVES = 4;
        double PERSISTENCE = 0.5;
        double LACUNARITY = 2.0;
        PerlinWorm perlinWorm = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY);

        int[][] grid = perlinWorm.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == 1 && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13) {
                    map[1][z][x] = Material.SKY.getId();
                }
            }
        }

        System.out.println ("Generando menas");
        SCALE = 5;
        OCTAVES = 4;
        PERSISTENCE = 0.0001;
        LACUNARITY = 1.0;
        PerlinWorm perlinWorm2 = new PerlinWorm(width , height, SCALE, OCTAVES, PERSISTENCE, LACUNARITY);

        grid = perlinWorm2.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z] == 1 && map[1][z][x] != 0 && map[1][z][x] != 2 && map[1][z][x] != 1 && map[1][z][x] != 13 && map[1][z][x] != 3) {
                    map[1][z][x] = Material.IRON.getId();
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
        if (col < 0 || col >= getWidth() || row < 0 || row > getHeight() || getHeight() - 1 - row < 0)
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


    public static class PerlinWorm {

        private final int[][] grid;

        public PerlinWorm(int width, int height, double scale, int octaves, double persistence, double lacunarity) {
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
                    if (noiseValue > 0.3) {
                        this.grid[x][y] = 1;
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
}
