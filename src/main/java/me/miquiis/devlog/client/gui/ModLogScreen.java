package me.miquiis.devlog.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.miquiis.devlog.Devlog;
import me.miquiis.devlog.common.data.ModLogManager;
import me.miquiis.devlog.common.data.ModTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ModLogScreen extends Screen {

    public static class DoNotRenderButton extends Button {
        public DoNotRenderButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction) {
            super(x, y, width, height, title, pressedAction);
        }
    }

    public static class ModTabButton extends DoNotRenderButton {

        private CustomModTab modTab;

        public ModTabButton(int x, int y, int width, int height, CustomModTab modTab, ITextComponent title, IPressable pressedAction) {
            super(x, y, width, height, title, pressedAction);
            this.modTab = modTab;
        }

        public void setModTab(CustomModTab modTab) {
            this.modTab = modTab;
        }
    }

    public static class CustomModTab {

        private final ModTab modTab;
        private final boolean isOnMemory;

        public CustomModTab(ModTab modTab, boolean isOnMemory) {
            this.modTab = modTab;
            this.isOnMemory = isOnMemory;
        }
    }

    protected static final ResourceLocation ASSETS = new ResourceLocation(Devlog.MOD_ID, "textures/gui/menu/assets.png");

    private ModLogManager modLogManager;

    private final int LINES_PER_PAGE = 11;
    private int guiLeft;
    private int guiTop;
    private int xSize = 147;
    private int ySize = 166;

    private List<CustomModTab> availableTabs;

    private CustomModTab focusedTab;
    private ModTab.Section focusedSection;

    private int currentTabPage, currentSectionPage, currentPage;

    private Button tabButton;
    private Button tab2Button;
    private Button tab3Button;
    private Button tab4Button;
    private Button tab5Button;

    private Button sectionButton;
    private Button section2Button;
    private Button section3Button;
    private Button section4Button;
    private Button section5Button;

    private Button pageTurn, pageBack;
    private Button tabUp, tabDown;
    private Button sectionUp, sectionDown;

    private Button editButton, cancelEditButton, saveCopyButton;

    public ModLogScreen() {
        super(new TranslationTextComponent("devlog.menu.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.modLogManager = Devlog.getInstance().getModLogManager();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.availableTabs = new ArrayList<>();

        modLogManager.getRegisteredModTabs().forEach((resourceLocation, modTab) -> {
            if (modTab != null && modTab.getTabName() != null)
            {
                CustomModTab customModTab = new CustomModTab(modTab, false);
                availableTabs.add(customModTab);
            }
        });

        modLogManager.getInMemoryTabs().forEach((resourceLocation, modTab) -> {
            if (modTab != null && modTab.getTabName() != null)
            {
                CustomModTab customModTab = new CustomModTab(modTab, true);
                availableTabs.add(customModTab);
            }
        });

        this.tabUp = this.addButton(new DoNotRenderButton(guiLeft - 5 - 17, guiTop - 6, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Tab Up Arrow");
            currentTabPage = MathHelper.clamp(currentTabPage - 1, 0, 99);
        }));

        this.tabDown = this.addButton(new DoNotRenderButton(guiLeft - 5 - 17, guiTop + 159, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Tab Down Arrow");
            currentTabPage = MathHelper.clamp(currentTabPage + 1, 0, 99);
        }));

        this.sectionUp = this.addButton(new DoNotRenderButton(guiLeft + 152, guiTop - 6, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Section Up Arrow");
            currentSectionPage = MathHelper.clamp(currentSectionPage - 1, 0, 99);
        }));

        this.sectionDown = this.addButton(new DoNotRenderButton(guiLeft + 152, guiTop + 159, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Section Down Arrow");
            currentSectionPage = MathHelper.clamp(currentSectionPage + 1, 0, 99);
        }));

        this.pageTurn = this.addButton(new DoNotRenderButton(guiLeft + 117, guiTop + 144, 18, 10, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Page Turn");
            currentPage = MathHelper.clamp(currentPage + 1, 0, 99);
        }));

        this.pageBack = this.addButton(new DoNotRenderButton(guiLeft + 12, guiTop + 144, 18, 10, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Page Back");
            currentPage = MathHelper.clamp(currentPage - 1, 0, 99);
        }));

        this.editButton = this.addButton(new Button(guiLeft + 7, guiTop + 170, 50, 20, new StringTextComponent("Edit"), p_onPress_1_ -> {
            ResourceLocation resourceLocation = modLogManager.getModTabResourceLocation(focusedTab.modTab);
            if (modLogManager.isInMemory(resourceLocation))
            {
                CustomModTab modTab = getAvailableModTabByResource(resourceLocation);
                if (modTab == null) return;
                setFocusedTab(modTab);
                return;
            }
            ModTab modTab = modLogManager.createInMemoryCopyOf(resourceLocation);
            setFocusedTab(new CustomModTab(modTab, true));
            availableTabs.add(focusedTab);
//            this.editButton.visible = false;
//            this.cancelEditButton.visible = true;
//            this.saveCopyButton.active = true;
        }));

        this.cancelEditButton = this.addButton(new Button(guiLeft + 7, guiTop + 170, 50, 20, new StringTextComponent("Cancel"), p_onPress_1_ -> {
            System.out.println("Cancel Edit");
            availableTabs.remove(focusedTab);
            modLogManager.getInMemoryTabs().remove(modLogManager.getModTabResourceLocation(focusedTab.modTab));
            setFocusedTab(null);
//            this.editButton.visible = true;
//            this.cancelEditButton.visible = false;
//            this.saveCopyButton.active = false;
        }));

        this.saveCopyButton = this.addButton(new Button(guiLeft + 60, guiTop + 170, 80, 20, new StringTextComponent("Save Copy"), p_onPress_1_ -> {
            System.out.println("Save Copy");
        }));

        this.editButton.visible = false;
        this.saveCopyButton.visible = false;
        this.cancelEditButton.visible = false;
        this.saveCopyButton.active = false;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        resetRenderButtons();
    }

    private CustomModTab getAvailableModTabByResource(ResourceLocation resourceLocation)
    {
        for (CustomModTab availableTab : availableTabs) {
            if (resourceLocation.equals(modLogManager.getMemoryModTabResourceLocation(availableTab.modTab))) return availableTab;
        }
        return null;
    }

    private void resetRenderButtons()
    {
        System.out.println("Resetting buttons");
        for (int i = 0; i < 5; i++)
        {
            this.buttons.remove(getSectionById(i));
            this.buttons.remove(getTabById(i));
            setSectionById(null, i);
            setTabById(null, i);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (pageTurn != null) pageTurn.active = focusedSection != null && getPagesFromSection(focusedSection) > currentPage + 1;
        if (pageBack != null) pageBack.active = currentPage > 0;
        if (sectionUp != null) sectionUp.active = currentSectionPage > 0;
        if (sectionDown != null) sectionDown.active = focusedTab != null && focusedTab.modTab.getTabSections().size() > (currentSectionPage + 1) * 5;

        if (focusedTab != null && focusedTab.isOnMemory)
        {
            if (editButton != null) editButton.visible = false;
            if (cancelEditButton != null) cancelEditButton.visible = true;
            if (saveCopyButton != null)
            {
                saveCopyButton.active = true;
                saveCopyButton.visible = true;
            }
        } else {
            if (editButton != null) editButton.visible = focusedTab != null;
            if (cancelEditButton != null) cancelEditButton.visible = false;
            if (saveCopyButton != null)
            {
                saveCopyButton.visible = focusedTab != null;
                saveCopyButton.active = false;
            }
        }

        //buttons.clear();
        //init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        int startX = this.guiLeft;
        int startY = this.guiTop;

        drawCenteredString(matrixStack, font, "\u00A7lDev Log", (this.width / 2), startY - 12, 0x257ACF);
        InventoryScreen.drawEntityOnScreen(startX - 100, (startY + ySize / 2) + 50, 50, (startX - 100) -mouseX, (startY + 50) -mouseY, minecraft.player);

        this.minecraft.getTextureManager().bindTexture(ASSETS);

        renderBackground(matrixStack, startX, startY);

        // Rendering Tabs

        int tabY = 9;
        for (int i = 0; i < 5; i++)
        {
            int currentPage = i + (5 * currentTabPage);
            if (currentPage >= availableTabs.size()) break;
            CustomModTab currentModTab = availableTabs.get(currentPage);

            if (getTabById(i) == null)
            {
                setTabById(this.addButton(new ModTabButton(startX - 30, startY + tabY, 30, 26, currentModTab, new StringTextComponent("tab"), p_onPress_1_ -> {
                    setFocusedTab(availableTabs.get(currentPage));
                })), i);
            }

            if (currentModTab.equals(focusedTab))
            {
                if (focusedTab.isOnMemory)
                {
                    renderSelectedMemoryTab(matrixStack, createItemStackFromResource(currentModTab.modTab.getTabItem()), startX + 3, startY + tabY, true, false);
                } else {
                    renderSelectedTab(matrixStack, createItemStackFromResource(currentModTab.modTab.getTabItem()), startX + 3, startY + tabY, true, false);
                }
            } else
            {
                if (currentModTab.isOnMemory)
                {
                    renderMemoryTab(matrixStack, createItemStackFromResource(currentModTab.modTab.getTabItem()), startX, startY + tabY, true, false);
                } else {
                    renderTab(matrixStack, createItemStackFromResource(currentModTab.modTab.getTabItem()), startX, startY + tabY, true, false);
                }
            }
            tabY += 4 + 26;
        }

        if (availableTabs.size() > (currentTabPage + 1) * 5)
        {
            renderDownArrow(matrixStack, startX - 5, startY + 159, true, false);
        }
        if (currentTabPage > 0)
        {
            renderUpArrow(matrixStack, startX - 5, startY - 6, true, false);
        }

        // Rendering Sections

        if (focusedTab != null)
        {
            if (focusedSection == null)
            {
                centeredString(matrixStack, font, "\u00A7l" + font.trimStringToWidth(focusedTab.modTab.getTabName(), 100), (this.width / 2), startY + 17, 0xFFFFFF);
                AtomicInteger skipLine = new AtomicInteger();
                font.trimStringToWidth(new StringTextComponent(focusedTab.modTab.getTabDescription()), 125).forEach(iReorderingProcessor -> {
                    font.func_238422_b_(matrixStack, iReorderingProcessor, startX + 12, startY + 35 + skipLine.get(), 0xFFFFFF);
                    skipLine.set(skipLine.get() + 10);
                });
            }

            this.minecraft.getTextureManager().bindTexture(ASSETS);

            tabY = 9;
            for (int i = 0; i < 5; i++)
            {
                int currentPage = i + (5 * currentSectionPage);
                if (currentPage >= focusedTab.modTab.getTabSections().size()) break;
                ModTab.Section section = focusedTab.modTab.getTabSections().get(currentPage);
                if (getSectionById(i) == null)
                {
                    System.out.println("Resetting sections");
                    setSectionById(this.addButton(new DoNotRenderButton(startX + 147, startY + tabY, 30, 26, new StringTextComponent("tab"), p_onPress_1_ -> {
                        System.out.println("Setting section from " + focusedTab.modTab.getTabName());
                        setFocusedSection(focusedTab.modTab.getTabSections().get(currentPage));
                        System.out.println("You pressed section " + focusedSection.getSectionName());
                    })), i);
                }
                if (section.equals(focusedSection))
                {
                    renderSelectedSection(matrixStack, createItemStackFromResource(focusedTab.modTab.getTabSections().get(currentPage).getSectionItem()), startX + 144, startY + tabY, false, false);
                } else
                {
                    renderSection(matrixStack, createItemStackFromResource(focusedTab.modTab.getTabSections().get(currentPage).getSectionItem()), startX + 147, startY + tabY, false, false);
                }
                tabY += 4 + 26;
            }

        }

        if (focusedTab != null && focusedTab.modTab.getTabSections().size() > (currentSectionPage + 1) * 5)
        {
            renderDownArrow(matrixStack, startX + 152, startY + 159, false, false);
        }
        if (currentSectionPage > 0)
        {
            renderUpArrow(matrixStack, startX + 152, startY - 6, false, false);
        }

        // Rendering Contents

        if (focusedSection != null)
        {
            ModTab.Section.Page currentPage = focusedSection.getSectionPages().get(this.currentPage);

            if (currentPage != null)
            {
                centeredString(matrixStack, font, "\u00A7l" + font.trimStringToWidth(currentPage.getPageTitle(), 100), (this.width / 2), startY + 17, 0xFFFFFF);
                AtomicInteger skipLine = new AtomicInteger();

                List<IReorderingProcessor> lines = getLinesFromPage(currentPage.getPageContents());
                List<String> linesString = getStringLinesFromPage(currentPage.getPageContents());
                System.out.println(lines.size());
                System.out.println(linesString.size());
                if (lines.size() > LINES_PER_PAGE)
                {
                    lines = lines.subList(0, LINES_PER_PAGE);
                }

                lines.forEach(iReorderingProcessor -> {
                    font.func_238422_b_(matrixStack, iReorderingProcessor, startX + 12, startY + 35 + skipLine.get(), 0xFFFFFF);
                    skipLine.set(skipLine.get() + 10);
                });
            }

            this.minecraft.getTextureManager().bindTexture(ASSETS);
        }

        if (currentPage > 0)
        {
            renderBackArrow(matrixStack, startX + 12, startY + 154, false, true);
        }
        if (focusedSection != null && getPagesFromSection(focusedSection) > currentPage + 1)
        {
            renderNextArrow(matrixStack, startX + 117, startY + 154, false, true);
        }

        for(int i = 0; i < this.buttons.size(); ++i) {
            Widget button = this.buttons.get(i);
            if (button instanceof DoNotRenderButton) continue;
            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }

//        AbstractGui.fill(matrixStack, startX - 1 + 15, startY - 1 + 15, startX + 15, startY + 10 + 15, -16777216);

    }

    private void selectText(Rectangle2d[] coords) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);

        for(Rectangle2d rectangle2d : coords) {
            int i = rectangle2d.getX();
            int j = rectangle2d.getY();
            int k = i + rectangle2d.getWidth();
            int l = j + rectangle2d.getHeight();
            bufferbuilder.pos((double)i, (double)l, 0.0D).endVertex();
            bufferbuilder.pos((double)k, (double)l, 0.0D).endVertex();
            bufferbuilder.pos((double)k, (double)j, 0.0D).endVertex();
            bufferbuilder.pos((double)i, (double)j, 0.0D).endVertex();
        }

        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private int getWidthOfLine(String line)
    {
        return font.getStringWidth(line);
    }

    private ItemStack createItemStackFromResource(ResourceLocation resourceLocation)
    {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(resourceLocation));
    }

    private List<IReorderingProcessor> getLinesFromPage(String contents)
    {
        return font.trimStringToWidth(new StringTextComponent(contents), 125);
    }

    private List<String> getStringLinesFromPage(String contents)
    {
        List<String> lines = new ArrayList<>();
        while (font.getStringWidth(contents) > 118)
        {
            String cutOut = font.trimStringToWidth(contents, 118);
            lines.add(cutOut);
            System.out.println(contents);
            int indexOf = contents.indexOf(cutOut);
            if (indexOf != -1)
            {
                contents = contents.substring(indexOf + cutOut.length());
            }
        }
        return lines;
    }

    private int getPagesFromSection(ModTab.Section section)
    {
        return section.getSectionPages().size();
    }

    private void centeredString(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color)
    {
        drawCenteredString(matrixStack, fontRenderer, text, x, y, color);
        minecraft.getTextureManager().bindTexture(ASSETS);
    }

    private void string(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color)
    {
        drawString(matrixStack, fontRenderer, text, x, y, color);
        minecraft.getTextureManager().bindTexture(ASSETS);
    }

    private void setFocusedSection(ModTab.Section section)
    {
        System.out.println("Setting Focused Section to " + (section != null ? section.getSectionName() : "null"));
        this.focusedSection = section;
        currentPage = 0;
    }

    private void setFocusedTab(CustomModTab tab)
    {
        System.out.println("Setting Focused Tab to " + (tab != null ? tab.modTab.getTabName() : "null"));
        this.focusedTab = tab;
        resetRenderButtons();
        setFocusedSection(null);
        currentSectionPage = 0;
    }

    private void renderBackground(MatrixStack matrixStack, int x, int y)
    {
        this.blit(matrixStack, x, y, 1, 1, 147, 166);
    }

    private void renderSelectedMemoryTab(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 10, y - (addSelfY ? 26 : 0) + 5);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 35 : 0), y - (addSelfY ? 26 : 0), 135, 191, 35, 26);
    }

    private void renderSelectedTab(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 10, y - (addSelfY ? 26 : 0) + 5);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 35 : 0), y - (addSelfY ? 26 : 0), 1, 191, 35, 26);
    }

    private void renderMemoryTab(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 13, y - (addSelfY ? 26 : 0) + 5);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 30 : 0), y - (addSelfY ? 26 : 0), 171, 191, 30, 26);
    }

    private void renderTab(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 13, y - (addSelfY ? 26 : 0) + 5);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 30 : 0), y - (addSelfY ? 26 : 0), 37, 191, 30, 26);
    }

    private void renderSection(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 5, y - (addSelfY ? 26 : 0) + 5);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 30 : 0), y - (addSelfY ? 26 : 0), 68, 191, 30, 26);
    }

    private void renderSelectedSection(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 8, y - (addSelfY ? 26 : 0) + 5);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 35 : 0), y - (addSelfY ? 26 : 0), 99, 191, 35, 26);
    }

    private void renderSelectedUpArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 17 : 0), y - (addSelfY ? 11 : 0), 1, 222, 17, 11);
    }

    private void renderSelectedDownArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 17 : 0), y - (addSelfY ? 11 : 0), 1, 234, 17, 11);
    }

    private void renderUpArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 17 : 0), y - (addSelfY ? 11 : 0), 19, 222, 17, 11);
    }

    private void renderDownArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 17 : 0), y - (addSelfY ? 11 : 0), 19, 234, 17, 11);
    }

    private void renderSelectedBackArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 18 : 0), y - (addSelfY ? 10 : 0), 37, 223, 18, 10);
    }

    private void renderSelectedNextArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 18 : 0), y - (addSelfY ? 10 : 0), 37, 235, 18, 10);
    }

    private void renderBackArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 18 : 0), y - (addSelfY ? 10 : 0), 56, 223, 18, 10);
    }

    private void renderNextArrow(MatrixStack matrixStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        this.blit(matrixStack, x - (addSelfX ? 18 : 0), y - (addSelfY ? 10 : 0), 56, 235, 18, 10);
    }

    private Button getTabById(int id)
    {
        if (id == 0) return tabButton;
        else if (id == 1) return tab2Button;
        else if (id == 2) return tab3Button;
        else if (id == 3) return tab4Button;
        else if (id == 4) return tab5Button;
        else return null;
    }

    private Button getSectionById(int id)
    {
        if (id == 0) return sectionButton;
        else if (id == 1) return section2Button;
        else if (id == 2) return section3Button;
        else if (id == 3) return section4Button;
        else if (id == 4) return section5Button;
        else return null;
    }

    private void setTabById(Button button, int id)
    {
        if (id == 0) tabButton = button;
        else if (id == 1) tab2Button = button;
        else if (id == 2) tab3Button = button;
        else if (id == 3) tab4Button = button;
        else if (id == 4) tab5Button = button;
    }

    private void setSectionById(Button button, int id)
    {
        if (id == 0) sectionButton = button;
        else if (id == 1) section2Button = button;
        else if (id == 2) section3Button = button;
        else if (id == 3) section4Button = button;
        else if (id == 4) section5Button = button;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
