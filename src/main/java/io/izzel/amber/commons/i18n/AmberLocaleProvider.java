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
    private Object plugin;
    private Locale info;

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
    public void reload() throws Exception {
        val container = Sponge.getPluginManager().fromInstance(plugin).orElseThrow(RuntimeException::new);
        val node = AmberLocaleLoader.load(container, info);
        locales = new AmberLocaleMapper().asMap(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(String path, Object... args) {
        return (Optional<T>) Optional.ofNullable(locales.get(path).mapAs(TypeToken.of(Text.class), args));
    }

    @Override
    public <T> Optional<T> getAs(String path, TypeToken<T> typeToken) {
        return Optional.ofNullable(locales.get(path).mapAs(typeToken));
    }

}
