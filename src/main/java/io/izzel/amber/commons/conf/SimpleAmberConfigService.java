package io.izzel.amber.commons.conf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
class SimpleAmberConfigService implements AmberConfigService {

    private final Map<Path, ConfigHolder> map = new ConcurrentHashMap<>();

    @Inject
    public SimpleAmberConfigService(PluginContainer container) {
        Sponge.getServiceManager().setProvider(container, AmberConfigService.class, this);
    }

    @Override
    public ConfigHolder get(PluginContainer container, Path path) {
        return map.computeIfAbsent(path, k -> new SimpleConfigHolder(path, container, this));
    }

}
