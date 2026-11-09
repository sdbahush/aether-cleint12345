package dev.aether.client;

import dev.aether.client.gui.ClickGuiScreen;
import dev.aether.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public final class AetherClient implements ClientModInitializer {
    public static final ModuleManager MODULES = new ModuleManager();
    private static KeyMapping openGui;

    @Override public void onInitializeClient() {
        openGui = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.aether.open_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.aether"));
        ClientTickEvents.END_CLIENT_TICK.register(AetherClient::tick);
    }

    private static void tick(Minecraft client) {
        while (openGui.consumeClick()) {
            if (client.screen == null) client.setScreen(new ClickGuiScreen());
            else if (client.screen instanceof ClickGuiScreen) client.setScreen(null);
        }
        MODULES.tick(client);
    }
}
