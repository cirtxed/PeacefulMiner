package liltrip.peaceful_miner.mixin.client;

import liltrip.peaceful_miner.util.LocalPlayerRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements LocalPlayerRenderState {
    @Unique
    private boolean peaceful_miner$isLocalPlayer;

    @Override
    public void setLocalPlayer(boolean localPlayer) {
        this.peaceful_miner$isLocalPlayer = localPlayer;
    }

    @Override
    public boolean isLocalPlayer() {
        return this.peaceful_miner$isLocalPlayer;
    }
}
