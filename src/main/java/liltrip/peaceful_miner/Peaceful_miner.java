package liltrip.peaceful_miner;

import liltrip.peaceful_miner.config.ModConfig;
import net.fabricmc.api.ModInitializer;

public class Peaceful_miner implements ModInitializer {
    public static ModConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = ModConfig.load();
    }
}
