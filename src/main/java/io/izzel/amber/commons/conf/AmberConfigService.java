package io.izzel.amber.commons.conf;

import com.google.inject.ImplementedBy;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

@ImplementedBy(SimpleAmberConfigService.class)
public interface AmberConfigService {

    ConfigHolder get(PluginContainer container, Path path);

}
