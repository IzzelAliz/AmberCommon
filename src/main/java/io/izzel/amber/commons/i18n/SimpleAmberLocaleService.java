package io.izzel.amber.commons.i18n;

import io.izzel.amber.commons.i18n.annotation.Locale;
import lombok.val;
import org.spongepowered.api.Sponge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SimpleAmberLocaleService implements AmberLocaleService {

    private final Map<String, Map<Locale, AmberLocaleProvider>> locale = new ConcurrentHashMap<>();

    @Override
    public AmberLocale get(Object plugin, Locale info) {
        val container = Sponge.getPluginManager().fromInstance(plugin).orElseThrow(RuntimeException::new);
        return locale.computeIfAbsent(container.getId(), it -> new HashMap<>())
                .computeIfAbsent(info, it -> new AmberLocaleProvider(plugin, info));
    }


}
