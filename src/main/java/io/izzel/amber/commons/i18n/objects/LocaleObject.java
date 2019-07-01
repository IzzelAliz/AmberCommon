package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.text.channel.MessageReceiver;

public interface LocaleObject {

    void send(MessageReceiver receiver, Object... args);

    <T> T raw();

    <T> T mapAs(TypeToken<T> typeToken);

    static LocaleObject nothing() {
        return new LocaleObject() {
            @Override
            public void send(MessageReceiver receiver, Object... args) {
            }

            @Override
            public <T> T raw() {
                return null;
            }

            @Override
            public <T> T mapAs(TypeToken<T> typeToken) {
                return null;
            }
        };
    }

}
