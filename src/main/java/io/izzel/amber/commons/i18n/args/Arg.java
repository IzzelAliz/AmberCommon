package io.izzel.amber.commons.i18n.args;

import lombok.val;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface Arg {

    Text toText();

    default Arg withCallback(Consumer<CommandSource> callback) {
        return CallbackArg.of(this, callback);
    }

    static Arg of(Object object) {
        if (object instanceof Arg) return ((Arg) object);
        if (object instanceof String) return map(TextSerializers.FORMATTING_CODE::deserialize, object);
        if (object instanceof ItemStack) {
            return map((ItemStack item) -> {
                val builder = Text.builder();
                builder.append(item.get(Keys.DISPLAY_NAME).orElseGet(() -> TranslatableText.of(item.getTranslation())));
                builder.onHover(TextActions.showItem(item.createSnapshot()));
                return builder.build();
            }, object);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static <U> Arg map(Function<? super U, Text> fun, Object object) {
        return () -> fun.apply((U) object);
    }

}
