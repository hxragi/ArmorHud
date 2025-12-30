package dev.hxragi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorHudRenderer {

    private final Minecraft minecraft;

    private static final int HOTBAR_TEX_W = 182;
    private static final int HOTBAR_TEX_H = 22;

    private static final int CAP_W = 1;
    private static final int SLOT_W = 20;
    private static final int SLOT_H = 22;

    private static final int ITEM_INSET_X = 2;
    private static final int ITEM_INSET_Y = 3;

    private static final int OFFSET_FROM_HOTBAR_RIGHT = 10;

    private static final ResourceLocation HOTBAR_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/hotbar.png");

    private static final int SLOT_U = 1 + 4 * 20;
    private static final int SLOT_V = 0;

    private static final EquipmentSlot[] ARMOR_SLOTS_RENDER = {
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    };

    public ArmorHudRenderer() {
        this.minecraft = Minecraft.getInstance();
    }

    public void render(GuiGraphics graphics) {
        if (minecraft.player == null || minecraft.options.hideGui || minecraft.screen != null) return;

        Player player = minecraft.player;
        Font font = minecraft.font;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        List<ItemStack> equippedArmor = new ArrayList<>();
        for (EquipmentSlot slot : ARMOR_SLOTS_RENDER) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                equippedArmor.add(stack);
            }
        }

        if (equippedArmor.isEmpty()) return;

        int hotbarX = screenWidth / 2 - (HOTBAR_TEX_W / 2);
        int hotbarY = screenHeight - HOTBAR_TEX_H;
        int hotbarRightX = hotbarX + HOTBAR_TEX_W;

        int durabilityY = hotbarY - font.lineHeight - 2;

        int slotsCount = equippedArmor.size();
        int totalWidth = CAP_W + slotsCount * SLOT_W + CAP_W;

        int startX = hotbarRightX + OFFSET_FROM_HOTBAR_RIGHT;
        if (startX + totalWidth > screenWidth) startX = screenWidth - totalWidth;

        renderHotbarStrip(graphics, startX, hotbarY, slotsCount);

        for (int i = 0; i < slotsCount; i++) {
            int slotX = startX + CAP_W + i * SLOT_W;
            renderArmorContent(graphics, font, equippedArmor.get(i), slotX, hotbarY, durabilityY);
        }
    }

    private void renderHotbarStrip(GuiGraphics graphics, int x, int y, int slotsCount) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                HOTBAR_TEXTURE,
                x, y,
                0f, 0f,
                CAP_W, SLOT_H,
                HOTBAR_TEX_W, HOTBAR_TEX_H
        );

        for (int i = 0; i < slotsCount; i++) {
            int dx = x + CAP_W + i * SLOT_W;
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_TEXTURE,
                    dx, y,
                    (float) SLOT_U, (float) SLOT_V,
                    SLOT_W, SLOT_H,
                    HOTBAR_TEX_W, HOTBAR_TEX_H
            );
        }

        int rightX = x + CAP_W + slotsCount * SLOT_W;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                HOTBAR_TEXTURE,
                rightX, y,
                (float) (HOTBAR_TEX_W - CAP_W), 0f,
                CAP_W, SLOT_H,
                HOTBAR_TEX_W, HOTBAR_TEX_H
        );
    }

    private void renderArmorContent(GuiGraphics graphics, Font font, ItemStack stack,
                                    int slotX, int slotY, int durabilityY) {
        if (stack.isEmpty()) return;

        graphics.renderItem(stack, slotX + ITEM_INSET_X, slotY + ITEM_INSET_Y);
        graphics.renderItemDecorations(font, stack, slotX + ITEM_INSET_X, slotY + ITEM_INSET_Y);

        if (stack.isDamageableItem() && stack.isDamaged()) {
            int max = stack.getMaxDamage();
            int current = max - stack.getDamageValue();
            String text = String.valueOf(current);

            int textX = slotX + (SLOT_W - font.width(text)) / 2;
            int color = getDurabilityColorARGB(current, max);

            graphics.drawString(font, text, textX + 1, durabilityY + 1, 0xFF000000, false);
            graphics.drawString(font, text, textX, durabilityY, color, false);
        }
    }

    private int getDurabilityColorARGB(int current, int max) {
        float ratio = Math.max(0.0f, Math.min(1.0f, (float) current / (float) max));
        int red = (int) ((1.0f - ratio) * 255.0f);
        int green = (int) (ratio * 255.0f);
        return 0xFF000000 | (red << 16) | (green << 8);
    }
}