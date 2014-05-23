package net.cogzmc.hub.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import net.cogzmc.core.model.Model;
import net.cogzmc.core.model.ModelField;

@EqualsAndHashCode(callSuper = true)
@ModelField
@Data
/**
 * Represents a stored setting about the hub. This can store any type that the database will accept.
 */
public final class HubSetting<T> extends Model {
    @Setter(AccessLevel.NONE) private String key;
    private T value;

    public void setValue(Object value) {
        try {
            //noinspection unchecked
            this.value = (T) value;
        } catch (ClassCastException ignored) {} //Nothing we should do other than not set the value.
    }
}
