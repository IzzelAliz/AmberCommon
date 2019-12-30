package io.izzel.amber.commons.conf;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.common.inject.SpongeInjectionPoint;

import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

class ConfigValueProvider implements Provider<ConfigValue<?>> {

    @Inject private SpongeInjectionPoint point;
    @Inject private AmberConfigService service;
    @Inject private PluginContainer container;
    @Inject private Injector injector;
    @Inject @ConfigDir(sharedRoot = false) private Path root;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public ConfigValue<?> get() {
        val setting = point.getAnnotation(Setting.class);
        Preconditions.checkNotNull(setting, "@Setting is not present.");
        val pathName = Optional.ofNullable(point.getAnnotation(io.izzel.amber.commons.conf.annotation.Path.class))
            .map(io.izzel.amber.commons.conf.annotation.Path::value).orElse("%id%.conf")
            .replace("%id%", container.getId());
        val path = root.resolve(pathName);
        val holder = service.get(container, path);
        var node = setting.value();
        try {
            val instance = injector.getInstance(((Class<?>) point.getSource().getType()));
            val optionalField = Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Setting.class))
                .filter(field -> field.getDeclaredAnnotation(Setting.class) == setting)
                .findAny();
            if (optionalField.isPresent()) {
                val field = optionalField.get();
                field.setAccessible(true);
                val defaultValue = ((ConfigValue) field.get(instance));
                if (defaultValue != null) {
                    node = setting.value().isEmpty() ? field.getName() : setting.value();
                    val wrapperType = field.getGenericType();
                    if (wrapperType instanceof ParameterizedType) {
                        holder.putDefault(node, (TypeToken) TypeToken.of(((ParameterizedType) wrapperType)
                            .getActualTypeArguments()[0]), defaultValue::get, setting.comment());
                    }
                }
            }
        } catch (Throwable t) {
            if (setting.value().isEmpty()) {
                throw new IllegalArgumentException("Cannot get field for setting.", t);
            }
        }
        val wrapperType = point.getType().getType();
        if (wrapperType instanceof ParameterizedType) {
            val valueType = ((ParameterizedType) wrapperType).getActualTypeArguments()[0];
            val typeToken = TypeToken.of(valueType);
            val value = holder.get(node, typeToken).orElse(null);
            return new ListenableConfigValue(node, typeToken, value, holder);
        } else {
            throw new IllegalArgumentException("Type parameter is not present in an @Setting field.");
        }
    }

}
