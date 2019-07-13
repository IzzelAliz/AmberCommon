package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import lombok.var;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@RequiredArgsConstructor(staticName = "of")
public class ListObject extends LocaleObject implements MetaObject {

    private final List<LocaleObject> list;

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        for (LocaleObject object : list) {
            object.send(receiver, args);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T mapAs(TypeToken<T> typeToken, Object... args) {
        if (typeToken.equals(new TypeToken<List<Text>>() {})) {
            return (T) list.stream().map(it -> it.mapAs(TypeToken.of(Text.class), args)).collect(Collectors.toList());
        } else if (typeToken.equals(TypeToken.of(Text.class))) {
            return (T) mapAs(new TypeToken<List<Text>>() {}, args).stream().reduce((a, b) -> Text.joinWith(Text.NEW_LINE, a, b)).orElse(Text.EMPTY);
        } else {
            return null;
        }
    }

    @Override
    public Text apply(Text text, Object... args) {
        var ret = text;
        for (val object : list) {
            if (object instanceof MetaObject) {
                ret = ((MetaObject) object).apply(ret, args);
            }
        }
        return ret;
    }

}
