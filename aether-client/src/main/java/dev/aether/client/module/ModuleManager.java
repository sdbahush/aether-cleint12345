package dev.aether.client.module;

import dev.aether.client.scan.BaseSurveyor;
import dev.aether.client.scan.StashRadar;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new SprintAssist());
        modules.add(new StashRadar());
        modules.add(new BaseSurveyor());
    }

    public void tick(Minecraft client) { modules.forEach(module -> module.tick(client)); }
    public List<Module> all() { return Collections.unmodifiableList(modules); }
    public <T extends Module> T get(Class<T> type) {
        return modules.stream().filter(type::isInstance).map(type::cast).findFirst().orElse(null);
    }

    private static final class SprintAssist extends Module {
        private SprintAssist() { super("Sprint Assist", "Keeps sprint active while moving forward."); }
        @Override protected void onTick(Minecraft client) {
            if (client.player != null && client.options.keyUp.isDown()) client.player.setSprinting(true);
        }
    }
}
