package liltrip.peaceful_miner.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("peacefullminer.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public float playerOpacity = 0.5f;
    public float mineThroughRange = 3.0f;
    public boolean showSatchelHud = true;
    public int satchelHudX = 10;
    public int satchelHudY = 10;
    public float satchelHudSize = 1.0f;
    public boolean satchelHudDynamicColors = true;
    public boolean satchelHudShowBackground = true;
    public boolean satchelHudTextShadow = true;
    public boolean satchelHudShowIcons = false;
    public int satchelHudColorEmpty = 0xFF00FF00; // Green
    public int satchelHudColorFull = 0xFFFF0000;  // Red
    public boolean dropProtectionEnabled = true;

    public static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                return GSON.fromJson(Files.newBufferedReader(CONFIG_PATH), ModConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
