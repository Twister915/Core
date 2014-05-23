package net.cogzmc.core.modular.command;

/**
 * Implement this on any exception that extends {@link net.cogzmc.core.modular.command.CommandException} and the retrun value of the {@link #getFriendlyMessage(ModuleCommand)}
 * method will be displayed instead of a verbose message for the exception in the default handler.
 */
public interface FriendlyException {
    /**
     * Grabs a friendly version of the message to be displayed during an exception.
     * @param command The command that is attempting to get the friendly message.
     * @return A message to be displayed to the user during failure by default.
     */
    String getFriendlyMessage(ModuleCommand command);
}
