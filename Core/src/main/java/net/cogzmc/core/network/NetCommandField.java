package net.cogzmc.core.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You may annotate either a {@link ElementType#FIELD} or {@link ElementType#TYPE} with this annotation.
 *
 * In situations where the entire type is denoted as a {@link net.cogzmc.core.network.NetCommandField} then any fields that are <b>also</b> denoted as a {@link net.cogzmc.core.network.NetCommandField} will be ignored.
 *
 * In situations where a type is not denoted as a {@link net.cogzmc.core.network.NetCommandField} but a specific field is, that field is accounted for during serialization.
 */
@Target(value = {ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NetCommandField {
}
