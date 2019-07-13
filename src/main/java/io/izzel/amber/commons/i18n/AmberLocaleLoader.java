package io.izzel.amber.commons.i18n;

import io.izzel.amber.commons.i18n.annotation.Locale;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Files;
import java.nio.file.Paths;

@UtilityClass
class AmberLocaleLoader {

    @SneakyThrows
    CommentedConfigurationNode load(PluginContainer container, Locale info) {
        val plugin = container.getInstance().orElseThrow(RuntimeException::new);
        val current = java.util.Locale.getDefault().toString().toLowerCase();
        val destPath = String.format(info.path(), current);
        val destFile = Paths.get("config", container.getId(), destPath);
        val assetLocation = String.format(info.assetLocation(), current);
        val asset = Sponge.getAssetManager().getAsset(plugin, assetLocation)
                .orElseGet(() -> Sponge.getAssetManager().getAsset(plugin, info.def())
                        .orElseThrow(() -> new RuntimeException(String.format("No default locale %s present.", info.def()))));
        if (!Files.exists(destFile)) {
            Files.createDirectories(destFile.getParent());
            Files.createFile(destFile);
            asset.copyToFile(destFile);
        }
        val builder = HoconConfigurationLoader.builder().setPath(destFile).build();
        val fileConf = builder.load();
        val assetConf = HoconConfigurationLoader.builder().setURL(asset.getUrl()).build().load();
        merge(fileConf, assetConf);
        builder.save(fileConf);
        return fileConf;
    }

    private void merge(ConfigurationNode to, ConfigurationNode from) {
        switch (from.getValueType()) {
            case LIST:
            case NULL:
            case SCALAR:
                if (to.getValue() == null) to.setValue(from.getValue());
                break;
            case MAP:
                if (from.getChildrenMap().keySet().contains("type") || from.getChildrenMap().keySet().contains("meta")) {
                    if (to.getValue() == null) to.setValue(from.getValue());
                } else {
                    for (val entry : from.getChildrenMap().entrySet()) {
                        val key = entry.getKey();
                        val node = entry.getValue();
                        merge(to.getNode(key), node);
                    }
                }
                break;
        }
    }

}
