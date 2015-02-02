package net.cogzmc.coreessentials.commands;

import net.cogzmc.coreessentials.CoreEssentials;

import java.util.Arrays;
import java.util.List;

@CommandMeta(aliases = {"plugins", "pl", "?"}, description = "Lists the modules and plugins on this instance.")
@CommandPermission("core.essentials.about")
public final class PluginsCommand extends ModuleCommand {
    public PluginsCommand() {
        super("coreplugins");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        char[] plating = new char[52];
        Arrays.fill(plating, ' ');
        String boilerPlate = ChatColor.YELLOW + ChatColor.STRIKETHROUGH.toString() + new String(plating);
        StringBuilder moduleList = new StringBuilder();
        StringBuilder thirdParty = new StringBuilder();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof ModularPlugin) {
                ModularPlugin modularPlugin = (ModularPlugin)plugin;
                moduleList.append(modularPlugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(modularPlugin.getMeta().name()).append(ChatColor.YELLOW).append(", ");
            }
            else if (!(plugin instanceof Core)) {
                List<String> authors = plugin.getDescription().getAuthors();
                StringBuilder author = new StringBuilder();
                for (String s : authors) {
                    author.append(s).append(", ");
                }
                String authorsString = author.toString();
                authorsString = authorsString.substring(0, Math.max(0,authorsString.length()-2));
                thirdParty.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(plugin.getName());
                if (authorsString.length() > 0) thirdParty.append(" by ").append(authorsString);
                thirdParty.append(ChatColor.RED).append("; ");
            }
        }
        String modules = moduleList.toString();
        modules = modules.substring(0, Math.max(0,modules.length()-2));
        String plugins = thirdParty.toString();
        plugins = plugins.substring(0, Math.max(0,plugins.length()-2));
        CoreEssentials moduleProvider = Core.getInstance().getModuleProvider(CoreEssentials.class);
        sender.sendMessage(boilerPlate);
        sender.sendMessage(moduleProvider.getFormat("core-info", false, new String[]{"<version>", Core.getInstance().getDescription().getVersion()}));
        sender.sendMessage("");
        sender.sendMessage(moduleProvider.getFormat("module-list", false, new String[]{"<modules>", modules}));
        sender.sendMessage("");
        sender.sendMessage(moduleProvider.getFormat("plugin-list", false, new String[]{"<plugins>", plugins}));
        sender.sendMessage(boilerPlate);
    }
}
