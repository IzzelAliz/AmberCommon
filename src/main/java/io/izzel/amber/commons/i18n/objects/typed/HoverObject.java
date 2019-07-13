package io.izzel.amber.commons.i18n.objects.typed;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.objects.LocaleObject;
import io.izzel.amber.commons.i18n.objects.MetaObject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;

@ToString
@RequiredArgsConstructor(staticName = "of")
public class HoverObject extends LocaleObject implements MetaObject {

    private final LocaleObject hover;

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        hover.send(receiver, args);
    }

    @Override
    public <T> T mapAs(TypeToken<T> typeToken, Object... args) {
        return hover.mapAs(typeToken, args);
    }

    @Override
    public Text apply(Text text, Object... args) {
        return Text.builder().append(text).onHover(TextActions.showText(hover.mapAs(TypeToken.of(Text.class), args))).build();
    }

}
