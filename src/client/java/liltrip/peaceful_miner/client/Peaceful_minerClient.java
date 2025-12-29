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
                    context.getSource().sendFeedback(Text.literal("/peacefulminer satchel toggle - Toggle Satchel HUD"));
                    context.getSource().sendFeedback(Text.literal("/peacefulminer satchel position - Edit Satchel HUD position"));
                    return 1;
                })
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
                            }))));
            dispatcher.register(cmd);
            dispatcher.register(ClientCommandManager.literal("pm").redirect(cmd.build()));
        });
    }
}
