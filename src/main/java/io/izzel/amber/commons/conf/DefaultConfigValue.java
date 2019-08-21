package io.izzel.amber.commons.conf;

import lombok.AllArgsConstructor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@AllArgsConstructor
class DefaultConfigValue<T> implements ConfigValue<T> {

    private final Supplier<T> supplier;

    @Override
    public T get() {
        return supplier.get();
    }

    @Override
    public void set(T value) {
        throw new IllegalStateException("ConfigValue is not properly injected.");
    }

    @Override
    public void addListener(BiFunction<T, T, T> onUpdate) {
        throw new IllegalStateException("ConfigValue is not properly injected.");
    }

}
