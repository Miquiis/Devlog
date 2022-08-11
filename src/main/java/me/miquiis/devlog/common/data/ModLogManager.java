package me.miquiis.devlog.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModLogManager extends JsonReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private Map<ResourceLocation, ModTab> inMemoryTabs = new HashMap<>();
    private Map<ResourceLocation, ModTab> registeredModTabs = ImmutableMap.of();

    public ModLogManager() {
        super(GSON_INSTANCE, "modlog");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        ImmutableMap.Builder<ResourceLocation, ModTab> builder = ImmutableMap.builder();
        objectIn.forEach((id, element) -> {
            try (net.minecraft.resources.IResource res = resourceManagerIn.getResource(getPreparedPath(id));){
                ModTab modTab = GSON_INSTANCE.fromJson(element, ModTab.class);
                builder.put(id, modTab);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse mod tab {}", id, exception);
            }

        });
        this.registeredModTabs = builder.build();
    }

    public ModTab createInMemoryCopyOf(ResourceLocation resourceLocation)
    {
        if (registeredModTabs.containsKey(resourceLocation))
        {
            ModTab modTab = registeredModTabs.get(resourceLocation).clone();
            inMemoryTabs.put(resourceLocation, modTab);
            return modTab;
        } else {
            return new ModTab("Empty", "Empty", "minecraft:air", new ArrayList<>());
        }
    }

    public Map<ResourceLocation, ModTab> getInMemoryTabs() {
        return inMemoryTabs;
    }

    public Map<ResourceLocation, ModTab> getRegisteredModTabs() {
        return registeredModTabs;
    }

    public ResourceLocation getModTabResourceLocation(ModTab modTab)
    {
        for (Map.Entry<ResourceLocation, ModTab> resourceLocationModTabEntry : registeredModTabs.entrySet()) {
            if (resourceLocationModTabEntry.getValue().equals(modTab)) return resourceLocationModTabEntry.getKey();
        }
        for (Map.Entry<ResourceLocation, ModTab> resourceLocationModTabEntry : inMemoryTabs.entrySet()) {
            if (resourceLocationModTabEntry.getValue().equals(modTab)) return resourceLocationModTabEntry.getKey();
        }
        return null;
    }

    public ResourceLocation getMemoryModTabResourceLocation(ModTab modTab)
    {
        for (Map.Entry<ResourceLocation, ModTab> resourceLocationModTabEntry : inMemoryTabs.entrySet()) {
            if (resourceLocationModTabEntry.getValue().equals(modTab)) return resourceLocationModTabEntry.getKey();
        }
        return null;
    }

    public boolean isInMemory(ResourceLocation resourceLocation)
    {
        return inMemoryTabs.containsKey(resourceLocation);
    }
}
