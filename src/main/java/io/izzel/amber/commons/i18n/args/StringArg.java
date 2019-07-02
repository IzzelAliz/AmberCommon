package io.izzel.amber.commons.i18n.args;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@ToString
@RequiredArgsConstructor(staticName = "of")
class StringArg implements Arg {

    private final String text;

    @Override
    public Text toText() {
        return TextSerializers.FORMATTING_CODE.deserialize(text);
    }

}
