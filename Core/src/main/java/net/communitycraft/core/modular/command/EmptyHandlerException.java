package net.communitycraft.core.modular.command;

public final class EmptyHandlerException extends CommandException {
    public EmptyHandlerException() {
        super("There was no handler found for this command!");
    }
}
