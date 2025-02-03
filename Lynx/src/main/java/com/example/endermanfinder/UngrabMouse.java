package com.example.endermanfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;

public class UngrabMouse {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static UngrabMouse instance;

    public static UngrabMouse getInstance() {
        if (instance == null) {
            instance = new UngrabMouse();
        }
        return instance;
    }

    private boolean mouseUngrabbed;
    private MouseHelper oldMouseHelper;

    public void ungrabMouse() {
        if (!Mouse.isGrabbed() || mouseUngrabbed) return;
        mc.gameSettings.pauseOnLostFocus = false;
        oldMouseHelper = mc.mouseHelper;
        oldMouseHelper.ungrabMouseCursor();
        mc.inGameHasFocus = true;
        mc.mouseHelper = new MouseHelper() {
            @Override
            public void mouseXYChange() {
                // Override to prevent default behavior
            }

            @Override
            public void grabMouseCursor() {
                // Override to prevent default behavior
            }

            @Override
            public void ungrabMouseCursor() {
                // Override to prevent default behavior
            }
        };
        mouseUngrabbed = true;
    }

    public void regrabMouse() {
        regrabMouse(false);
    }

    public void regrabMouse(boolean force) {
        if (!mouseUngrabbed && !force) return;
        mc.mouseHelper = oldMouseHelper;
        if (mc.currentScreen == null || force) {
            mc.mouseHelper.grabMouseCursor();
        }
        mouseUngrabbed = false;
    }
}
