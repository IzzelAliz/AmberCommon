package io.izzel.amber.commons.i18n.args;

import org.spongepowered.api.text.Text;

public class NullArg implements Arg {

    @Override
    public Text toText() {
        return Text.of("null");
    }

}
