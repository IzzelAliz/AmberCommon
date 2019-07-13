package io.izzel.amber.commons.i18n.args;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@FunctionalInterface
public interface Arg {

    Text toText();

    default Arg withCallback(Consumer<CommandSource> callback) {
        return CallbackArg.of(this, callback);
    }

    static Arg of(Object object) {
        if (object instanceof Optional) return of(((Optional<?>) object).orElse(null));
        if (object == null) return new NullArg();
        if (object instanceof Arg) return ((Arg) object);
        if (object instanceof String) return StringArg.of(((String) object));
        if (object instanceof ItemStack) return ItemStackArg.of(((ItemStack) object));
        if (object instanceof Entity) return EntityArg.of(((Entity) object));
        return StringArg.of(String.valueOf(object));
    }

    static Arg user(UUID uuid) {
        return UserArg.of(uuid);
    }

}
