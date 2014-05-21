package net.communitycraft.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SerializationException extends Exception {
    private final String message;
}
