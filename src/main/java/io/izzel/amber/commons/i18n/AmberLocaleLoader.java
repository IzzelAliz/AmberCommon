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
        fileConf.mergeValuesFrom(assetConf);
        builder.save(fileConf);
        return fileConf;
    }

}
