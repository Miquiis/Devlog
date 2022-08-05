package me.miquiis.devlog.common.classes;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OldEasyGUI {

    public enum HAnchor {
        LEFT,
        RIGHT,
        CENTER
    }

    public enum VAnchor {
        TOP,
        BOTTOM,
        CENTER
    }

    public static class Anchor {
        public HAnchor horizontalAnchor;
        public VAnchor verticalAnchor;

        public Anchor(VAnchor verticalAnchor, HAnchor horizontalAnchor)
        {
            this.horizontalAnchor = horizontalAnchor;
            this.verticalAnchor = verticalAnchor;
        }

        public Anchor(HAnchor horizontalAnchor)
        {
            this(null, horizontalAnchor);
        }

        public Anchor(VAnchor verticalAnchor)
        {
            this(verticalAnchor, null);
        }

    }

    public static class GUIElement {
        public MatrixStack elementMatrix;
        public MainWindow elementWindow;

        public Anchor elementAnchor;

        public int elementWidth;
        public int elementHeight;
        public float elementPosX;
        public float elementPosY;
        public float elementScale;
        public boolean elementIsScalable;
        public int elementColor;

        public float elementFixedPosX;
        public float elementFixedPosY;

        public List<GUIElement> elementChildren;

        public GUIElement(Anchor elementAnchor, MatrixStack elementMatrix, MainWindow elementWindow, float elementPosX, float elementPosY, int elementWidth, int elementHeight, float elementScale, boolean elementIsScalable, int elementColor)
        {
            this.elementMatrix = elementMatrix;
            this.elementWindow = elementWindow;
            this.elementAnchor = elementAnchor;
            this.elementPosX = elementPosX;
            this.elementPosY = elementPosY;
            this.elementWidth = elementWidth;
            this.elementHeight = elementHeight;
            this.elementScale = elementScale;
            this.elementIsScalable = elementIsScalable;
            this.elementColor = elementColor;
            this.elementChildren = new ArrayList<>();
        }

        public GUIElement(GUIElement parent, Anchor elementAnchor, float elementPosX, float elementPosY, int elementWidth, int elementHeight, float elementScale, boolean elementIsScalable, int elementColor)
        {
            this(elementAnchor, parent.elementMatrix, parent.elementWindow, elementPosX, elementPosY, elementWidth, elementHeight, elementScale, elementIsScalable, elementColor);
        }

        public void render(@Nullable GUIElement parent)
        {
            if (elementIsScalable && elementWindow.getGuiScaleFactor() > 1)
                elementScale = elementScale;
            else
                elementScale /= elementWindow.getGuiScaleFactor();

            if (parent != null)
            {
                float anchorPointX = parent.getScaledFixedX();
                float anchorPointY = parent.getScaledFixedY();

                elementFixedPosX = getHorizontalAnchor(elementAnchor, anchorPointX, parent.getScaledWidth(), elementScale) + getSelfHorizontalAnchor(elementAnchor, elementWidth) + getScaledOffset(elementAnchor.horizontalAnchor, elementPosX, elementScale, elementWindow.getGuiScaleFactor());
                elementFixedPosY = getVerticalAnchor(elementAnchor, anchorPointY, parent.getScaledHeight(), elementScale) + getSelfVerticalAnchor(elementAnchor, elementHeight) + getScaledOffset(elementAnchor.verticalAnchor, elementPosY, elementScale, elementWindow.getGuiScaleFactor());
            } else {
                elementFixedPosX = getHorizontalAnchor(elementAnchor, elementWindow.getScaledWidth(), elementScale) + getSelfHorizontalAnchor(elementAnchor, elementWidth) + getScaledOffset(elementAnchor.horizontalAnchor, elementPosX, elementScale, elementWindow.getGuiScaleFactor());
                elementFixedPosY = getVerticalAnchor(elementAnchor, elementWindow.getScaledHeight(), elementScale) + getSelfVerticalAnchor(elementAnchor, elementHeight) + getScaledOffset(elementAnchor.verticalAnchor, elementPosY, elementScale, elementWindow.getGuiScaleFactor());
            }

            elementMatrix.push();
            elementMatrix.scale(elementScale, elementScale, elementScale);
            elementMatrix.translate(elementFixedPosX,elementFixedPosY,0);
            selfRender();

            this.elementChildren.forEach(guiElement -> guiElement.render(this));
        }

        protected void selfRender()
        {
            elementMatrix.pop();
        }

        public float getScaledFixedX()
        {
            return this.elementFixedPosX * this.elementScale;
        }

        public float getScaledFixedY()
        {
            return this.elementFixedPosY * this.elementScale;
        }

        public float getScaledWidth()
        {
            return this.elementWidth * this.elementScale;
        }

        public float getScaledHeight()
        {
            return this.elementHeight * this.elementScale;
        }

        public GUIElement assignChildren(GUIElement guiElement)
        {
            this.elementChildren.add(guiElement);
            return this;
        }

        public GUIElement assignChildren(GUIElement... guiElements)
        {
            this.elementChildren.addAll(new ArrayList<>(Arrays.asList(guiElements)));
            return this;
        }

    }

    public static class BoxGUIElement extends GUIElement {

        public BoxGUIElement(Anchor elementAnchor, MatrixStack elementMatrix, MainWindow elementWindow, float elementPosX, float elementPosY, int elementWidth, int elementHeight, float elementScale, boolean elementIsScalable, int elementColor) {
            super(elementAnchor, elementMatrix, elementWindow, elementPosX, elementPosY, elementWidth, elementHeight, elementScale, elementIsScalable, elementColor);
        }

        public BoxGUIElement(GUIElement parent, Anchor elementAnchor, float elementPosX, float elementPosY, int elementWidth, int elementHeight, float elementScale, boolean elementIsScalable, int elementColor) {
            super(parent, elementAnchor, elementPosX, elementPosY, elementWidth, elementHeight, elementScale, elementIsScalable, elementColor);
        }

        @Override
        protected void selfRender() {
            AbstractGui.fill(elementMatrix, 0, 0, elementWidth, elementHeight, elementColor);
            super.selfRender();
        }
    }

    public static class StringGUIElement extends GUIElement {

        public FontRenderer elementFontRenderer;
        public String elementString;
        public boolean elementHasShadow;

        public StringGUIElement(Anchor elementAnchor, MatrixStack elementMatrix, MainWindow elementWindow, FontRenderer elementFontRenderer, String elementString, boolean elementHasShadow, float elementPosX, float elementPosY, float elementScale, boolean elementIsScalable, int elementColor) {
            super(elementAnchor, elementMatrix, elementWindow, elementPosX, elementPosY, elementFontRenderer.getStringWidth(elementString), elementFontRenderer.FONT_HEIGHT, elementScale, elementIsScalable, elementColor);
            this.elementFontRenderer = elementFontRenderer;
            this.elementString = elementString;
            this.elementHasShadow = elementHasShadow;
        }

        public StringGUIElement(GUIElement parent, Anchor elementAnchor, FontRenderer elementFontRenderer, String elementString, boolean elementHasShadow, float elementPosX, float elementPosY, float elementScale, boolean elementIsScalable, int elementColor) {
            super(parent, elementAnchor, elementPosX, elementPosY, elementFontRenderer.getStringWidth(elementString), elementFontRenderer.FONT_HEIGHT, elementScale, elementIsScalable, elementColor);
            this.elementFontRenderer = elementFontRenderer;
            this.elementString = elementString;
            this.elementHasShadow = elementHasShadow;
        }

        @Override
        protected void selfRender() {
            if (elementHasShadow)
                elementFontRenderer.drawTextWithShadow(elementMatrix, new StringTextComponent(elementString), 0, 0, elementColor);
            else
                elementFontRenderer.drawText(elementMatrix, new StringTextComponent(elementString), 0, 0, elementColor);
            super.selfRender();
        }
    }

    private static float getHorizontalAnchor(Anchor anchor, float width, float scale) {
        if (anchor.horizontalAnchor == null) return 0f;
        if (anchor.horizontalAnchor == HAnchor.RIGHT) {
            return width / scale;
        } else if (anchor.horizontalAnchor == HAnchor.CENTER) {
            return width / 2 / scale;
        }
        return 0f;
    }

    private static float getHorizontalAnchor(Anchor anchor, float startingPoint, float width, float scale) {
        float scaledWidth = width;
        if (anchor.horizontalAnchor == null) return startingPoint;
        width += startingPoint;
        if (anchor.horizontalAnchor == HAnchor.RIGHT) {
            return width / scale;
        } else if (anchor.horizontalAnchor == HAnchor.CENTER) {
            return ((scaledWidth / 2) + startingPoint) / scale;
        }
        return startingPoint / scale;
    }


    private static float getVerticalAnchor(Anchor anchor, float height, float scale) {
        if (anchor.verticalAnchor == null) return 0f;
        if (anchor.verticalAnchor == VAnchor.BOTTOM) {
            return height / scale;
        } else if (anchor.verticalAnchor == VAnchor.CENTER) {
            return height / 2 / scale;
        }
        return 0f;
    }

    private static float getVerticalAnchor(Anchor anchor, float startingPoint, float height, float scale) {
        float scaledHeight = height;

        if (anchor.verticalAnchor == null) return startingPoint;
        height += startingPoint;
        if (anchor.verticalAnchor == VAnchor.BOTTOM) {
            return height / scale;
        } else if (anchor.verticalAnchor == VAnchor.CENTER) {
            return ((scaledHeight / 2) + startingPoint) / scale;
        }
        return startingPoint / scale;
    }

    private static float getSelfHorizontalAnchor(Anchor anchor, float width) {
        if (anchor.horizontalAnchor == null) return 0f;
        if (anchor.horizontalAnchor == HAnchor.RIGHT) {
            return -width;
        } else if (anchor.horizontalAnchor == HAnchor.CENTER) {
            return -width / 2;
        }
        return 0f;
    }

    private static float getSelfVerticalAnchor(Anchor anchor, float height) {
        if (anchor.verticalAnchor == null) return 0f;
        if (anchor.verticalAnchor == VAnchor.BOTTOM) {
            return -height;
        } else if (anchor.verticalAnchor == VAnchor.CENTER) {
            return -height / 2;
        }
        return 0f;
    }

    private static float getScaledOffset(HAnchor anchor, float offset, float scale, double guiScale)
    {
        return (float) ((anchor == HAnchor.RIGHT ? -offset : offset) / guiScale);
    }

    private static float getScaledOffset(VAnchor anchor, float offset, float scale, double guiScale)
    {
        return (float) ((anchor == VAnchor.BOTTOM ? -offset : offset) / scale);
    }

    public static String color(String message)
    {
        return message.replace("&", "\u00a7");
    }

}