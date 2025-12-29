package liltrip.peaceful_miner.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import liltrip.peaceful_miner.Peaceful_miner;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new OpacityConfigScreen(parent);
    }

    private static class OpacityConfigScreen extends Screen {
        private final Screen parent;

        protected OpacityConfigScreen(Screen parent) {
            super(Text.of("Peaceful Miner Config"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int centerX = this.width / 2;
            int startY = this.height / 2 - 60;

            // Opacity Slider
            this.addDrawableChild(new SliderWidget(centerX - 100, startY, 200, 20, Text.of("Player Opacity: " + (int)(Peaceful_miner.CONFIG.playerOpacity * 100) + "%"), Peaceful_miner.CONFIG.playerOpacity) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.of("Player Opacity: " + (int)(this.value * 100) + "%"));
                }

                @Override
                protected void applyValue() {
                    Peaceful_miner.CONFIG.playerOpacity = (float) this.value;
                }
            });

            // Range Slider (0 to 10 blocks)
            this.addDrawableChild(new SliderWidget(centerX - 100, startY + 25, 200, 20, Text.of("Mine-Through Range: " + String.format("%.1f", Peaceful_miner.CONFIG.mineThroughRange) + " blocks"), Peaceful_miner.CONFIG.mineThroughRange / 10.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.of("Mine-Through Range: " + String.format("%.1f", this.value * 10.0) + " blocks"));
                }

                @Override
                protected void applyValue() {
                    Peaceful_miner.CONFIG.mineThroughRange = (float) (this.value * 10.0);
                }
            });

            // Drop Protection Toggle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Drop Protection: " + (Peaceful_miner.CONFIG.dropProtectionEnabled ? "ON" : "OFF")), button -> {
                Peaceful_miner.CONFIG.dropProtectionEnabled = !Peaceful_miner.CONFIG.dropProtectionEnabled;
                button.setMessage(Text.of("Drop Protection: " + (Peaceful_miner.CONFIG.dropProtectionEnabled ? "ON" : "OFF")));
            }).dimensions(centerX - 100, startY + 50, 200, 20).build());

            // HUD Settings Button
            this.addDrawableChild(ButtonWidget.builder(Text.of("HUD Settings..."), button -> {
                this.client.setScreen(new HudConfigScreen(this));
            }).dimensions(centerX - 100, startY + 75, 200, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.of("Done"), button -> {
                Peaceful_miner.CONFIG.save();
                this.client.setScreen(this.parent);
            }).dimensions(centerX - 100, startY + 110, 200, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        }

        @Override
        public void close() {
            Peaceful_miner.CONFIG.save();
            this.client.setScreen(this.parent);
        }
    }

    private static class HudConfigScreen extends Screen {
        private final Screen parent;

        protected HudConfigScreen(Screen parent) {
            super(Text.of("HUD Settings"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int centerX = this.width / 2;
            int startY = this.height / 2 - 50;

            // Satchel HUD Toggle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Satchel HUD: " + (Peaceful_miner.CONFIG.showSatchelHud ? "ON" : "OFF")), button -> {
                Peaceful_miner.CONFIG.showSatchelHud = !Peaceful_miner.CONFIG.showSatchelHud;
                button.setMessage(Text.of("Satchel HUD: " + (Peaceful_miner.CONFIG.showSatchelHud ? "ON" : "OFF")));
            }).dimensions(centerX - 100, startY - 25, 200, 20).build());

            // Background Toggle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Show Background: " + (Peaceful_miner.CONFIG.satchelHudShowBackground ? "ON" : "OFF")), button -> {
                Peaceful_miner.CONFIG.satchelHudShowBackground = !Peaceful_miner.CONFIG.satchelHudShowBackground;
                button.setMessage(Text.of("Show Background: " + (Peaceful_miner.CONFIG.satchelHudShowBackground ? "ON" : "OFF")));
            }).dimensions(centerX - 100, startY, 200, 20).build());

            // Text Shadow Toggle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Text Shadow: " + (Peaceful_miner.CONFIG.satchelHudTextShadow ? "ON" : "OFF")), button -> {
                Peaceful_miner.CONFIG.satchelHudTextShadow = !Peaceful_miner.CONFIG.satchelHudTextShadow;
                button.setMessage(Text.of("Text Shadow: " + (Peaceful_miner.CONFIG.satchelHudTextShadow ? "ON" : "OFF")));
            }).dimensions(centerX - 100, startY + 25, 98, 20).build());

            // Show Icons Toggle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Icons: " + (Peaceful_miner.CONFIG.satchelHudShowIcons ? "ON" : "OFF")), button -> {
                Peaceful_miner.CONFIG.satchelHudShowIcons = !Peaceful_miner.CONFIG.satchelHudShowIcons;
                button.setMessage(Text.of("Icons: " + (Peaceful_miner.CONFIG.satchelHudShowIcons ? "ON" : "OFF")));
            }).dimensions(centerX + 2, startY + 25, 98, 20).build());

            // Dynamic Colors Toggle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Dynamic Colors: " + (Peaceful_miner.CONFIG.satchelHudDynamicColors ? "ON" : "OFF")), button -> {
                Peaceful_miner.CONFIG.satchelHudDynamicColors = !Peaceful_miner.CONFIG.satchelHudDynamicColors;
                button.setMessage(Text.of("Dynamic Colors: " + (Peaceful_miner.CONFIG.satchelHudDynamicColors ? "ON" : "OFF")));
            }).dimensions(centerX - 100, startY + 50, 200, 20).build());

            // Empty Color Cycle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Empty: " + getColorName(Peaceful_miner.CONFIG.satchelHudColorEmpty)), button -> {
                Peaceful_miner.CONFIG.satchelHudColorEmpty = getNextColor(Peaceful_miner.CONFIG.satchelHudColorEmpty);
                button.setMessage(Text.of("Empty: " + getColorName(Peaceful_miner.CONFIG.satchelHudColorEmpty)));
            }).dimensions(centerX - 100, startY + 75, 98, 20).build());

            // Full Color Cycle
            this.addDrawableChild(ButtonWidget.builder(Text.of("Full: " + getColorName(Peaceful_miner.CONFIG.satchelHudColorFull)), button -> {
                Peaceful_miner.CONFIG.satchelHudColorFull = getNextColor(Peaceful_miner.CONFIG.satchelHudColorFull);
                button.setMessage(Text.of("Full: " + getColorName(Peaceful_miner.CONFIG.satchelHudColorFull)));
            }).dimensions(centerX + 2, startY + 75, 98, 20).build());

            // Satchel HUD Position Button
            this.addDrawableChild(ButtonWidget.builder(Text.of("Edit HUD Position"), button -> {
                this.client.setScreen(new SatchelHudPositionScreen(this));
            }).dimensions(centerX - 100, startY + 100, 200, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), button -> {
                this.client.setScreen(this.parent);
            }).dimensions(centerX - 100, startY + 125, 200, 20).build());
        }

        private static final int[] COLORS = {
            0xFF00FF00, // Green
            0xFFADFF2F, // GreenYellow
            0xFFFFFF00, // Yellow
            0xFFFFA500, // Orange
            0xFFFF0000, // Red
            0xFFFFFFFF, // White
            0xFFA9A9A9, // Dark Gray
            0xFF555555, // Gray
            0xFF0000FF, // Blue
            0xFF00FFFF, // Cyan
            0xFFA020F0, // Purple
            0xFFFF00FF  // Magenta
        };

        private static final String[] COLOR_NAMES = {
            "Green", "Lime", "Yellow", "Orange", "Red", "White", "D.Gray", "Gray", "Blue", "Cyan", "Purple", "Magenta"
        };

        private String getColorName(int color) {
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i] == color) return COLOR_NAMES[i];
            }
            return "Custom";
        }

        private int getNextColor(int color) {
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i] == color) return COLORS[(i + 1) % COLORS.length];
            }
            return COLORS[0];
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        }

        @Override
        public void close() {
            this.client.setScreen(this.parent);
        }
    }
}
