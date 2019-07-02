package io.izzel.amber.commons.i18n.objects;

import org.spongepowered.api.text.Text;

public interface MetaObject {

    Text apply(Text text, Object... args);

}
