package io.izzel.amber.commons.i18n.objects.typed;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.objects.LocaleObject;
import io.izzel.amber.commons.i18n.objects.MetaObject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.function.Consumer;

@ToString
@RequiredArgsConstructor(staticName = "of")
public class RefObject extends LocaleObject implements MetaObject {

    private final String refNode;
    private LocaleObject ref;

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        if (ref == null) {
            receiver.sendMessage(Text.of("Ref node: " + refNode));
        } else {
            ref.send(receiver, args);
        }
    }

    @Override
    public <T> T mapAs(TypeToken<T> typeToken, Object... args) {
        return ref == null ? null : ref.mapAs(typeToken, args);
    }

    public Consumer<LocaleObject> getCallback() {
        return it -> ref = it;
    }

    @Override
    public Text apply(Text text, Object... args) {
        return (ref != null && ref instanceof MetaObject) ? ((MetaObject) ref).apply(text, args) : text;
    }

}
