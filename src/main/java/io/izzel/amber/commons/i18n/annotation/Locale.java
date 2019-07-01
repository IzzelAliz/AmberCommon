package io.izzel.amber.commons.i18n.annotation;

import lombok.Generated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Generated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Locale {

    String path() default "locale_%s.conf";

    String assetLocation() default "locale/%s.conf";

    boolean listenChanges() default true; // todo implement this

    String def() default "locale/en_us.conf";

}
