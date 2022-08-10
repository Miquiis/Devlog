package me.miquiis.devlog.common.data;

import com.google.gson.*;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.List;

public class ModTab {

    public class Section {

        public class Page {

            private final String pageTitle;
            private final String pageContents;

            public Page(String pageTitle, String pageContents) {
                this.pageTitle = pageTitle;
                this.pageContents = pageContents;
            }

            public String getPageTitle() {
                return pageTitle;
            }

            public String getPageContents() {
                return pageContents;
            }
        }

        private final String sectionName;
        private final String sectionItem;
        private final Page sectionPage;

        public Section(String sectionName, String sectionItem, Page sectionPage) {
            this.sectionName = sectionName;
            this.sectionItem = sectionItem;
            this.sectionPage = sectionPage;
        }

        public String getSectionName() {
            return sectionName;
        }

        public ResourceLocation getSectionItem() {
            return new ResourceLocation(sectionItem);
        }

        public Page getSectionPage() {
            return sectionPage;
        }
    }

    private final String tabName;
    private final String tabDescription;
    private final String tabItem;
    private final List<Section> tabSections;

    public ModTab(String tabName, String tabDescription, String tabItem, List<Section> tabSections)
    {
        this.tabName = tabName;
        this.tabDescription = tabDescription;
        this.tabItem = tabItem;
        this.tabSections = tabSections;
    }

    public String getTabName() {
        return tabName;
    }

    public String getTabDescription() {
        return tabDescription;
    }

    public ResourceLocation getTabItem() {
        return new ResourceLocation(tabItem);
    }

    public List<Section> getTabSections() {
        return tabSections;
    }

}
