package me.miquiis.devlog.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.miquiis.devlog.Devlog;
import me.miquiis.devlog.common.data.ModTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
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

public class ModLogScreen extends Screen {

    public class ModTabButton extends Button {

        private ModTab modTab;

        public ModTabButton(int x, int y, int width, int height, ModTab modTab, ITextComponent title, IPressable pressedAction) {
            super(x, y, width, height, title, pressedAction);
            this.modTab = modTab;
        }

        public void setModTab(ModTab modTab) {
            this.modTab = modTab;
        }
    }

    protected static final ResourceLocation ASSETS = new ResourceLocation(Devlog.MOD_ID, "textures/gui/menu/assets.png");
    private final int LINES_PER_PAGE = 11;
    private int guiLeft;
    private int guiTop;
    private int xSize = 147;
    private int ySize = 166;

    private List<ModTab> availableTabs;

    private ModTab focusedTab;
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

    public ModLogScreen() {
        super(new TranslationTextComponent("devlog.menu.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.availableTabs = new ArrayList<>();

        Devlog.getInstance().getModLogManager().getRegisteredModTabs().forEach((resourceLocation, modTab) -> {
            if (modTab != null && modTab.getTabName() != null)
            {
                availableTabs.add(modTab);
            }
        });

        this.tabUp = this.addButton(new Button(guiLeft - 5 - 17, guiTop - 6, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Tab Up Arrow");
            currentTabPage = MathHelper.clamp(currentTabPage - 1, 0, 99);
        }));

        this.tabDown = this.addButton(new Button(guiLeft - 5 - 17, guiTop + 159, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Tab Down Arrow");
            currentTabPage = MathHelper.clamp(currentTabPage + 1, 0, 99);
        }));

        this.sectionUp = this.addButton(new Button(guiLeft + 152, guiTop - 6, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Section Up Arrow");
            currentSectionPage = MathHelper.clamp(currentSectionPage - 1, 0, 99);
        }));

