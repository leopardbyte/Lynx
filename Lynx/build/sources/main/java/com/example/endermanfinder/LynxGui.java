package com.example.endermanfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;

public class LynxGui extends GuiScreen {
    private CustomDropdownMenu dropdownMenu;
    private EndermanFinder endermanFinder;
    private AntiStuck antiStuck;
    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    private boolean isAutokillerRunning = false;
    private float hue = 0;

    private ToggleButton setting1Toggle;
    private ToggleButton setting2Toggle;
    private ToggleButton setting3Toggle;

    public LynxGui(EndermanFinder endermanFinder) {
        this.endermanFinder = endermanFinder;
        this.antiStuck = new AntiStuck();
    }
                // add more menus for each entity each health specific
    @Override
    public void initGui() {
        this.isAutokillerRunning = endermanFinder.autoWalkEnabled;
        List<String> targets = Arrays.asList("enderman", "zombie", "spider", "wolf", "blaze", "mooshroom", "creeper", "skeleton", "chicken", "cow", "pig", "slime", "sheep", "rabbit", "irongolem", "magmacube", "custom");
        this.dropdownMenu = new CustomDropdownMenu(this.width / 2 - 60, this.height / 2 - 110, 100, 20, targets, this.endermanFinder);
        this.dropdownMenu.selectedOption = endermanFinder.selectedTarget;

        this.buttonList.clear();
        this.buttonList.add(new CustomGuiButton(0, this.width / 2 - 150, this.height / 2 - 170, 80, 20, isAutokillerRunning ? I18n.format("Stop Autokiller") : I18n.format("Start Autokiller")));
        this.buttonList.add(new CustomGuiButton(1, this.width / 2 - 150, this.height / 2 - 110, 80, 20, I18n.format("Select Target")));

        // Add new toggle buttons for settings
        this.setting1Toggle = new ToggleButton(2, this.width / 2 + 330, this.height / 2 - 170, 80, 20, "Auto Heal", endermanFinder.getSetting1State());
        this.setting2Toggle = new ToggleButton(3, this.width / 2 + 330, this.height / 2 - 140, 80, 20, "Draw Path", endermanFinder.isDrawPathEnabled());
        this.setting3Toggle = new ToggleButton(4, this.width / 2 + 330, this.height / 2 - 110, 80, 20, "Setting 3", endermanFinder.getSetting3State());

        this.buttonList.add(this.setting1Toggle);
        this.buttonList.add(this.setting2Toggle);
        this.buttonList.add(this.setting3Toggle);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                startAutokiller((CustomGuiButton) button);
                break;
            case 1:
                this.dropdownMenu.setVisible(!this.dropdownMenu.isVisible());
                break;
            case 2:
                toggleSetting1();
                break;
            case 3:
                toggleSetting2();
                break;
            case 4:
                toggleSetting3();
                break;
        }
    }

    private void startAutokiller(CustomGuiButton button) {
        endermanFinder.setAutoWalkEnabled(!endermanFinder.autoWalkEnabled);
        String status = endermanFinder.autoWalkEnabled ? "§aenabled" : "§cdisabled";
        mc.thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §bAuto-walk " + status));
        isAutokillerRunning = endermanFinder.autoWalkEnabled;
        if (endermanFinder.autoWalkEnabled) {
            button.displayString = I18n.format("Stop Autokiller");
            button.setButtonColor(0x00FF00);
            mc.displayGuiScreen(null);
        } else {
            button.displayString = I18n.format("Start Autokiller");
            button.setButtonColor(0x4169E1);
            //endermanFinder.keyAndClickActions.stopActions();
        }
    }

    private void toggleSetting1() {
        setting1Toggle.toggle();
        endermanFinder.setSetting1State(setting1Toggle.isToggled());
        endermanFinder.toggleKeyAndClickActions();
        //mc.thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §bAuto Heal " + (setting1Toggle.isToggled() ? "enabled" : "disabled")));
    }

    private void toggleSetting2() {
        setting2Toggle.toggle();
        endermanFinder.setDrawPathEnabled(); // Call a method in EndermanFinder
        //mc.thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §bDraw Path " + (setting2Toggle.isToggled() ? "enabled" : "disabled")));
    }

    private void toggleSetting3() {
        setting3Toggle.toggle();
        endermanFinder.setSetting3State(setting3Toggle.isToggled());
        mc.thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §bSetting 3 " + (setting3Toggle.isToggled() ? "enabled" : "disabled")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int rainbow = Color.HSBtoRGB(hue, 1.0F, 1.0F);
        this.drawCenteredString(this.fontRendererObj, I18n.format("Lynx"), this.width / 2, 20, rainbow);
        hue += 0.001F;
        if (hue > 1.0F) hue = 0.0F;
        this.dropdownMenu.drawMenu(mc, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
        }
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.dropdownMenu.handleMouseClick(mouseX, mouseY);
    }

    public static class CustomGuiButton extends GuiButton {
        private int buttonColor = 0x4169E1;

        public CustomGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        public void setButtonColor(int color) {
            this.buttonColor = color;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                FontRenderer fontrenderer = mc.fontRendererObj;
                mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                GlStateManager.color((buttonColor >> 16 & 255) / 255.0F, (buttonColor >> 8 & 255) / 255.0F, (buttonColor & 255) / 255.0F, this.hovered ? 1.0F : 0.8F);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + (this.getHoverState(this.hovered) * 20), this.width, this.height);
                this.mouseDragged(mc, mouseX, mouseY);
                int j = 14737632;
                if (packedFGColour != 0) {
                    j = packedFGColour;
                } else if (!this.enabled) {
                    j = 10526880;
                } else if (this.hovered) {
                    j = 16777120;
                }
                this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
            }
        }
    }

    public static class ToggleButton extends GuiButton {
        private boolean toggled;
        private static final int ROYAL_BLUE = 0x4169E1;

        public ToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, boolean initialState) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.toggled = initialState;
        }

        public void toggle() {
            this.toggled = !this.toggled;
        }

        public boolean isToggled() {
            return this.toggled;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                FontRenderer fontrenderer = mc.fontRendererObj;
                mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                GlStateManager.color((ROYAL_BLUE >> 16 & 255) / 255.0F, (ROYAL_BLUE >> 8 & 255) / 255.0F, (ROYAL_BLUE & 255) / 255.0F, this.hovered ? 1.0F : 0.8F);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + this.getHoverState(this.hovered) * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + this.getHoverState(this.hovered) * 20, this.width / 2, this.height);
                this.mouseDragged(mc, mouseX, mouseY);
                int j = 14737632;
                if (packedFGColour != 0) {
                    j = packedFGColour;
                } else if (!this.enabled) {
                    j = 10526880;
                } else if (this.hovered) {
                    j = 16777120;
                }
                String buttonText = this.displayString + ": " + (this.toggled ? "On" : "Off");
                this.drawCenteredString(fontrenderer, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
            }
        }
    }

    public static class CustomDropdownMenu {
        private int x, y, width, height;
        private List<String> options;
        private boolean visible = false;
        private EndermanFinder endermanFinder;
        public String selectedOption = null;

        public CustomDropdownMenu(int x, int y, int width, int height, List<String> options, EndermanFinder endermanFinder) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.options = options;
            this.endermanFinder = endermanFinder;
        }

        public void drawMenu(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                FontRenderer fontrenderer = mc.fontRendererObj;
                for (int i = 0; i < options.size(); i++) {
                    int optionY = this.y + i * this.height;
                    mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                    if (options.get(i).equals(selectedOption)) {
                        GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                    mc.ingameGUI.drawTexturedModalRect(this.x, optionY, 0, 46, this.width, this.height);
                    fontrenderer.drawString(options.get(i), this.x + 5, optionY + (this.height - 8) / 2, options.get(i).equals(selectedOption) ? 0x00FFFF : 0xFFFFFF);
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return this.visible;
        }

        public void handleMouseClick(int mouseX, int mouseY) {
            if (this.visible) {
                for (int i = 0; i < options.size(); i++) {
                    int optionY = this.y + i * this.height;
                    if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= optionY && mouseY <= optionY + this.height) {
                        String entityType = options.get(i);
                        if (endermanFinder.setTargetEntityType(entityType)) {
                            selectedOption = entityType;
                            endermanFinder.selectedTarget = entityType;
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §bTarget entity set to " + entityType));
                            endermanFinder.updateDesiredHPListForEntity(entityType);
                        } else {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§6§l[Lynx] §cInvalid entity type!"));
                        }
                        this.setVisible(false);
                        break;
                    }
                }
            }
        }
    }
}