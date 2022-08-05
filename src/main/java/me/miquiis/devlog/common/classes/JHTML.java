package me.miquiis.devlog.common.classes;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JHTML {

    public final static int WIDTH_REFERENCE = 1920;
    public final static int HEIGHT_REFERENCE = 1080;

    public static class Canvas {

        public Canvas parent;

        public double width;
        public double height;
        public boolean column;

        public int marginLeft;
        public int marginTop;
        public int paddingLeft;
        public int paddingTop;

        // CSS
        public boolean isFlexWidth;
        public boolean isFlexHeight;
        public boolean isCenteredX;
        public boolean isCenteredY;
        public boolean floatRight;

        public boolean absolutePosition;

        public List<Canvas> child;

        public double nextDrawX;
        public double nextDrawY;

        public double realX;
        public double realY;
        public double realW;
        public double realH;

        public boolean isActive = true;

        public OnRender onRenderEvent = (x, y, w, h) -> {};

        public Canvas(double width, double height, boolean column, Canvas... canvasChild)
        {
            this.width = width;
            this.height = height;
            this.marginLeft = 0;
            this.marginTop = 0;
            this.paddingLeft = 0;
            this.paddingTop = 0;
            this.column = column;
            this.child = new ArrayList<>(Arrays.asList(canvasChild));
        }

        public Canvas(double width, double height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, Canvas... canvasChild)
        {
            this.width = width;
            this.height = height;
            this.marginLeft = marginLeft;
            this.marginTop = marginTop;
            this.paddingLeft = paddingLeft;
            this.paddingTop = paddingTop;
            this.column = column;
            this.child = new ArrayList<>(Arrays.asList(canvasChild));
        }

        public Canvas(double width, double height, Canvas... canvasChild)
        {
            this.width = width;
            this.height = height;
            this.marginLeft = 0;
            this.marginTop = 0;
            this.paddingLeft = 0;
            this.paddingTop = 0;
            this.column = true;
            this.child = new ArrayList<>(Arrays.asList(canvasChild));
        }

        public Canvas(double width, double height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, Canvas... canvasChild)
        {
            this.width = width;
            this.height = height;
            this.marginLeft = marginLeft;
            this.marginTop = marginTop;
            this.paddingLeft = paddingLeft;
            this.paddingTop = paddingTop;
            this.column = true;
            this.child = new ArrayList<>(Arrays.asList(canvasChild));
        }

        public Canvas setOnRenderEvent(OnRender onRenderEvent)
        {
            this.onRenderEvent = onRenderEvent;
            return this;
        }

        protected void renderBackground(MatrixStack matrixStack, Minecraft minecraft, double x, double y)
        {
            if (!isActive) return;
            final MainWindow mainWindow = minecraft.getMainWindow();

            final float resScale = mainWindow.getFramebufferWidth() / (float)WIDTH_REFERENCE;
            final float yResScale = mainWindow.getFramebufferHeight() / (float)HEIGHT_REFERENCE;
            matrixStack.push();
            matrixStack.scale(1f / (float)mainWindow.getGuiScaleFactor() * resScale, 1f / (float)mainWindow.getGuiScaleFactor() * yResScale, 1);
            matrixStack.translate(x, y, 1);

            this.realX = x / 1f / (float)mainWindow.getGuiScaleFactor() * resScale;
            this.realY = y / 1f / (float)mainWindow.getGuiScaleFactor() * yResScale;
            this.realW = width / 1f / (float)mainWindow.getGuiScaleFactor() * resScale;
            this.realH = height / 1f / (float)mainWindow.getGuiScaleFactor() * yResScale;

            renderSelf(matrixStack, minecraft);
            posRenderBackground(matrixStack);
        }

        protected void posRenderBackground(MatrixStack matrixStack)
        {
            matrixStack.pop();
        }

        protected void renderSelf(MatrixStack matrixStack, Minecraft minecraft) {
            onRenderEvent.onRender(realX, realY, realW, realH);
            //AbstractGui.fill(matrixStack, 0, 0, width, height, Color.BLACK.getRGB());
        }

        public void render(MatrixStack matrixStack, Minecraft minecraft, double x, double y)
        {
            double initialX = x + marginLeft;
            double initialY = y + marginTop;

            if (parent != null)
            {
                initialY += parent.paddingTop;
                initialX += parent.paddingLeft;

                if (isCenteredX)
                {
                    initialX = (x + parent.width / 2.0) - (width / 2.0) + marginLeft + parent.paddingLeft;
                }

                if (isCenteredY)
                {
                    initialY = (y + parent.height / 2.0) - (height / 2.0) + marginTop + parent.paddingTop;
                }

                if (floatRight)
                {
                    initialX = (x + parent.width) - (width) + marginLeft + parent.paddingLeft;
                }
            }

            double biggestX = initialX;
            double biggestY = initialY;

            nextDrawX = initialX;
            nextDrawY = initialY;

            renderBackground(matrixStack, minecraft, initialX, initialY);

            for (Canvas child : child) {
                child.setParent(this);

                if (child.isFlexHeight) child.height = (height - paddingTop - child.marginTop);

                if (biggestX < child.width) biggestX = child.width;
                if (biggestY < child.height) biggestY = child.height;
                if (column)
                {
                    if (child.absolutePosition)
                    {
                        child.render(matrixStack, minecraft, initialX, initialY);
                    } else
                    {
                        child.render(matrixStack, minecraft, nextDrawX, nextDrawY);
                        nextDrawY += child.height + child.marginTop;
                    }
                } else
                {
                    if (child.absolutePosition)
                    {
                        child.render(matrixStack, minecraft, initialX, initialY);
                    } else
                    {
                        child.render(matrixStack, minecraft, nextDrawX, nextDrawY);
                        nextDrawX += (child.floatRight ? -1 : 1) * child.width + child.marginLeft;
                    }
                }
            }
        }

        private void setCSS() {
            if (isFlexHeight)
            {
                this.height = getFirstParent().height;
            }
            if (isFlexWidth)
            {
                this.width = getFirstParent().width;
            }
        }

        public Canvas setActive(boolean active)
        {
            this.isActive = active;
            return this;
        }

        public Canvas getFirstParent()
        {
            Canvas firstParent = parent;
            if (firstParent == null) return this;
            while (firstParent.parent != null)
            {
                firstParent = firstParent.parent;
            }
            return firstParent;
        }

        public Canvas setFloatRight()
        {
            this.floatRight = true;
            return this;
        }

        public Canvas setCenteredHorizontally()
        {
            this.isCenteredX = true;
            return this;
        }

        public Canvas setCenteredVertically()
        {
            this.isCenteredY = true;
            return this;
        }

        public Canvas setFlexHeight()
        {
            this.isFlexHeight = true;
            return this;
        }

        public Canvas setAbsolutePosition()
        {
            this.absolutePosition = true;
            return this;
        }

        public void setParent(Canvas parent)
        {
            this.parent = parent;
        }

        public double getWidth()
        {
            return width;
        }

        public double getHeight()
        {
            return height;
        }

        public double getScaledWidth(double guiScaledFactor)
        {
            return getWidth() / guiScaledFactor;
        }

        public double getScaledHeight(double guiScaledFactor)
        {
            return getHeight() / guiScaledFactor;
        }

    }

    public interface OnRender
    {
        public void onRender(double x, double y, double width, double height);
    }

    public static class Box extends Canvas {
        public int color;

        public Box(int width, int height, boolean column, int color, Canvas... canvasChild) {
            super(width, height, column, canvasChild);
            this.color = color;
        }

        public Box(int width, int height, int color, Canvas... canvasChild) {
            super(width, height, canvasChild);
            this.color = color;
        }

        public Box(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, int color, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, canvasChild);
            this.color = color;
        }

        public Box(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, int color, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, canvasChild);
            this.color = color;
        }

        @Override
        protected void renderSelf(MatrixStack matrixStack, Minecraft minecraft) {
            super.renderSelf(matrixStack, minecraft);
            AbstractGui.fill(matrixStack, 0, 0, (int)getWidth(), (int)getHeight(), color);
        }
    }

    public static class UVImage extends Image {

        public int uWidth;
        public int vHeight;

        public UVImage(int width, int height, boolean column, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild) {
            super(width, height, column, texture, canvasChild);
            this.uWidth = uWidth;
            this.vHeight = vHeight;
        }

        public UVImage(int width, int height, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild) {
            super(width, height, texture, canvasChild);
            this.uWidth = uWidth;
            this.vHeight = vHeight;
        }

        public UVImage(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, texture, canvasChild);
            this.uWidth = uWidth;
            this.vHeight = vHeight;
        }

        public UVImage(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, texture, canvasChild);
            this.uWidth = uWidth;
            this.vHeight = vHeight;
        }

        @Override
        protected void renderSelf(MatrixStack matrixStack, Minecraft minecraft) {
            onRenderEvent.onRender(realX, realY, realW, realH);
            //super.renderSelf(matrixStack, minecraft);
            minecraft.getTextureManager().bindTexture(texture);
            RenderSystem.enableBlend();
            matrixStack.scale(2, 2, 2);
            GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, 32, 32, 32, 32, 1);
            RenderSystem.disableBlend();
        }
    }

    public static class Image extends Canvas {
        public ResourceLocation texture;

        public Image(int width, int height, boolean column, ResourceLocation texture, Canvas... canvasChild) {
            super(width, height, column, canvasChild);
            this.texture = texture;
        }

        public Image(int width, int height, ResourceLocation texture, Canvas... canvasChild) {
            super(width, height, canvasChild);
            this.texture = texture;
        }

        public Image(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, ResourceLocation texture, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, canvasChild);
            this.texture = texture;
        }

        public Image(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, ResourceLocation texture, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, canvasChild);
            this.texture = texture;
        }

        @Override
        protected void renderSelf(MatrixStack matrixStack, Minecraft minecraft) {
            super.renderSelf(matrixStack, minecraft);
            minecraft.getTextureManager().bindTexture(texture);
            RenderSystem.enableBlend();
            AbstractGui.blit(matrixStack, 0, 0, 0, 0, (int)width, (int)height, (int)width, (int)height);
            RenderSystem.disableBlend();
        }
    }

    public static class Item extends Canvas {
        public ItemStack itemStack;
        public float scale;

        public Item(int width, int height, boolean column, ItemStack itemStack, Canvas... canvasChild) {
            super(width, height, column, canvasChild);
            this.itemStack = itemStack;
        }

        public Item(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, ItemStack itemStack, float scale, Canvas... canvasChild) {
            super(width, height, marginLeft, marginTop, paddingLeft, paddingTop, canvasChild);
            this.itemStack = itemStack;
            this.scale = width / 16f;
        }

        @Override
        protected void renderSelf(MatrixStack matrixStack, Minecraft minecraft) {
            super.renderSelf(matrixStack, minecraft);
            matrixStack.push();
            matrixStack.scale(scale, scale, 1f);
            renderItemModelIntoGUI(itemStack, minecraft, matrixStack, nextDrawX, nextDrawY, 0, 0,  minecraft.getItemRenderer().getItemModelWithOverrides(itemStack, (World)null, (LivingEntity)null));
            matrixStack.pop();
        }
    }

    public static class Text extends Canvas {
        public String text;
        public float scale;
        public boolean hasShadow;
        public int color;

        public Text(double width, String text, float scale, Canvas... canvasChild)
        {
            super(Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * scale, canvasChild);
            this.text = text;
            this.scale = scale;
            this.hasShadow = hasShadow;
            this.color = Color.WHITE.getRGB();
        }

        public Text(int width, boolean column, String text, float scale, Canvas... canvasChild) {
            super(Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * scale, column, canvasChild);
            this.text = text;
            this.scale = scale;
            this.hasShadow = hasShadow;
            this.color = Color.WHITE.getRGB();
        }

        public Text(int width, String text, float scale, Canvas... canvasChild) {
            super(Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * scale, canvasChild);
            this.text = text;
            this.scale = scale;
            this.hasShadow = hasShadow;
            this.color = Color.WHITE.getRGB();
        }

        public Text(int width, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, String text, float scale, Canvas... canvasChild) {
            super(Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * scale, marginLeft, marginTop, paddingLeft, paddingTop, column, canvasChild);
            this.text = text;
            this.scale = scale;
            this.hasShadow = hasShadow;
            this.color = Color.WHITE.getRGB();
        }

        public Text(int width, int marginLeft, int marginTop, int paddingLeft, int paddingTop, String text, float scale, Canvas... canvasChild) {
            super(Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * scale, marginLeft, marginTop, paddingLeft, paddingTop, canvasChild);
            this.text = text;
            this.scale = scale;
            this.hasShadow = hasShadow;
            this.color = Color.WHITE.getRGB();
        }

        public Text(int width, int marginLeft, int marginTop, int paddingLeft, int paddingTop, String text, float scale, boolean hasShadow, int color, Canvas... canvasChild) {
            super(Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale, Minecraft.getInstance().fontRenderer.FONT_HEIGHT * scale, marginLeft, marginTop, paddingLeft, paddingTop, canvasChild);
            this.text = text;
            this.scale = scale;
            this.hasShadow = hasShadow;
            this.color = color;
        }

        @Override
        protected void renderSelf(MatrixStack matrixStack, Minecraft minecraft) {
            super.renderSelf(matrixStack, minecraft);
            matrixStack.push();
            matrixStack.scale(scale, scale, 0);
            if (hasShadow)
                minecraft.fontRenderer.drawStringWithShadow(matrixStack, text, 0, 0, color);
            else
                minecraft.fontRenderer.drawString(matrixStack, text, 0, 0, color);
            matrixStack.pop();
        }
    }

    public static Text Text(int width, String text, float scale, Canvas... canvasChild)
    {
        return new Text(width, text, scale, canvasChild);
    }

    public static Text Text(int width, boolean column, String text, float scale, Canvas... canvasChild)
    {
        return new Text(width, column, text, scale, canvasChild);
    }

    public static Text Text(int width, int marginLeft, int marginTop, int paddingLeft, int paddingTop, String text, float scale, Canvas... canvasChild)
    {
        return new Text(width, marginLeft, marginTop, paddingLeft, paddingTop, text, scale, canvasChild);
    }

    public static Text Text(int width, int marginLeft, int marginTop, int paddingLeft, int paddingTop, String text, float scale, boolean hasShadown, int color, Canvas... canvasChild)
    {
        return new Text(width, marginLeft, marginTop, paddingLeft, paddingTop, text, scale, hasShadown, color, canvasChild);
    }

    public static Text Text(int width, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, String text, float scale, Canvas... canvasChild) {
        return new Text(width, marginLeft, marginTop, paddingLeft, paddingTop, column, text, scale, canvasChild);
    }

    public static Item Item(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, ItemStack itemStack, float scale, Canvas... canvasChild)
    {
        return new Item(width, height, marginLeft, marginTop, paddingLeft, paddingTop, itemStack, scale, canvasChild);
    }

    public static Image Image(int width, int height, boolean column, ResourceLocation texture, Canvas... canvasChild)
    {
        return new Image(width, height, column, texture, canvasChild);
    }

    public static Image Image(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, ResourceLocation texture, Canvas... canvasChild)
    {
        return new Image(width, height, marginLeft, marginTop, paddingLeft, paddingTop, texture, canvasChild);
    }

    public static Image Image(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, ResourceLocation texture, Canvas... canvasChild)
    {
        return new Image(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, texture, canvasChild);
    }

    public static Image Image(int width, int height, ResourceLocation texture, Canvas... canvasChild)
    {
        return new Image(width, height, texture, canvasChild);
    }

    public static UVImage UVImage(int width, int height, boolean column, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild)
    {
        return new UVImage(width, height, column, texture, uWidth, vHeight, canvasChild);
    }

    public static UVImage UVImage(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild)
    {
        return new UVImage(width, height, marginLeft, marginTop, paddingLeft, paddingTop, texture, uWidth, vHeight, canvasChild);
    }

    public static UVImage UVImage(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild)
    {
        return new UVImage(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, texture, uWidth, vHeight, canvasChild);
    }

    public static UVImage UVImage(int width, int height, ResourceLocation texture, int uWidth, int vHeight, Canvas... canvasChild)
    {
        return new UVImage(width, height, texture, uWidth, vHeight, canvasChild);
    }

    public static Box Box(int width, int height, boolean column, int color, Canvas... canvasChild)
    {
        return new Box(width, height, column, color, canvasChild);
    }

    public static Box Box(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, int color, Canvas... canvasChild)
    {
        return new Box(width, height, marginLeft, marginTop, paddingLeft, paddingTop, color, canvasChild);
    }

    public static Box Box(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, int color, Canvas... canvasChild)
    {
        return new Box(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, color, canvasChild);
    }

    public static Box Box(int width, int height, int color, Canvas... canvasChild)
    {
        return new Box(width, height, color, canvasChild);
    }

    public static Canvas Canvas(int width, int height, boolean column, Canvas... canvasChild)
    {
        return new Canvas(width, height, column, canvasChild);
    }

    public static Canvas Canvas(int width, int height, Canvas... canvasChild)
    {
        return new Canvas(width, height, canvasChild);
    }

    public static Canvas Canvas(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, boolean column, Canvas... canvasChild)
    {
        return new Canvas(width, height, marginLeft, marginTop, paddingLeft, paddingTop, column, canvasChild);
    }

    public static Canvas Canvas(int width, int height, int marginLeft, int marginTop, int paddingLeft, int paddingTop, Canvas... canvasChild)
    {
        return new Canvas(width, height, marginLeft, marginTop, paddingLeft, paddingTop, canvasChild);
    }

    private static void renderItemModelIntoGUI(ItemStack stack, Minecraft minecraft, MatrixStack matrixStack, double x, double y, double scaleX, double scaleY, IBakedModel bakedmodel) {
        matrixStack.push();
        minecraft.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        minecraft.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.translate(0, 0, 100.0F + 100);
        matrixStack.translate(8.0F, 8.0F, 0.0F);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !bakedmodel.isSideLit();
        if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        minecraft.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.finish();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        matrixStack.pop();
    }


}