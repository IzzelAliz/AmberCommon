package io.izzel.amber.commons.i18n;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.annotation.Locale;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
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

    private Object plugin;
    private Game game;
    private Locale info;

    @Inject
    public SimpleAmberLocale(PluginContainer container, Game game, SpongeInjectionPoint point) {
        this.plugin = container.getInstance().orElseThrow(IllegalStateException::new);
        this.info = Optional.ofNullable(point.getAnnotation(Locale.class)).orElse(DEF);
        this.game = game;
        game.getEventManager().registerListener(plugin, GamePostInitializationEvent.class, Order.FIRST, true, event -> {
            if (!game.getServiceManager().isRegistered(AmberLocaleService.class)) {
                game.getServiceManager().setProvider(plugin, AmberLocaleService.class, new SimpleAmberLocaleService());
            }
            game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info);
        });
    }

    @Override
    public void to(MessageReceiver receiver, String path, Object... args) {
        game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).to(receiver, path, args);
    }

    @Override
    public void reload() throws Exception {
        game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).reload();
    }

    @Override
    public <T> Optional<T> get(String path, Object... args) {
        return game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).get(path, args);
    }

    @Override
    public <T> Optional<T> getAs(String path, TypeToken<T> typeToken) {
        return game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).getAs(path, typeToken);
    }

}
