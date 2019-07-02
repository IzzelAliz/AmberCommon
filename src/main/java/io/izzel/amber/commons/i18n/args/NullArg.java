package io.izzel.amber.commons.i18n.args;

import lombok.ToString;
import org.spongepowered.api.text.Text;

@ToString
public class NullArg implements Arg {

    @Override
    public Text toText() {
        return Text.of("null");
    }

}
