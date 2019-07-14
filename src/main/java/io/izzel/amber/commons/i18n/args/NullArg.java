package io.izzel.amber.commons.i18n.args;

import io.izzel.amber.commons.i18n.AmberLocale;
import lombok.ToString;
import org.spongepowered.api.text.Text;

@ToString
public class NullArg implements Arg {

    @Override
    public Text toText(AmberLocale holder, Object... args) {
        return Text.of("null");
    }

}
