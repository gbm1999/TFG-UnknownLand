package persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import model.Material;
import model.World;

import org.bukkit.util.noise.CombinedNoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import java.util.Random;

public class WorldMapProvider {
    private static Json json = new Json();
    public int[][][] map;


    public static WorldMapProvider loadMap(String id, int SIZE, World w) {
        if (SIZE < 100) {
            SIZE = 100;
        }
        Gdx.files.local("maps/").file().mkdirs();
        FileHandle file = Gdx.files.local("maps/" + id + ".map");
        WorldMapProvider data = new WorldMapProvider();
       // if (file.exists()) {
          //  data = json.fromJson(WorldMapProvider.class, file.readString());
        //} else {
            w = w.generateRandomMap(id, SIZE);
            data.map = w.map;
            saveMap(id, w.map);
        //}
        return data;
    }

    public static void saveMap(String id, int[][][] map) {

        WorldMapProvider worldMapProvider = new WorldMapProvider();
        worldMapProvider.map = map;

        Gdx.files.local("maps/").file().mkdirs();
        FileHandle file = Gdx.files.local("maps/" + id + ".map");
        file.writeString(json.prettyPrint(worldMapProvider), false);
    }
}