package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.AmberLocale;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Map;

public abstract class LocaleObject {

    private static final MetaObject SELF = (it, x) -> it;

    public abstract void send(MessageReceiver receiver, Object... args);

    public abstract <T> T mapAs(TypeToken<T> typeToken, Object... args);

    protected AmberLocale holder;
    private Map<Integer, MetaObject> meta;

    protected Text applyMeta(int idx, Text text, Object... args) {
        return meta == null ? text : meta.getOrDefault(idx, SELF).apply(text, args);
    }

    public final void setMeta(Map<Integer, MetaObject> meta) {
        this.meta = meta;
    }

    public LocaleObject holder(AmberLocale locale) {
        this.holder = locale;
        return this;
    }

}
