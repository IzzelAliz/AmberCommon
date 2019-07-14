package io.izzel.amber.commons.i18n.args;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.AmberLocale;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.text.Text;

@RequiredArgsConstructor(staticName = "of")
public class RefArg implements Arg {

    private final String node;

    @Override
    public Text toText(AmberLocale holder, Object... args) {
        return holder.getAs(node, TypeToken.of(Text.class), args).orElse(Text.of("{Arg: " + node + "}"));
    }

}
