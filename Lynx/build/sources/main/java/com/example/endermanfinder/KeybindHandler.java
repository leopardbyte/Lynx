package com.example.endermanfinder;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;

public class KeybindHandler {

    private static EndermanFinder endermanFinder = new EndermanFinder();
    //private static Freelook freelook = Freelook.getInstance();

    // Define the custom keybindings
    private static final KeyBinding openGuiKeybind = new KeyBinding(
        "GUI", // Description
        Keyboard.KEY_COMMA, // Default key
        "Lynx" // Category
    );

    private static final KeyBinding freelookKeybind = new KeyBinding(
        "Freelook", // Description
        Keyboard.KEY_M, // Default key
        "Lynx" // Category
    );

    public static void init() {
        // Register the keybindings with Minecraft
        ClientRegistry.registerKeyBinding(openGuiKeybind);
        ClientRegistry.registerKeyBinding(freelookKeybind);
        // Register the keybinding event
        MinecraftForge.EVENT_BUS.register(new KeybindHandler());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Check if the custom keybindings are pressed
        if (openGuiKeybind.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new LynxGui(endermanFinder));
        }
        if (freelookKeybind.isPressed()) {
            //freelook.toggle();
        }
    }
}
