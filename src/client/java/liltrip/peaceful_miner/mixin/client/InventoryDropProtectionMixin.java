package liltrip.peaceful_miner.mixin.client;

import liltrip.peaceful_miner.Peaceful_miner;
import liltrip.peaceful_miner.util.SatchelUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class InventoryDropProtectionMixin {

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    private void onClickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!Peaceful_miner.CONFIG.dropProtectionEnabled) return;
        if (player.currentScreenHandler == null) return;

        ItemStack stackToDrop = ItemStack.EMPTY;

        if (actionType == SlotActionType.THROW) {
            if (slotId >= 0 && slotId < player.currentScreenHandler.slots.size()) {
                stackToDrop = player.currentScreenHandler.getSlot(slotId).getStack();
            }
        } else if (slotId == -999 && actionType == SlotActionType.PICKUP) {
            // Clicking outside with an item in cursor
            stackToDrop = player.currentScreenHandler.getCursorStack();
        }

        if (!stackToDrop.isEmpty() && isProtectedItem(stackToDrop)) {
            cancelDrop(player);
            ci.cancel();
        }
    }

    private boolean isProtectedItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.isIn(ItemTags.PICKAXES)) return true;
        if (SatchelUtil.getSatchelInfo(stack) != null) return true;
        return false;
    }

    private void cancelDrop(PlayerEntity player) {
        player.sendMessage(Text.literal("§cYou cannot drop this item due to Peaceful Miner protection"), false);
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}
