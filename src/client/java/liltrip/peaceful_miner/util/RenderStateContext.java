package liltrip.peaceful_miner.util;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;

public class RenderStateContext {
    private static final ThreadLocal<LivingEntityRenderState> CURRENT_STATE = new ThreadLocal<>();

    public static void set(LivingEntityRenderState state) {
        CURRENT_STATE.set(state);
    }

    public static LivingEntityRenderState get() {
        return CURRENT_STATE.get();
    }

    public static void clear() {
        CURRENT_STATE.remove();
    }
}
