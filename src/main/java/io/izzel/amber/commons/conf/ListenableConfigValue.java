package io.izzel.amber.commons.conf;

import com.google.common.reflect.TypeToken;
import lombok.ToString;
import lombok.var;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@ToString
class ListenableConfigValue<T> implements ConfigValue<T> {

    private final String node;
    private final TypeToken<T> typeToken;
    private final ConfigHolder holder;

    private List<BiFunction<T, T, T>> listeners;
    private T value;

    ListenableConfigValue(String node, TypeToken<T> typeToken, T value, ConfigHolder holder) {
        this.node = node;
        this.typeToken = typeToken;
        this.value = value;
        this.holder = holder;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        if (listeners == null) {
            this.value = value;
        } else {
            var ret = value;
            for (BiFunction<T, T, T> listener : listeners) {
                ret = listener.apply(this.value, ret);
            }
            this.value = ret;
        }
        holder.set(node, typeToken, this.value);
    }

    @Override
    public void addListener(BiFunction<T, T, T> onUpdate) {
        if (listeners == null) listeners = new ArrayList<>();
        listeners.add(onUpdate);
    }

}
