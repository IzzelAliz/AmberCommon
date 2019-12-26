package io.izzel.amber.commons.i18n;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.annotation.Locale;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.common.inject.SpongeInjectionPoint;

import java.lang.annotation.Annotation;
import java.util.Optional;

class SimpleAmberLocale implements AmberLocale {

    private static final Locale DEF = new Locale() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Locale.class;
        }

        @Override
        public String path() {
            return "locale_%s.conf";
        }

        @Override
        public String assetLocation() {
            return "locale/%s.conf";
        }

        @Override
        public boolean listenChanges() {
            return true;
        }

        @Override
        public String def() {
            return "locale/en_us.conf";
        }
    };

    private final PluginContainer container;
    private final Locale info;
    private final AmberLocaleService service;

    @Inject
    public SimpleAmberLocale(PluginContainer container, Game game, SpongeInjectionPoint point) {
        this.container = container;
        this.info = Optional.ofNullable(point.getAnnotation(Locale.class)).orElse(DEF);
        if (!game.getServiceManager().isRegistered(AmberLocaleService.class)) {
            game.getServiceManager().setProvider(container, AmberLocaleService.class,
                service = new SimpleAmberLocaleService());
        } else {
            service = game.getServiceManager().provideUnchecked(AmberLocaleService.class);
        }
    }

    @Override
    public void to(MessageReceiver receiver, String path, Object... args) {
        service.get(container, info).to(receiver, path, args);
    }

    @Override
    public void reload() throws Exception {
        service.get(container, info).reload();
    }

    @Override
    public Optional<Text> get(String path, Object... args) {
        return service.get(container, info).get(path, args);
    }

    @Override
    public <T> Optional<T> getAs(String path, TypeToken<T> typeToken, Object... args) {
        return service.get(container, info).getAs(path, typeToken, args);
    }

}
