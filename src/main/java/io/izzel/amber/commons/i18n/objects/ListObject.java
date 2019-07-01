package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class ListObject implements LocaleObject {

    private final List<LocaleObject> list;

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        for (LocaleObject object : list) {
            object.send(receiver, args);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T raw() {
        return (T) mapAs(new TypeToken<List<Text>>() {});
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T mapAs(TypeToken<T> typeToken) {
        if (typeToken.equals(new TypeToken<List<Text>>() {})) {
            return (T) list.stream().map(it -> it.mapAs(TypeToken.of(Text.class))).collect(Collectors.toList());
        } else if (typeToken.equals(TypeToken.of(Text.class))) {
            return (T) mapAs(new TypeToken<List<Text>>() {}).stream().reduce((a, b) -> Text.joinWith(Text.NEW_LINE, a, b)).orElse(Text.EMPTY);
        } else {
            return null;
        }
    }
}
