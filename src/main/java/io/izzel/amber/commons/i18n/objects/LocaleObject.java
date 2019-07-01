package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.text.channel.MessageReceiver;

public interface LocaleObject {

    void send(MessageReceiver receiver, Object... args);

    <T> T mapAs(TypeToken<T> typeToken, Object... args);

}
