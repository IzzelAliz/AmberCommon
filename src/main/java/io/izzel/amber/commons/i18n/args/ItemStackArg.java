package io.izzel.amber.commons.i18n.args;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.action.TextActions;

@RequiredArgsConstructor(staticName = "of")
class ItemStackArg implements Arg {

    private final ItemStack item;

    @Override
    public Text toText() {
        val builder = Text.builder();
        builder.append(item.get(Keys.DISPLAY_NAME).orElseGet(() -> TranslatableText.of(item.getTranslation())));
        builder.onHover(TextActions.showItem(item.createSnapshot()));
        return builder.build();
    }

}
