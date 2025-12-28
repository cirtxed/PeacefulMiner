package liltrip.peaceful_miner.util;

import liltrip.peaceful_miner.Peaceful_miner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;

public class RenderUtil {
    public static boolean shouldBeTranslucent(LivingEntityRenderState state) {
        if (state == null) return false;

        // Ignore armor stands
        if (state.getClass().getName().contains("ArmorStand")) {
            return false;
        }

        if ((Object)state instanceof LocalPlayerRenderState localState && localState.isLocalPlayer()) {
            return false;
        }
        
        PlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer != null && PlayerUtil.isHoldingPickaxe(clientPlayer)) {
            float range = Peaceful_miner.CONFIG.mineThroughRange;
            float checkRange = range + 5.0f;
            if (state.squaredDistanceToCamera <= checkRange * checkRange) {
                 return isPlayerState(state);
            }
        }
        return false;
    }

    public static boolean isPlayerState(LivingEntityRenderState state) {
        if (state instanceof PlayerEntityRenderState || state instanceof BipedEntityRenderState) {
            return true;
        }

        try {
            Class<?> clazz = state.getClass();
            while (clazz != null && clazz != Object.class) {
                String name = clazz.getName();
                if (name.contains("Player") || name.contains("Biped") || name.contains("Armor")) {
                    return true;
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception ignored) {}
        
        return false;
    }
}
