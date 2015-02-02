package net.cogzmc.core.modular.command;

public final class ArgumentRequirementException extends CommandException implements FriendlyException {
    public ArgumentRequirementException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(ModuleCommand command) {
        return ChatColor.RED + this.getMessage();
    }
}
