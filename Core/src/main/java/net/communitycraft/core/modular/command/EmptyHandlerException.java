package net.communitycraft.core.modular.command;

public final class EmptyHandlerException extends CommandException implements FriendlyException {
    public EmptyHandlerException() {
        super("There was no handler found for this command!");
    }

    @Override
    public String getFriendlyMessage(ModuleCommand command) {
        return getMessage();
    }
}
