package io.izzel.amber.commons.conf;

import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.val;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("RedundantCast")
@ToString
class SimpleConfigHolder implements ConfigHolder {

    private final Path path;
    private final PluginContainer container;

    private HoconConfigurationLoader loader;
    private AmberConfigService service;
    private CommentedConfigurationNode rootNode;
    private boolean save = false;

    @SneakyThrows
    SimpleConfigHolder(Path path, PluginContainer container, AmberConfigService service) {
        this.path = path;
        this.container = container;
        this.service = service;
        loader = HoconConfigurationLoader.builder().setPath(path).build();
        reload();
        Sponge.getEventManager().registerListener(container, GameStartedServerEvent.class, this::on);
        Sponge.getEventManager().registerListener(container, GameStoppingServerEvent.class, this::on);
    }

    @Override
    public void reload() throws Exception {
        rootNode = loader.load();
    }

    @SneakyThrows
    @Override
    public <T> void putDefault(String node, TypeToken<T> typeToken, Supplier<T> supplier, String comment) {
        val n = rootNode.getNode((Object[]) node.split("\\."));
        if (n.getValueType() == ValueType.NULL) {
            n.setValue(typeToken, supplier.get());
            if (StringUtils.isNotEmpty(comment)) {
                n.setComment(comment);
            }
        }
    }

    @SneakyThrows
    @Override
    public <T> void set(String node, TypeToken<T> typeToken, T value) {
        rootNode.getNode((Object[]) node.split("\\.")).setValue(typeToken, value);
    }

    @SneakyThrows
    @Override
    public <T> Optional<T> get(String node, TypeToken<T> typeToken) {
        return Optional.ofNullable(rootNode.getNode((Object[]) node.split("\\.")).getValue(typeToken));
    }

    @SneakyThrows
    private void on(GameStartedServerEvent event) {
        loader.save(rootNode);
    }

    @SneakyThrows
    private void on(GameStoppingServerEvent event) {
        loader.save(rootNode);
    }

}
