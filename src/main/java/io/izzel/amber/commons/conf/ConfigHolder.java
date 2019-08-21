package io.izzel.amber.commons.conf;

import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.function.Supplier;

public interface ConfigHolder {

    void reload() throws Exception;

    <T> void putDefault(String node, TypeToken<T> typeToken, Supplier<T> supplier, String comment);

    <T> void set(String node, TypeToken<T> typeToken, T value);

    <T> Optional<T> get(String node, TypeToken<T> typeToken);

    default <T> Optional<T> get(String node, Class<T> type) {
        return get(node, TypeToken.of(type));
    }

}
