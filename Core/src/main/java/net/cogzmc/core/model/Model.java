package net.cogzmc.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * A {@link net.cogzmc.core.model.Model} represents something that can be serialized and deserialized into and out of a database.
 */
public abstract class Model {
    @Getter @Setter private String id;
}
