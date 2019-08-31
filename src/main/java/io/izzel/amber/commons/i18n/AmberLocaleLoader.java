package io.izzel.amber.commons.i18n;

import io.izzel.amber.commons.i18n.annotation.Locale;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
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
        val current = java.util.Locale.getDefault().toString().toLowerCase();
        val destPath = String.format(info.path(), current);
        val destFile = Paths.get("config", container.getId(), destPath);
        val assetLocation = String.format(info.assetLocation(), current);
        val asset = Sponge.getAssetManager().getAsset(container, assetLocation)
                .orElseGet(() -> Sponge.getAssetManager().getAsset(container, info.def())
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

    private void merge(CommentedConfigurationNode to, CommentedConfigurationNode from) {
        switch (from.getValueType()) {
            case LIST:
            case NULL:
            case SCALAR:
                if (to.getValue() == null) {
                    to.setValue(from.getValue());
                    from.getComment().ifPresent(to::setComment);
                }
                break;
            case MAP:
                if (from.getChildrenMap().containsKey("type") || from.getChildrenMap().containsKey("meta")) {
                    if (to.getValue() == null) {
                        to.setValue(from.getValue());
                        from.getComment().ifPresent(to::setComment);
                    }
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
