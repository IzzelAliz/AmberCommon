package io.izzel.amber.commons.i18n;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.annotation.Locale;
import io.izzel.amber.commons.i18n.objects.LocaleObject;
import lombok.SneakyThrows;
import lombok.val;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

class AmberLocaleProvider implements AmberLocale {

    private Map<String, LocaleObject> locales;
    private final Object plugin;
    private final Locale info;

    @SneakyThrows
    AmberLocaleProvider(Object plugin, Locale info) {
        this.plugin = plugin;
        this.info = info;
        reload();
    }

    @Override
    public void to(MessageReceiver receiver, String path, Object... args) {
        val opt = Optional.ofNullable(locales.get(path));
        if (opt.isPresent()) {
            opt.get().send(receiver, args);
        } else {
            receiver.sendMessage(Text.of(TextColors.RED, "Missing language node: " + path + " with args: " + Arrays.toString(args)));
        }
    }

    @Override
    public void reload() {
        val container = Sponge.getPluginManager().fromInstance(plugin).orElseThrow(RuntimeException::new);
        val node = AmberLocaleLoader.load(container, info);
        locales = new AmberLocaleMapper(this).asMap(node);
    }

    @Override
    public Optional<Text> get(String path, Object... args) {
        return Optional.ofNullable(locales.get(path)).map(it -> it.mapAs(TypeToken.of(Text.class), args));
    }

    @Override
    public <T> Optional<T> getAs(String path, TypeToken<T> typeToken, Object... args) {
        return Optional.ofNullable(locales.get(path)).map(it -> it.mapAs(typeToken, args));
    }

}
