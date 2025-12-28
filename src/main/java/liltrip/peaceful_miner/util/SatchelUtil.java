package liltrip.peaceful_miner.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SatchelUtil {

    public static class SatchelInfo {
        public final String name;
        public final int count;
        public final int capacity;

        public SatchelInfo(String name, int count, int capacity) {
            this.name = name;
            this.count = count;
            this.capacity = capacity;
        }

        public float getPercentage() {
            if (capacity <= 0) return 0;
            return (float) count / capacity;
        }
    }

    public static List<SatchelInfo> getSatchelsInInventory(PlayerEntity player) {
        Map<String, SatchelInfo> satchelMap = new LinkedHashMap<>();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            SatchelInfo info = getSatchelInfo(stack);
            if (info != null) {
                if (satchelMap.containsKey(info.name)) {
                    SatchelInfo existing = satchelMap.get(info.name);
                    satchelMap.put(info.name, new SatchelInfo(info.name, existing.count + info.count, existing.capacity + info.capacity));
                } else {
                    satchelMap.put(info.name, info);
                }
            }
        }
        return new ArrayList<>(satchelMap.values());
    }

    public static SatchelInfo getSatchelInfo(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return null;

        NbtCompound nbt = nbtComponent.copyNbt();
        NbtCompound bukkitValues = nbt.getCompoundOrEmpty("PublicBukkitValues");
        
        String itemId = bukkitValues.getString("cosmicprisons:custom_item_id").orElse("");
        if (itemId.isEmpty() || !itemId.contains("satchel")) return null;

        int count = bukkitValues.getInt("cosmicprisons:satchel_count").orElse(0);
        if (count == 0) {
            count = bukkitValues.getDouble("cosmicprisons:satchel_count").orElse(0.0).intValue();
        }

        int capacity = -1;
        
        // Try to get capacity from NBT first if available in some other field
        // But based on the example it seems it's only in name/lore
        
        Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (customName != null) {
            capacity = parseCapacityFromText(customName.getString());
        }
        
        if (capacity == -1) {
            // Try lore
            var lore = stack.get(DataComponentTypes.LORE);
            if (lore != null) {
                for (Text line : lore.lines()) {
                    capacity = parseCapacityFromText(line.getString());
                    if (capacity != -1) break;
                }
            }
        }

        if (capacity == -1) capacity = 1;

        // Construct a more descriptive name if possible
        String name = stack.getName().getString();
        String ore = bukkitValues.getString("cosmicprisons:satchel_ore").orElse("");
        if (!ore.isEmpty()) {
            boolean refined = bukkitValues.getBoolean("cosmicprisons:satchel_refined").orElse(false);
            name = ore.substring(0, 1).toUpperCase() + ore.substring(1).toLowerCase() + (refined ? " Refined" : " Ore") + " Satchel";
        }

        // Debug logging
        System.out.println("[DEBUG_LOG] Satchel check: " + name + " | ID: " + itemId + " | Count: " + count + " | Capacity: " + capacity);

        return new SatchelInfo(name, count, capacity);
    }

    private static final Pattern CAPACITY_PATTERN = Pattern.compile("/\\s*([\\d,]+)");

    private static int parseCapacityFromText(String text) {
        // Look for "/ 20,736" style strings
        Matcher matcher = CAPACITY_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                String capStr = matcher.group(1).replace(",", "");
                return Integer.parseInt(capStr);
            } catch (NumberFormatException ignored) {}
        }
        return -1;
    }
}
