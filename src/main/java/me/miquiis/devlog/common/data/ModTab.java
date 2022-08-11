package me.miquiis.devlog.common.data;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModTab implements Cloneable {

    public class Section implements Cloneable {

        public class Page implements Cloneable {

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

            @Override
            public Page clone() {
                try {
                    return (Page) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError();
                }
            }
        }

        private final String sectionName;
        private final String sectionItem;
        private List<Page> sectionPages;

        public Section(String sectionName, String sectionItem, List<Page> sectionPages) {
            this.sectionName = sectionName;
            this.sectionItem = sectionItem;
            this.sectionPages = sectionPages;
        }

        public String getSectionName() {
            return sectionName;
        }

        public ResourceLocation getSectionItem() {
            return new ResourceLocation(sectionItem);
        }

        public List<Page> getSectionPages() {
            return sectionPages;
        }

        @Override
        public Section clone() {
            try {
                Section clone = (Section) super.clone();
                clone.sectionPages = new ArrayList<Page>(clone.sectionPages).stream().map(Page::clone).collect(Collectors.toList());
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    private final String tabName;
    private final String tabDescription;
    private final String tabItem;
    private List<Section> tabSections;

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

    @Override
    public ModTab clone() {
        try {
            ModTab clone = (ModTab) super.clone();
            clone.tabSections = new ArrayList<Section>(tabSections).stream().map(Section::clone).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
