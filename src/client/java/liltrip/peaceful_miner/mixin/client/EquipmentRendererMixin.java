package liltrip.peaceful_miner.mixin.client;

import liltrip.peaceful_miner.Peaceful_miner;
import liltrip.peaceful_miner.util.RenderStateContext;
import liltrip.peaceful_miner.util.RenderUtil;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {

    @Redirect(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer redirectGetArmorLayer(Identifier texture) {
        LivingEntityRenderState state = RenderStateContext.get();
        if (RenderUtil.shouldBeTranslucent(state)) {
            return RenderLayer.createArmorTranslucent(texture);
        }
        return RenderLayer.getArmorCutoutNoCull(texture);
    }

    @Redirect(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void redirectModelRender(Model model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int packedColor) {
        int color = packedColor;
        LivingEntityRenderState state = RenderStateContext.get();
        if (RenderUtil.shouldBeTranslucent(state)) {
            float opacity = Peaceful_miner.CONFIG.playerOpacity;
            int alpha = (int) (opacity * 255.0f);
            color = (alpha << 24) | (packedColor & 0x00FFFFFF);
        }
        model.render(matrices, vertices, light, overlay, color);
    }
}
