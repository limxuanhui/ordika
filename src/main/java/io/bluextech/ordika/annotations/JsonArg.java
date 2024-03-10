package io.bluextech.ordika.annotations;
/* Created by limxuanhui on 19/7/23 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonArg {
    String value() default "";
}