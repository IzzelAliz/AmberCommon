package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

public class SimpleStringObject implements LocaleObject {

    private final Text text;

    private SimpleStringObject(Text text) {
        this.text = text;
    }

    public static SimpleStringObject of(String text) {
        return new SimpleStringObject(TextSerializers.FORMATTING_CODE.deserialize(text));
    }

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        receiver.sendMessage(text);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T raw() {
        return ((T) TextSerializers.FORMATTING_CODE.serialize(text));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T mapAs(TypeToken<T> typeToken) {
        if (typeToken.equals(TypeToken.of(String.class))) {
            return raw();
        } else if (typeToken.equals(TypeToken.of(Text.class))) {
            return ((T) text);
        } else {
            return null;
        }
    }

}
