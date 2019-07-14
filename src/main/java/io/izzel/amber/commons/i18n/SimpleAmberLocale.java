package io.izzel.amber.commons.i18n;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.annotation.Locale;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameState;
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
    private PluginContainer container;
    private Game game;
    private Locale info;

    @Inject
    public SimpleAmberLocale(PluginContainer container, Game game, SpongeInjectionPoint point) {
        this.container = container;
        this.info = Optional.ofNullable(point.getAnnotation(Locale.class)).orElse(DEF);
        this.game = game;
    }

    private void checkState() {
        if (game.getState().compareTo(GameState.POST_INITIALIZATION) >= 0) {
            if (plugin == null) {
                plugin = container.getInstance().orElseThrow(IllegalStateException::new);
            }
            if (!game.getServiceManager().isRegistered(AmberLocaleService.class)) {
                game.getServiceManager().setProvider(plugin, AmberLocaleService.class, new SimpleAmberLocaleService());
            }
        } else throw new IllegalStateException();
    }

    @Override
    public void to(MessageReceiver receiver, String path, Object... args) {
        checkState();
        game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).to(receiver, path, args);
    }

    @Override
    public void reload() throws Exception {
        checkState();
        game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).reload();
    }

    @Override
    public <T> Optional<T> get(String path, Object... args) {
        checkState();
        return game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).get(path, args);
    }

    @Override
    public <T> Optional<T> getAs(String path, TypeToken<T> typeToken, Object... args) {
        checkState();
        return game.getServiceManager().provideUnchecked(AmberLocaleService.class).get(plugin, info).getAs(path, typeToken, args);
    }

}
