package com.example.endermanfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AntiStuck {
    private final Minecraft mc = Minecraft.getMinecraft();
    private long lastCheckTime;
    private final long CHECK_INTERVAL = 500;
    private BlockPos lastPosition;
    private volatile boolean isCheckingStuck = false;
    private volatile boolean isRunning = false;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;

    public AntiStuck() {
        this.lastCheckTime = System.currentTimeMillis();
        this.lastPosition = null;
    }

    public synchronized void start() {
        if (!isRunning) {
            isRunning = true;
            if (scheduledTask == null || scheduledTask.isDone()) {
                scheduledTask = scheduler.scheduleAtFixedRate(this::checkIfStuck, 0, CHECK_INTERVAL, TimeUnit.MILLISECONDS);
            }
        }
    }

    public synchronized void stop() {
        isRunning = false;
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTask = null;
        }
    }

    public void shutdown() {
        stop();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private void checkIfStuck() {
        if (!isRunning || isCheckingStuck || mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        isCheckingStuck = true;
        BlockPos currentPosition = mc.thePlayer.getPosition();
        long currentTime = System.currentTimeMillis();

        if (lastPosition != null && (Math.abs(currentPosition.getX() - lastPosition.getX()) >= 2 ||
                Math.abs(currentPosition.getZ() - lastPosition.getZ()) >= 2)) {
            lastCheckTime = currentTime;
            lastPosition = currentPosition;
            isCheckingStuck = false;
            return;
        }

        if (lastPosition != null && currentTime - lastCheckTime >= 2000) {
            if (isRunning) {
                if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
                    handleWaterOrLavaStuck();
                } else {
                    mc.addScheduledTask(() -> {
                        if (isRunning) {
                            mc.thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §cyou got stuck. antistuck not implemented yet :c"));
                        }
                    });
                }
            }
            lastCheckTime = currentTime;
        }

        lastPosition = currentPosition;
        isCheckingStuck = false;
    }

    private void handleWaterOrLavaStuck() {
        if (!isRunning) return;

        mc.addScheduledTask(() -> {
            if (!isRunning) return;

            int previousSlot = mc.thePlayer.inventory.currentItem;
            int slotIndex = findHotbarItem(new String[]{"grappling", "aspect"});

            if (slotIndex != -1) {
                mc.thePlayer.inventory.currentItem = slotIndex;
                sendRightClick();
                delay(200);
                sendRightClick();
                mc.thePlayer.inventory.currentItem = previousSlot;
            }
        });
    }

    private int findHotbarItem(String[] keywords) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                String displayName = itemStack.getDisplayName().toLowerCase();
                for (String keyword : keywords) {
                    if (displayName.contains(keyword)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private void sendRightClick() {
        if (!isRunning) return;
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
    }

    private void delay(int milliseconds) {
        // Use a single sleep instead of busy-waiting loop to reduce CPU usage
        if (!isRunning) return;
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}