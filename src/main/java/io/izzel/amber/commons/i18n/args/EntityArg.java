package io.izzel.amber.commons.i18n.args;

import lombok.RequiredArgsConstructor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;

@RequiredArgsConstructor(staticName = "of")
class EntityArg implements Arg {

    private final Entity entity;

    @Override
    public Text toText() {
        return entity.get(Keys.DISPLAY_NAME).orElseGet(() -> {
            if (entity instanceof Player) return Text.of(((Player) entity).getName());
            else return TranslatableText.of(entity.getTranslation());
        });
    }

}