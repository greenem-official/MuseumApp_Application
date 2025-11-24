package org.daylight.museumapp.components.annotations;

import org.daylight.museumapp.dto.filterrelated.FilterRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnMeta {
    String title() default "";
    boolean sortable() default true;

    Class<? extends FilterRule>[] filters() default {};
}

