package com.example.endermanfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.util.Random;

public class KeyAndClickActions {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean running = false;  // Tracks if actions are currently running
    private boolean paused = false;   // Tracks if actions should be paused
    private final Random random = new Random();
    private long floridCooldownTime = 0; // Tracks the cooldown for using "Florid"

    // This method starts simulating key presses for using the wand and florid
    public void performActions() {
        running = true;
        new Thread(() -> {
            try {
                while (running) {
                    // Check if the player is paused (in inventory or chat)
                    if (isPlayerPaused()) {
                        paused = true;
                    } else {
                        paused = false;
                    }

                    // Only execute actions if not paused
                    if (!paused) {
                        // Check for "Wand" in hotbar
                        int wandSlot = findWandInHotbar(); // Check for a wand in the hotbar
                        if (wandSlot != -1) {
                            sendKeyPress(wandSlot); // Switch to the wand slot
                            Thread.sleep(getRandomizedDelay(300)); // Delay after switching

                            // Simulate right-click
                            sendRightClick();
                            Thread.sleep(getRandomizedDelay(100));

                            // Now switch to hotbar slot '5' (assuming you still need to use another item)
                            sendKeyPress(4); // 3 is the hotbar slot for '5'
                            Thread.sleep(getRandomizedDelay(100));

                            // Healing wand cooldown
                            Thread.sleep(getRandomizedDelay(6800)); 
                        } else {
                            // Optionally handle case where no wand is found
                            mc.thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §4no healing wand/florid sword found..."));
                            System.out.println("No wand found in hotbar.");
                            Thread.sleep(1500); // Brief pause if no wand is found
                        }

                        // Check for "Florid" in hotbar and player health
                        int floridSlot = findFloridInHotbar(); // Check for florid in the hotbar
                        if (floridSlot != -1 && isHealthBelowThreshold(0.6)) {
                            // Check cooldown
                            if (System.currentTimeMillis() > floridCooldownTime) {
                                sendKeyPress(floridSlot); // Switch to the florid slot
                                Thread.sleep(getRandomizedDelay(300)); // Delay after switching

                                // Simulate right-click twice
                                sendRightClick();
                                Thread.sleep(getRandomizedDelay(100));
                                sendRightClick();
                                Thread.sleep(getRandomizedDelay(100));

                                sendKeyPress(4); // 3 is the hotbar slot for '5'
                                Thread.sleep(getRandomizedDelay(100));

                                // Set cooldown for using florid again
                                floridCooldownTime = System.currentTimeMillis() + 5000; // 15 seconds cooldown
                            } else {
                                System.out.println("Florid cooldown active. Try again later.");
                            }
                        }

                    } else {
                        // Sleep briefly to avoid high CPU usage when paused
                        Thread.sleep(500);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Stop the actions by setting running to false
    public void stopActions() {
        running = false;
    }

    // Simulate pressing a hotbar key (Minecraft hotbar slots start from 0)
    private void sendKeyPress(int hotbarSlot) {
        mc.thePlayer.inventory.currentItem = hotbarSlot;  // Change the player's current item
        System.out.println("Simulated key press for slot: " + (hotbarSlot + 1));  // Hotbar slots are 0-indexed, Minecraft's hotbar is 1-9
    }

    // Simulate right-click using KeyBinding
    private void sendRightClick() {
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());  // Simulate a right-click (use item)
    }

    // This method checks if the player is paused (in inventory or chat)
    private boolean isPlayerPaused() {
        return mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiChat;
    }

    // Find a wand in the hotbar and return its slot index, or -1 if not found
    private int findWandInHotbar() {
        for (int i = 0; i < 9; i++) { // Check slots 0-8 (hotbar)
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getDisplayName().toLowerCase().contains("wand")) {
                return i; // Return the slot index if a wand is found
            }
        }
        return -1; // No wand found
    }

    // Find a item in the hotbar and return its slot index, or -1 if not found
    private int findFloridInHotbar() {
        for (int i = 0; i < 9; i++) { // Check slots 0-8 (hotbar)
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getDisplayName().toLowerCase().contains("florid")) {
                return i;
            }
        }
        return -1;
    }

    // Check if player's health is below a specified threshold (0.4 = 40%)
    private boolean isHealthBelowThreshold(double threshold) {
        return mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth() < threshold; // Check if below threshold
    }

    // Helper method to add random delay (+/- 80ms)
    private int getRandomizedDelay(int baseDelay) {
        return baseDelay + random.nextInt(160) - 80;  // Random delay between -80ms and +80ms
    }
}
