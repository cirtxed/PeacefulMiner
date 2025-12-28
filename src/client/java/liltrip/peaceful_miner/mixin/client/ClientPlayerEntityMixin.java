package liltrip.peaceful_miner.mixin.client;

import liltrip.peaceful_miner.Peaceful_miner;
import liltrip.peaceful_miner.util.SatchelUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (!Peaceful_miner.CONFIG.dropProtectionEnabled) return;

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (isProtectedItem(stack)) {
            cancelDrop(player);
            cir.setReturnValue(false);
        }
    }

    private boolean isProtectedItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        // Check if it's a pickaxe using tags
        if (stack.isIn(ItemTags.PICKAXES)) return true;
        
        // Check if it's a satchel
        if (SatchelUtil.getSatchelInfo(stack) != null) return true;
        
        return false;
    }

    private void cancelDrop(ClientPlayerEntity player) {
        player.sendMessage(Text.literal("§cYou cannot drop this item due to Peaceful Miner protection"), false);
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}
