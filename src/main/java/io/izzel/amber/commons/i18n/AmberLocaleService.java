package io.izzel.amber.commons.i18n;

import io.izzel.amber.commons.i18n.annotation.Locale;

public interface AmberLocaleService {

    AmberLocale get(Object plugin, Locale info);

}
