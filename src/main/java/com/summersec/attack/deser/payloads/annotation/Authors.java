package com.summersec.attack.deser.payloads.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authors {
    public static final String FROHOFF = "frohoff";
    public static final String KORLR = "KORLR";
    public static final String phith0n = "phith0n";
    public static final String MATTHIASKAISER = "MATTHIASKAISER";
    public static final String GEBL = "GEBL";


    String[] value() default {};

    public static class Utils {
        public static String[] getAuthors(AnnotatedElement annotated) {
            Authors authors = annotated.<Authors>getAnnotation(Authors.class);
            if (authors != null && authors.value() != null) {
                return authors.value();
            }
            return new String[0];
        }
    }
}


