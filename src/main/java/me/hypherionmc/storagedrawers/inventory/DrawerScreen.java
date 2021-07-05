package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.client.renderer.StorageRenderItem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.BaseText;
import net.minecraft.util.Identifier;

import java.util.List;

public class DrawerScreen extends HandledScreen<ContainerDrawers>
{
    private static final Identifier guiTextures1 = new Identifier(StorageDrawers.MOD_ID, "textures/gui/drawers_1.png");
    private static final Identifier guiTextures2 = new Identifier(StorageDrawers.MOD_ID, "textures/gui/drawers_2.png");
    private static final Identifier guiTextures4 = new Identifier(StorageDrawers.MOD_ID, "textures/gui/drawers_4.png");
    private static final Identifier guiTexturesComp = new Identifier(StorageDrawers.MOD_ID, "textures/gui/drawers_comp.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;

    private static StorageRenderItem storageItemRender;

    private final Identifier background;

    public DrawerScreen(ContainerDrawers container, PlayerInventory playerInv, BaseText name, Identifier bg) {
        super(container, playerInv, name);

        backgroundWidth = 176;
        backgroundHeight = 199;
        background = bg;
    }

    public static class Slot1 extends DrawerScreen {
        public Slot1(ContainerDrawers container, PlayerInventory playerInv, BaseText name) {
            super(container, playerInv, name, guiTextures1);
        }
    }

    public static class Slot2 extends DrawerScreen {
        public Slot2(ContainerDrawers container, PlayerInventory playerInv, BaseText name) {
            super(container, playerInv, name, guiTextures2);
        }
    }

    public static class Slot4 extends DrawerScreen {
        public Slot4(ContainerDrawers container, PlayerInventory playerInv, BaseText name) {
            super(container, playerInv, name, guiTextures4);
        }
    }

    public static class Compacting extends DrawerScreen {
        public Compacting(ContainerDrawers container, PlayerInventory playerInv, BaseText name) {
            super(container, playerInv, name, guiTexturesComp);
        }
    }

    @Override
    protected void init () {
        super.init();

        if (storageItemRender == null) {
            ItemRenderer defaultRenderItem = client.getItemRenderer();
            storageItemRender = new StorageRenderItem(client.getTextureManager(), defaultRenderItem.getModels().getModelManager(), client.getBlockColors());
        }
    }

    @Override
    public void render (MatrixStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        ItemRenderer ri = setItemRender(storageItemRender);
        handler.activeRenderItem = storageItemRender;

        this.renderBackground(stack);
        super.render(stack, p_render_1_, p_render_2_, p_render_3_);
        this.drawMouseoverTooltip(stack, p_render_1_, p_render_2_);

        handler.activeRenderItem = null;
        storageItemRender.overrideStack = ItemStack.EMPTY;

        setItemRender(ri);
    }

    @Override
    protected void drawForeground (MatrixStack stack, int mouseX, int mouseY) {
        this.textRenderer.draw(stack, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(stack, I18n.translate("container.storagedrawers.upgrades"), 8, 75, 4210752);
        this.textRenderer.draw(stack, this.playerInventory.getDisplayName().getString(), 8, this.backgroundHeight - 96 + 2, 4210752);
    }

    @Override
    protected void drawBackground (MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1, 1, 1, 1);

        client.getTextureManager().bindTexture(background);

        int guiX = (width - backgroundWidth) / 2;
        int guiY = (height - backgroundHeight) / 2;
        drawTexture(stack, guiX, guiY, 0, 0, backgroundWidth, backgroundHeight);

        List<Slot> storageSlots = handler.getStorageSlots();
        for (Slot slot : storageSlots) {
            drawTexture(stack, guiX + slot.x, guiY + slot.y, smDisabledX, smDisabledY, 16, 16);
        }

        /*List<Slot> upgradeSlots = container.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack())
                blit(guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16);
        }*/
    }



    @Override
    protected boolean isPointWithinBounds (int x, int y, int width, int height, double originX, double originY) {
        List<Slot> storageSlots = handler.getStorageSlots();
        for (Slot slot : storageSlots) {
            if (slot instanceof SlotStorage && slot.x == x && slot.y == y)
                return false;
        }

        /*List<Slot> upgradeSlots = container.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack() && slot.xPos == x && slot.yPos == y)
                return false;
        }*/

        return super.isPointWithinBounds(x, y, width, height, originX, originY);
    }

    private ItemRenderer setItemRender (ItemRenderer renderItem) {
        ItemRenderer prev = itemRenderer;
        itemRenderer = renderItem;

        return prev;
    }
}