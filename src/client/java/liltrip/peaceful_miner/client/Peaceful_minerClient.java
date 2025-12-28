package liltrip.peaceful_miner.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.text.Text;

import java.util.List;

import static liltrip.peaceful_miner.Peaceful_miner.CONFIG;
import liltrip.peaceful_miner.util.SatchelUtil;

public class Peaceful_minerClient implements ClientModInitializer {
    private static SatchelHud satchelHud;

    @Override
    public void onInitializeClient() {
        satchelHud = new SatchelHud();
        ClientTickEvents.END_CLIENT_TICK.register(satchelHud);

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (satchelHud != null) {
                satchelHud.onHudRender(drawContext, tickCounter);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var cmd = ClientCommandManager.literal("peacefulminer")
                .executes(context -> {
                    context.getSource().sendFeedback(Text.literal("Peaceful Miner Mod Commands:"));
                    context.getSource().sendFeedback(Text.literal("/peacefulminer debug - Show debug info"));
                    context.getSource().sendFeedback(Text.literal("/peacefulminer satchel toggle - Toggle Satchel HUD"));
                    context.getSource().sendFeedback(Text.literal("/peacefulminer satchel position - Edit Satchel HUD position"));
                    context.getSource().sendFeedback(Text.literal("/peacefulminer satchel debug - Show satchel info"));
                    return 1;
                })
                .then(ClientCommandManager.literal("debug")
                    .executes(context -> {
                        var player = context.getSource().getPlayer();
                        var stack = player.getMainHandStack();
                        if (stack.isEmpty()) {
                            context.getSource().sendFeedback(Text.literal("Holding nothing."));
                            return 1;
                        }
                        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
                        if (nbtComponent == null) {
                            context.getSource().sendFeedback(Text.literal("No custom NBT data on this item."));
                        } else {
                            String nbtString = nbtComponent.copyNbt().toString();
                            context.getSource().sendFeedback(Text.literal("NBT: " + nbtString));
                            System.out.println("[DEBUG_LOG] NBT for " + stack.getName().getString() + ": " + nbtString);
                        }

                        context.getSource().sendFeedback(Text.literal("Config - Range: " + CONFIG.mineThroughRange + ", Opacity: " + CONFIG.playerOpacity));
                        return 1;
                    }))
                .then(ClientCommandManager.literal("satchel")
                    .then(ClientCommandManager.literal("toggle")
                        .executes(context -> {
                            CONFIG.showSatchelHud = !CONFIG.showSatchelHud;
                            CONFIG.save();
                            context.getSource().sendFeedback(Text.literal("Satchel HUD: " + (CONFIG.showSatchelHud ? "Enabled" : "Disabled")));
                            return 1;
                        }))
                    .then(ClientCommandManager.literal("position")
                        .executes(context -> {
                            var client = context.getSource().getClient();
                            client.send(() -> client.setScreen(new SatchelHudPositionScreen(null)));
                            return 1;
                        }))
                    .then(ClientCommandManager.literal("size")
                        .then(ClientCommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0.1f, 5.0f))
                            .executes(context -> {
                                float size = com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value");
                                CONFIG.satchelHudSize = size;
                                CONFIG.save();
                                context.getSource().sendFeedback(Text.literal("Satchel HUD Size set to: " + size));
                                return 1;
                            })))
                    .then(ClientCommandManager.literal("debug")
                        .executes(context -> {
                            var client = context.getSource().getClient();
                            if (satchelHud != null) {
                                satchelHud.refresh(client);
                            }
                            var player = context.getSource().getPlayer();
                            List<SatchelUtil.SatchelInfo> satchels = SatchelUtil.getSatchelsInInventory(player);
                            if (satchels.isEmpty()) {
                                context.getSource().sendFeedback(Text.literal("No satchels found in inventory."));
                            } else {
                                context.getSource().sendFeedback(Text.literal("Found " + satchels.size() + " satchels:"));
                                for (SatchelUtil.SatchelInfo satchel : satchels) {
                                    context.getSource().sendFeedback(Text.literal(String.format("- %s: %d/%d (%.1f%%)",
                                        satchel.name, satchel.count, satchel.capacity, satchel.getPercentage() * 100)));
                                }
                            }
                            return 1;
                        })));
            dispatcher.register(cmd);
            dispatcher.register(ClientCommandManager.literal("pm").redirect(cmd.build()));
        });
    }
}
