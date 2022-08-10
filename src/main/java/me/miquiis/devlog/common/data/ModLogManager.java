package me.miquiis.devlog.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ModLogManager extends JsonReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
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

    public Map<ResourceLocation, ModTab> getRegisteredModTabs() {
        return registeredModTabs;
    }
}
