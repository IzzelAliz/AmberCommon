package io.izzel.amber.commons.i18n.args;

import io.izzel.amber.commons.i18n.AmberLocale;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@FunctionalInterface
public interface Arg {

    Text toText(AmberLocale holder, Object... args);

    default Arg withCallback(Consumer<CommandSource> callback) {
        return CallbackArg.of(this, callback);
    }

    static Arg of(Object object) {
        if (object instanceof Optional) return of(((Optional<?>) object).orElse(null));
        if (object == null) return new NullArg();
        if (object instanceof Arg) return ((Arg) object);
        if (object instanceof String) return StringArg.of((String) object);
        if (object instanceof ItemStack) return ItemStackArg.of(((ItemStack) object).createSnapshot());
        if (object instanceof ItemStackSnapshot) return ItemStackArg.of((ItemStackSnapshot) object);
        if (object instanceof Entity) return EntityArg.of((Entity) object);
        if (object instanceof Text) return (holder, args) -> ((Text) object);
        return StringArg.of(String.valueOf(object));
    }

    static Arg user(UUID uuid) {
        return UserArg.of(uuid);
    }

    static Arg ref(String node) {
        return RefArg.of(node);
    }

}
