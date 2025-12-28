package liltrip.peaceful_miner.client;

import liltrip.peaceful_miner.Peaceful_miner;
import liltrip.peaceful_miner.util.SatchelUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;

public class SatchelHud implements HudRenderCallback, ClientTickEvents.EndTick {
    private List<SatchelUtil.SatchelInfo> cachedSatchels = List.of();
    private int tickCounter = 19;

    public void refresh(MinecraftClient client) {
        if (client.player != null) {
            cachedSatchels = SatchelUtil.getSatchelsInInventory(client.player);
            tickCounter = 0;
        }
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        if (!Peaceful_miner.CONFIG.showSatchelHud) return;
        if (client.player == null || client.world == null) return;

        tickCounter++;
        if (tickCounter >= 20) {
            refresh(client);
        }
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!Peaceful_miner.CONFIG.showSatchelHud) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (client.options.hudHidden) return;

        TextRenderer textRenderer = client.textRenderer;
        float size = Peaceful_miner.CONFIG.satchelHudSize;
        int x = Peaceful_miner.CONFIG.satchelHudX;
        int y = Peaceful_miner.CONFIG.satchelHudY;

        if (cachedSatchels == null || cachedSatchels.isEmpty()) {
            return;
        }

        // Try to use matrices for scaling
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(size, size);
        
        int scaledX = (int) (x / size);
        int scaledY = (int) (y / size);

        // Calculate total width and height
        int maxWidth = 0;
        for (SatchelUtil.SatchelInfo satchel : cachedSatchels) {
            String text = String.format("%s: %.1f%% (%d/%d)", 
                satchel.name, satchel.getPercentage() * 100, satchel.count, satchel.capacity);
            maxWidth = Math.max(maxWidth, textRenderer.getWidth(text));
        }
        
        if (maxWidth > 0) {
            int totalHeight = cachedSatchels.size() * 10;
            // Draw black border
            int borderColor = 0xFF000000;
            
            // Draw background
            drawContext.fill(scaledX - 2, scaledY - 2, scaledX + maxWidth + 2, scaledY + totalHeight + 2, 0x88000000);
            
            // Draw border (1 pixel outline)
            drawContext.fill(scaledX - 3, scaledY - 3, scaledX + maxWidth + 3, scaledY - 2, borderColor); // Top
            drawContext.fill(scaledX - 3, scaledY + totalHeight + 2, scaledX + maxWidth + 3, scaledY + totalHeight + 3, borderColor); // Bottom
            drawContext.fill(scaledX - 3, scaledY - 2, scaledX - 2, scaledY + totalHeight + 2, borderColor); // Left
            drawContext.fill(scaledX + maxWidth + 2, scaledY - 2, scaledX + maxWidth + 3, scaledY + totalHeight + 2, borderColor); // Right
        }

        int currentY = scaledY;
        for (SatchelUtil.SatchelInfo satchel : cachedSatchels) {
            String text = String.format("%s: %.1f%% (%d/%d)", 
                satchel.name, satchel.getPercentage() * 100, satchel.count, satchel.capacity);
            
            int textColor = getPercentColor(satchel.getPercentage());
            drawContext.drawText(textRenderer, text, scaledX, currentY, textColor, true);
            currentY += 10;
        }
        
        drawContext.getMatrices().popMatrix();
    }

    public static int getPercentColor(float percent) {
        if (!Peaceful_miner.CONFIG.satchelHudDynamicColors) {
            return Peaceful_miner.CONFIG.satchelHudColorEmpty;
        }

        int startColor = Peaceful_miner.CONFIG.satchelHudColorEmpty;
        int endColor = Peaceful_miner.CONFIG.satchelHudColorFull;

        int a1 = (startColor >> 24) & 0xFF;
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >> 24) & 0xFF;
        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = (int) (a1 + (a2 - a1) * percent);
        int r = (int) (r1 + (r2 - r1) * percent);
        int g = (int) (g1 + (g2 - g1) * percent);
        int b = (int) (b1 + (b2 - b1) * percent);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
