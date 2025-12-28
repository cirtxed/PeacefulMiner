package liltrip.peaceful_miner.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

public class PlayerUtil {
    public static boolean isHoldingPickaxe(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        return stack.isIn(ItemTags.PICKAXES);
    }
}
