package me.hypherionmc.storagedrawers.client.renderer;

import me.hypherionmc.storagedrawers.inventory.ItemStackHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Environment(EnvType.CLIENT)
public class StorageRenderItem extends ItemRenderer
{
    private ItemRenderer parent;

    @Nonnull
    public ItemStack overrideStack;

    public StorageRenderItem (TextureManager texManager, BakedModelManager modelManager, ItemColors colors) {
        super(texManager, modelManager, colors);
        parent = MinecraftClient.getInstance().getItemRenderer();
        overrideStack = ItemStack.EMPTY;
    }

    @Override
    public ItemModels getModels() {
        return parent.getModels();
    }

    @Override
    public void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model) {
        parent.renderItem(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
    }

    @Override
    public void renderItem(ItemStack stack, ModelTransformation.Mode transformationType, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        parent.renderItem(stack, transformationType, light, overlay, matrices, vertexConsumers);
    }

    @Override
    public void renderItem(@org.jetbrains.annotations.Nullable LivingEntity entity, ItemStack item, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @org.jetbrains.annotations.Nullable World world, int light, int overlay) {
        parent.renderItem(entity, item, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay);
    }


    public void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
        parent.renderQuads(matrixStackIn, bufferIn, quadsIn, itemStackIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public BakedModel getHeldItemModel(ItemStack stack, @org.jetbrains.annotations.Nullable World world, @org.jetbrains.annotations.Nullable LivingEntity entity) {
        return parent.getHeldItemModel(stack, world, entity);
    }

    @Override
    public void renderGuiItemIcon(ItemStack stack, int x, int y) {
        parent.renderGuiItemIcon(stack, x, y);
    }

    @Override
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
        parent.renderGuiItemOverlay(renderer, stack, x, y);
    }

    @Override
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        if (stack != overrideStack) {
            super.renderGuiItemOverlay(renderer, stack, x, y, countLabel);
            return;
        }

        if (!stack.isEmpty())
        {
            stack = ItemStackHelper.decodeItemStack(stack);

            float scale = .5f;
            float xoff = 0;
            //if (font.getUnicodeFlag()) {
            //    scale = 1f;
            //    xoff = 1;
            //}

            int stackSize = stack.getCount();
            if (ItemStackHelper.isStackEncoded(stack))
                stackSize = 0;

            MatrixStack matrixstack = new MatrixStack();
            if (stackSize >= 0 || countLabel != null) {
                if (stackSize >= 100000000)
                    countLabel = (countLabel == null) ? String.format("%.0fM", stackSize / 1000000f) : countLabel;
                else if (stackSize >= 1000000)
                    countLabel = (countLabel == null) ? String.format("%.1fM", stackSize / 1000000f) : countLabel;
                else if (stackSize >= 100000)
                    countLabel = (countLabel == null) ? String.format("%.0fK", stackSize / 1000f) : countLabel;
                else if (stackSize >= 10000)
                    countLabel = (countLabel == null) ? String.format("%.1fK", stackSize / 1000f) : countLabel;
                else
                    countLabel = (countLabel == null) ? String.valueOf(stackSize) : countLabel;

                int textX = (int) ((x + 16 + xoff - renderer.getWidth(countLabel) * scale) / scale) - 1;
                int textY = (int) ((y + 16 - 7 * scale) / scale) - 1;

                int color = 16777215;
                if (stackSize == 0)
                    color = (255 << 16) | (96 << 8) | (96);

                matrixstack.scale(scale, scale, 1);
                matrixstack.translate(0.0D, 0.0D, (double) (this.zOffset + 200.0F));
                IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                renderer.draw(countLabel, textX, textY, color, true, matrixstack.peek().getNormal(), buffer, false, 0, 15728880);
                buffer.finish();
            }

            if (stack.getItem().showDurabilityBar(stack)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = stack.getItem().getRGBDurabilityForDisplay(stack);
                this.draw(bufferbuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.draw(bufferbuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldownTracker().getCooldown(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
            if (f3 > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                this.draw(bufferbuilder1, x, y + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    @Override
    public void renderInGuiWithOverrides(ItemStack stack, int x, int y) {
        parent.renderInGuiWithOverrides(stack, x, y);
    }

    @Override
    public void renderInGuiWithOverrides(LivingEntity entity, ItemStack stack, int x, int y) {
        parent.renderInGuiWithOverrides(entity, stack, x, y);
    }

    @Override
    public void reload(ResourceManager manager) {
        parent.reload(manager);
    }

    private void draw (BufferBuilder tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        tessellator.pos(x + 0, y + 0, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + 0, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + 0, 0).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();
    }
}
