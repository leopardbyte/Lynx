package com.example.endermanfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OverlayCounter {
    private volatile int killCount = 0; // Thread-safe kill count
    private volatile double hourAverage = 0.0; // Thread-safe average
    private long startTime;

    public OverlayCounter() {
        this.startTime = System.currentTimeMillis();
        MinecraftForge.EVENT_BUS.register(this);

        // Start a background thread for hourly average calculation
        new Thread(() -> {
            while (true) {
                try {
                    updateHourlyAverage();
                    Thread.sleep(1000); // Update every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break; // Exit thread if interrupted
                }
            }
        }, "OverlayCounter-Thread").start();
    }

    private void updateHourlyAverage() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        double hours = elapsedTime / 3600000.0; // Convert milliseconds to hours
        hourAverage = (hours > 0) ? killCount / hours : 0;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaled = new ScaledResolution(mc);

        // Screen dimensions
        int screenWidth = scaled.getScaledWidth();

        // Draw kill count and hourly average
        String killText = "Kills: " + killCount;
        String averageText = String.format("Kills/Hour: %.2f", hourAverage);

        mc.fontRendererObj.drawString(killText, screenWidth - mc.fontRendererObj.getStringWidth(killText) - 10, 10, 0xFFFFFF);
        mc.fontRendererObj.drawString(averageText, screenWidth - mc.fontRendererObj.getStringWidth(averageText) - 10, 20, 0xFFFFFF);
    }

    @SubscribeEvent
    public void onEntityKill(LivingDeathEvent event) {
        if (event.source != null) {
            // Direct damage detection
            if (event.source.getEntity() == Minecraft.getMinecraft().thePlayer) {
                killCount++;
                return;
            }

            // Projectile detection (e.g., arrows)
            if (event.source.getEntity() instanceof net.minecraft.entity.projectile.EntityArrow) {
                net.minecraft.entity.projectile.EntityArrow arrow = (net.minecraft.entity.projectile.EntityArrow) event.source.getEntity();
                if (arrow.shootingEntity == Minecraft.getMinecraft().thePlayer) {
                    killCount++;
                    return;
                }
            }

            // Add more checks here for pets, explosions, etc., if necessary
        }
    }
}
