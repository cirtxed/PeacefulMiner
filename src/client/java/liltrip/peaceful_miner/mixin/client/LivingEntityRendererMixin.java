package liltrip.peaceful_miner.mixin.client;

import liltrip.peaceful_miner.Peaceful_miner;
import liltrip.peaceful_miner.util.LocalPlayerRenderState;
import liltrip.peaceful_miner.util.PlayerUtil;
import liltrip.peaceful_miner.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<S>> {

    @Shadow protected abstract RenderLayer getRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline);
    @Shadow public abstract net.minecraft.util.Identifier getTexture(S state);

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("RETURN"))
    private void onUpdateRenderState(T entity, S state, float f, CallbackInfo ci) {
        if ((Object)state instanceof LocalPlayerRenderState localPlayerRenderState) {
            localPlayerRenderState.setLocalPlayer(entity == MinecraftClient.getInstance().player || (MinecraftClient.getInstance().player != null && entity.getUuid().equals(MinecraftClient.getInstance().player.getUuid())));
        }
    }

    @Redirect(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", 
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer getTranslucentLayer(LivingEntityRenderer<T, S, M> instance, S state, boolean showBody, boolean translucent, boolean showOutline) {
        boolean shouldBeTranslucent = translucent || RenderUtil.shouldBeTranslucent(state);
        
        RenderLayer layer = this.getRenderLayer(state, showBody, shouldBeTranslucent, showOutline);
        if (shouldBeTranslucent) {
             String layerStr = layer.toString().toLowerCase();
             if (!layerStr.contains("translucent")) {
                  // Fallback to force translucency if vanilla didn't provide it
                  return RenderLayer.getEntityTranslucent(instance.getTexture(state));
             }
        }
        return layer;
    }

    @Redirect(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void renderTranslucent(EntityModel<S> instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int packedColor, S state, MatrixStack matrices2, VertexConsumerProvider vertexConsumerProvider, int light2) {
        int color = packedColor;
        if (RenderUtil.shouldBeTranslucent(state)) {
            float opacity = Peaceful_miner.CONFIG.playerOpacity;
            int alpha = (int) (opacity * 255.0f);
            color = (alpha << 24) | (packedColor & 0x00FFFFFF);
        }
        instance.render(matrices, vertices, light, overlay, color);
    }
}
