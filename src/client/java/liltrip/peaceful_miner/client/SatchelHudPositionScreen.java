package liltrip.peaceful_miner.client;

import liltrip.peaceful_miner.Peaceful_miner;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SatchelHudPositionScreen extends Screen {
    private final Screen parent;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    public SatchelHudPositionScreen(Screen parent) {
        super(Text.of("HUD Position"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw example HUD
        int x = Peaceful_miner.CONFIG.satchelHudX;
        int y = Peaceful_miner.CONFIG.satchelHudY;
        float size = Peaceful_miner.CONFIG.satchelHudSize;
        
        String exampleText = "Satchel Example: 50.0% (100/200)";
        int textWidth = this.textRenderer.getWidth(exampleText);
        int textHeight = 10;

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(size, size);

        int scaledX = (int) (x / size);
        int scaledY = (int) (y / size);

        int borderColor = 0xFF000000; // Black
        int textColor = SatchelHud.getPercentColor(0.5f);
        
        // Draw background
        context.fill(scaledX - 2, scaledY - 2, scaledX + textWidth + 2, scaledY + textHeight + 2, 0x88000000);
        
        // Draw border
        context.fill(scaledX - 3, scaledY - 3, scaledX + textWidth + 3, scaledY - 2, borderColor); // Top
        context.fill(scaledX - 3, scaledY + textHeight + 2, scaledX + textWidth + 3, scaledY + textHeight + 3, borderColor); // Bottom
        context.fill(scaledX - 3, scaledY - 2, scaledX - 2, scaledY + textHeight + 2, borderColor); // Left
        context.fill(scaledX + textWidth + 2, scaledY - 2, scaledX + textWidth + 3, scaledY + textHeight + 2, borderColor); // Right

        context.drawText(this.textRenderer, exampleText, scaledX, scaledY, textColor, true);

        context.getMatrices().popMatrix();

        context.drawCenteredTextWithShadow(this.textRenderer, "Drag the example HUD to reposition it", this.width / 2, 20, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Use Mouse Wheel or Up/Down arrows to scale: " + String.format("%.1f", size), this.width / 2, 35, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press ESC to save and return", this.width / 2, 50, 0xFFAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float size = Peaceful_miner.CONFIG.satchelHudSize;
        if (verticalAmount > 0) {
            size += 0.1f;
        } else if (verticalAmount < 0) {
            size -= 0.1f;
        }
        Peaceful_miner.CONFIG.satchelHudSize = Math.max(0.1f, Math.min(5.0f, size));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int x = Peaceful_miner.CONFIG.satchelHudX;
            int y = Peaceful_miner.CONFIG.satchelHudY;
            float size = Peaceful_miner.CONFIG.satchelHudSize;
            String exampleText = "Satchel Example: 50.0% (100/200)";
            int textWidth = (int) (this.textRenderer.getWidth(exampleText) * size);
            int textHeight = (int) (10 * size);

            if (mouseX >= x - (3 * size) && mouseX <= x + textWidth + (3 * size) && mouseY >= y - (3 * size) && mouseY <= y + textHeight + (3 * size)) {
                dragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            Peaceful_miner.CONFIG.satchelHudX = (int) mouseX - dragOffsetX;
            Peaceful_miner.CONFIG.satchelHudY = (int) mouseY - dragOffsetY;
            
            // Constrain to screen
            Peaceful_miner.CONFIG.satchelHudX = Math.max(0, Math.min(Peaceful_miner.CONFIG.satchelHudX, this.width - 10));
            Peaceful_miner.CONFIG.satchelHudY = Math.max(0, Math.min(Peaceful_miner.CONFIG.satchelHudY, this.height - 10));
            
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Peaceful_miner.CONFIG.save();
            this.client.setScreen(this.parent);
            return true;
        }
        
        if (keyCode == GLFW.GLFW_KEY_UP) {
            Peaceful_miner.CONFIG.satchelHudSize = Math.min(5.0f, Peaceful_miner.CONFIG.satchelHudSize + 0.1f);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            Peaceful_miner.CONFIG.satchelHudSize = Math.max(0.1f, Peaceful_miner.CONFIG.satchelHudSize - 0.1f);
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
