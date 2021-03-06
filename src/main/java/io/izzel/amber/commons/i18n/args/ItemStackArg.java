package io.izzel.amber.commons.i18n.args;

import io.izzel.amber.commons.i18n.AmberLocale;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.action.TextActions;

@ToString
@RequiredArgsConstructor(staticName = "of")
class ItemStackArg implements Arg {

    private final ItemStackSnapshot item;

    @Override
    public Text toText(AmberLocale holder, Object... args) {
        val builder = Text.builder();
        builder.append(item.get(Keys.DISPLAY_NAME).orElseGet(() -> TranslatableText.of(item.getTranslation())));
        builder.onHover(TextActions.showItem(item));
        return builder.build();
    }

}
