package dev.aether.client.gui;

import dev.aether.client.AetherClient;
import dev.aether.client.module.Module;
import dev.aether.client.scan.BaseSurveyor;
import dev.aether.client.scan.StashRadar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClickGuiScreen extends Screen {
    private static final int PANEL = 0xF0141722;
    private static final int PANEL_INNER = 0xFF1C2230;
    private static final int PURPLE = 0xFF9B7BFF;
    private static final int MUTED = 0xFF9AA4B2;
    private static final int WHITE = 0xFFF2F3F7;
    private static final int LEFT = 34;
    private static final int TOP = 30;
    private static final int WIDTH = 310;
    private static final int ROW_HEIGHT = 42;

    public ClickGuiScreen() { super(Component.literal("AETHER CLIENT")); }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);
        int x = Math.max(12, (width - WIDTH) / 2);
        int y = Math.max(12, (height - 430) / 2);
        g.fill(x, y, x + WIDTH, y + 430, PANEL);
        g.fill(x, y, x + WIDTH, y + 3, PURPLE);
        g.drawString(font, "A E T H E R", x + 18, y + 17, WHITE, false);
        g.drawString(font, "CLIENT  /  1.21.11", x + 18, y + 33, MUTED, false);
        g.fill(x + 16, y + 54, x + WIDTH - 16, y + 55, 0x553A4050);
        g.drawString(font, "MODULES", x + 18, y + 67, PURPLE, false);

        int row = 0;
        for (Module module : AetherClient.MODULES.all()) {
            int ry = y + 84 + row++ * ROW_HEIGHT;
            g.fill(x + 14, ry, x + WIDTH - 14, ry + 34, PANEL_INNER);
            g.drawString(font, module.name(), x + 25, ry + 7, WHITE, false);
            g.drawString(font, module.description(), x + 25, ry + 20, MUTED, false);
            int color = module.enabled() ? 0xFF8C72F5 : 0xFF596170;
            g.fill(x + WIDTH - 48, ry + 11, x + WIDTH - 27, ry + 23, color);
            g.fill(x + (module.enabled() ? WIDTH - 36 : WIDTH - 47), ry + 12, x + (module.enabled() ? WIDTH - 28 : WIDTH - 39), ry + 22, WHITE);
        }

        int infoY = y + 225;
        g.fill(x + 14, infoY, x + WIDTH - 14, y + 407, 0xFF171D29);
        g.drawString(font, "EXPLORATION INTEL", x + 25, infoY + 12, PURPLE, false);
        StashRadar stash = AetherClient.MODULES.get(StashRadar.class);
        BaseSurveyor base = AetherClient.MODULES.get(BaseSurveyor.class);
        g.drawString(font, "Storage marks: " + (stash == null ? 0 : stash.results().size()), x + 25, infoY + 34, WHITE, false);
        g.drawString(font, "Base candidates: " + (base == null ? 0 : base.candidates().size()), x + 25, infoY + 50, WHITE, false);
        g.drawString(font, "Scans cover loaded client terrain only.", x + 25, infoY + 72, MUTED, false);
        g.drawString(font, "RIGHT SHIFT  ·  CLOSE", x + 18, y + 414, MUTED, false);
        super.render(g, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int x = Math.max(12, (width - WIDTH) / 2);
        int y = Math.max(12, (height - 430) / 2);
        int row = 0;
        for (Module module : AetherClient.MODULES.all()) {
            int ry = y + 84 + row++ * ROW_HEIGHT;
            if (mouseX >= x + 14 && mouseX <= x + WIDTH - 14 && mouseY >= ry && mouseY <= ry + 34) {
                module.toggle();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean isPauseScreen() { return false; }
}
