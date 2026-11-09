package dev.aether.client.module;

import net.minecraft.client.Minecraft;

public abstract class Module {
    private final String name;
    private final String description;
    private boolean enabled;

    protected Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public final void toggle() { setEnabled(!enabled); }

    public final void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public final void tick(Minecraft client) {
        if (enabled) onTick(client);
    }

    protected void onEnable() {}
    protected void onDisable() {}
    protected void onTick(Minecraft client) {}
    public String name() { return name; }
    public String description() { return description; }
    public boolean enabled() { return enabled; }
}
