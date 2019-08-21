package io.izzel.amber.commons.conf;

import com.google.inject.ProvidedBy;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("InvalidProvidedBy")
@ProvidedBy(ConfigValueProvider.class)
public interface ConfigValue<T> {

    default Optional<T> getOption() {
        return Optional.ofNullable(get());
    }

    T get();

    void set(T value);

    void addListener(BiFunction<T, T, T> onUpdate);

    static <T> ConfigValue<T> of(T value) {
        return new DefaultConfigValue<>(() -> value);
    }

    static <T> ConfigValue<T> of(Supplier<T> value) {
        return new DefaultConfigValue<>(value);
    }

}
