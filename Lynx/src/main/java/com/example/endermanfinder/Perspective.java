package com.example.endermanfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class Perspective {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean active = false;

    private float savedYaw, savedPitch;
    private float currentYaw, currentPitch;
    private float prevYaw, prevPitch;

    public Perspective() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
            toggle();
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (active) {
            EntityPlayerSP player = mc.thePlayer;
            if (player == null) return;

            // Save original rotations
            float originalYaw = player.rotationYaw;
            float originalPitch = player.rotationPitch;

            // Update player rotation to freelook rotations
            player.rotationYaw = currentYaw;
            player.rotationPitch = currentPitch;

            // Restore original rotations after rendering
            player.rotationYaw = originalYaw;
            player.rotationPitch = originalPitch;
        }
    }

    private void toggle() {
        active = !active;

        if (active) {
            // Save current player rotation when enabling freelook
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                savedYaw = player.rotationYaw;
                savedPitch = player.rotationPitch;

                currentYaw = savedYaw;
                currentPitch = savedPitch;

                prevYaw = savedYaw;
                prevPitch = savedPitch;
            }
        } else {
            // Restore original player rotation when disabling freelook
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                player.rotationYaw = savedYaw;
                player.rotationPitch = savedPitch;
            }
        }
    }

    private void updateRotation(float yawDiff, float pitchDiff) {
        prevYaw = currentYaw;
        prevPitch = currentPitch;

        // Adjust freelook rotations
        currentYaw += yawDiff;
        currentPitch = Math.max(-90, Math.min(90, currentPitch + pitchDiff));
    }
}