        this.sectionDown = this.addButton(new Button(guiLeft + 152, guiTop + 159, 17, 11, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Section Down Arrow");
            currentSectionPage = MathHelper.clamp(currentSectionPage + 1, 0, 99);
        }));

        this.pageTurn = this.addButton(new Button(guiLeft + 117, guiTop + 144, 18, 10, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Page Turn");
            currentPage = MathHelper.clamp(currentPage + 1, 0, 99);
        }));

        this.pageBack = this.addButton(new Button(guiLeft + 12, guiTop + 144, 18, 10, new StringTextComponent("up_arrow"), p_onPress_1_ -> {
            System.out.println("Page Back");
            currentPage = MathHelper.clamp(currentPage - 1, 0, 99);
        }));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        resetRenderButtons();
    }

    private void resetRenderButtons()
    {
        for (int i = 0; i < 5; i++)
        {
            setSectionById(null, i);
            setTabById(null, i);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (pageTurn != null) pageTurn.active = focusedSection != null && getPagesFromSection(focusedSection) > currentPage + 1;
        if (pageBack != null) pageBack.active = currentPage > 0;
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
            if (getTabById(i) == null)
            {
                setTabById(this.addButton(new ModTabButton(startX - 30, startY + tabY, 30, 26, availableTabs.get(currentPage), new StringTextComponent("tab"), p_onPress_1_ -> {
                    setFocusedTab(((ModTabButton)p_onPress_1_).modTab);
                })), i);
            }
            renderTab(matrixStack, createItemStackFromResource(availableTabs.get(currentPage).getTabItem()), startX, startY + tabY, true, false);
            tabY += 4 + 26;
        }

        if (availableTabs.size() > (currentTabPage + 1) * 5)
        {
            renderUpArrow(matrixStack, startX - 5, startY - 6, true, false);
        }
        if (currentTabPage > 0)
        {
            renderDownArrow(matrixStack, startX - 5, startY + 159, true, false);
        }

        // Rendering Sections

        if (focusedTab != null)
        {
            if (focusedSection == null)
            {
                centeredString(matrixStack, font, "\u00A7l" + font.trimStringToWidth(focusedTab.getTabName(), 100), (this.width / 2), startY + 17, 0xFFFFFF);
                AtomicInteger skipLine = new AtomicInteger();
                font.trimStringToWidth(new StringTextComponent(focusedTab.getTabDescription()), 125).forEach(iReorderingProcessor -> {
                    font.func_238422_b_(matrixStack, iReorderingProcessor, startX + 12, startY + 35 + skipLine.get(), 0xFFFFFF);
                    skipLine.set(skipLine.get() + 10);
                });
            }

            this.minecraft.getTextureManager().bindTexture(ASSETS);

            tabY = 9;
            for (int i = 0; i < 5; i++)
            {
                int currentPage = i + (5 * currentSectionPage);
                if (currentPage >= focusedTab.getTabSections().size()) break;
                if (getSectionById(i) == null)
                {
                    setSectionById(this.addButton(new Button(startX + 147, startY + tabY, 30, 26, new StringTextComponent("tab"), p_onPress_1_ -> {
                        setFocusedSection(focusedTab.getTabSections().get(currentPage));
                        System.out.println("You pressed section " + focusedSection.getSectionName());
                    })), i);
                }
                renderSection(matrixStack, createItemStackFromResource(focusedTab.getTabSections().get(currentPage).getSectionItem()), startX + 147, startY + tabY, false, false);
                tabY += 4 + 26;
            }

        }

        if (focusedTab != null && focusedTab.getTabSections().size() > (currentSectionPage + 1) * 5)
        {
            renderUpArrow(matrixStack, startX + 152, startY - 6, false, false);
        }
        if (currentSectionPage > 0)
        {
            renderDownArrow(matrixStack, startX + 152, startY + 159, false, false);
        }

        // Rendering Contents

        if (focusedSection != null)
        {
            centeredString(matrixStack, font, "\u00A7l" + font.trimStringToWidth(focusedSection.getSectionPage().getPageTitle(), 100), (this.width / 2), startY + 17, 0xFFFFFF);
            AtomicInteger skipLine = new AtomicInteger();

            List<IReorderingProcessor> linesFromPage = getLinesFromPage(focusedSection.getSectionPage().getPageContents());
            if (linesFromPage.size() > currentPage * LINES_PER_PAGE)
            {
                int lastReadLine = currentPage * LINES_PER_PAGE + LINES_PER_PAGE;
                linesFromPage = linesFromPage.subList(currentPage * LINES_PER_PAGE, currentPage * LINES_PER_PAGE + lastReadLine < linesFromPage.size() ? lastReadLine : linesFromPage.size());
            }

            linesFromPage.forEach(iReorderingProcessor -> {
                font.func_238422_b_(matrixStack, iReorderingProcessor, startX + 12, startY + 35 + skipLine.get(), 0xFFFFFF);
                skipLine.set(skipLine.get() + 10);
            });

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

        //super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private ItemStack createItemStackFromResource(ResourceLocation resourceLocation)
    {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(resourceLocation));
    }

    private List<IReorderingProcessor> getLinesFromPage(String contents)
    {
        return font.trimStringToWidth(new StringTextComponent(contents), 125);
    }

    private int getPagesFromSection(ModTab.Section section)
    {
        List<IReorderingProcessor> lines = getLinesFromPage(section.getSectionPage().getPageContents());
        return (lines.size() / LINES_PER_PAGE) + 1;
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
        this.focusedSection = section;
        currentPage = 0;
    }

    private void setFocusedTab(ModTab tab)
    {
        this.focusedTab = tab;
        setFocusedSection(null);
        currentSectionPage = 0;
    }

    private void renderBackground(MatrixStack matrixStack, int x, int y)
    {
        this.blit(matrixStack, x, y, 1, 1, 147, 166);
    }

    private void renderSelectedTab(MatrixStack matrixStack, ItemStack itemStack, int x, int y, boolean addSelfX, boolean addSelfY)
    {
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 13, y - (addSelfY ? 26 : 0) + 4);
        this.minecraft.getTextureManager().bindTexture(ASSETS);
        this.blit(matrixStack, x - (addSelfX ? 35 : 0), y - (addSelfY ? 26 : 0), 1, 191, 35, 26);
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
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (addSelfX ? 35 : 0) + 13, y - (addSelfY ? 26 : 0) + 4);
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
