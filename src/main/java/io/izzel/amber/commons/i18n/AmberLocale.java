package io.izzel.amber.commons.i18n;

import com.google.common.reflect.TypeToken;
import com.google.inject.ImplementedBy;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Optional;

@ImplementedBy(SimpleAmberLocale.class)
public interface AmberLocale {

    void to(MessageReceiver receiver, String path, Object... args);

    void reload() throws Exception;

    <T> Optional<T> get(String path, Object... args);

    <T> Optional<T> getAs(String path, TypeToken<T> typeToken);

    default <T> T get(String path, T def, Object... args) {
        return this.<T>get(path, args).orElse(def);
    }

    default <T> T getAs(String path, T def, TypeToken<T> typeToken) {
        return this.getAs(path, typeToken).orElse(def);
    }

}
