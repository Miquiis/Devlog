package me.miquiis.devlog.common.classes;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EasyGUI {

    public static class GUIElement {
        public double elementPosX;
        public double elementPosY;

        public int elementWidth;
        public int elementHeight;

        public int elementColor;

        public GUIElement(double elementPosX, double elementPosY, int elementWidth, int elementHeight, int elementColor)
        {
            this.elementPosX = elementPosX;
            this.elementPosY = elementPosY;
            this.elementWidth = elementWidth;
            this.elementHeight = elementHeight;
            this.elementColor = elementColor;
        }

        public void render(MatrixStack matrixStack)
        {
            render(matrixStack, elementPosX, elementPosY);
        }

        public void render(MatrixStack matrixStack, double x, double y)
        {
            matrixStack.push();
            matrixStack.translate(x, y,0);
        }
    }

    public static class BoxGUIElement extends GUIElement {

        public BoxGUIElement(float elementPosX, float elementPosY, int elementWidth, int elementHeight, int elementColor) {
            super(elementPosX, elementPosY, elementWidth, elementHeight, elementColor);
        }

        public void render(MatrixStack matrixStack, double x, double y, int elementWidth, int elementHeight) {
            super.render(matrixStack, x, y);
            AbstractGui.fill(matrixStack, 0, 0, elementWidth, elementHeight, elementColor);
            matrixStack.pop();
        }

        @Override
        public void render(MatrixStack matrixStack) {
            render(matrixStack, elementPosX, elementPosY, elementWidth, elementHeight);
        }
    }

    public static class ListGUIElement extends BoxGUIElement {

        public int elementMaxHeight;
        public List<GUIElement> childrenList;

        public ListGUIElement(float elementPosX, float elementPosY, int elementWidth, int elementHeight, int elementColor) {
            super(elementPosX, elementPosY, elementWidth, elementHeight, elementColor);
            this.childrenList = new ArrayList<>();
        }

        public ListGUIElement addChildren(GUIElement... guiElements)
        {
            this.childrenList.addAll(new ArrayList<>(Arrays.asList(guiElements)));
            return this;
        }

        @Override
        public void render(MatrixStack matrixStack, double x, double y) {
            calculateMaxHeight();
            super.render(matrixStack, x, y, elementWidth, elementMaxHeight);
        }

        @Override
        public void render(MatrixStack matrixStack) {
            render(matrixStack, elementPosX, elementPosY);
        }

        private void calculateMaxHeight()
        {
            elementMaxHeight = 0;
            for (GUIElement guiElement : childrenList) {
                elementMaxHeight += guiElement.elementHeight;
            }
            if (elementMaxHeight < elementHeight) elementMaxHeight = elementHeight;
        }
    }

    public static EasyGUI.BoxGUIElement createBoxGui(float elementPosX, float elementPosY, int elementWidth, int elementHeight, int elementColor)
    {
        return new BoxGUIElement(elementPosX, elementPosY, elementWidth, elementHeight, elementColor);
    }

    public static String color(String message)
    {
        return message.replace("&", "\u00a7");
    }

}