package net.cogzmc.coreessentials.commands;

import net.cogzmc.coreessentials.CoreEssentials;

@CommandMeta(aliases = {"tps", "lag", "lm"}, description = "Get the current runtime info!")
@CommandPermission("core.essentials.laginfo")
public final class LagInfoCommand extends ModuleCommand {
    public LagInfoCommand() {
        super("laginfo");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        long memoryAv = Runtime.getRuntime().maxMemory();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long freeMemory = memoryAv - usedMemory;
        sender.sendMessage(formatStat("Memory Available", ByteLevels.formatBytes(memoryAv)));
        sender.sendMessage(formatStat("Memory Used", ByteLevels.formatBytes(usedMemory)));
        sender.sendMessage(formatStat("Memory Free", ByteLevels.formatBytes(freeMemory)));
        sender.sendMessage(formatStat("Players Online", String.valueOf(Core.getPlayerManager().getOnlinePlayers().size())));
    }

    private String formatStat(String stat, String value) {
        return Core.getInstance().getModuleProvider(CoreEssentials.class).getFormat("lag-stat", new String[]{"<stat>", stat}, new String[]{"<value>", value});
    }

    static enum ByteLevels {
        BYTES("bytes"),
        KILOBYTES("KB"),
        MEGABYTES("MB"),
        GIGABYTES("GB");
        ByteLevels(String s) {
            this.suffix = s;
        }
        private final String suffix;
        static String formatBytes(double bytes) {
            Double currentNumber = bytes;
            ByteLevels level = BYTES;
            int levelIndex = 0;
            while (currentNumber/1024 > 1 && levelIndex < ByteLevels.values().length) {
                levelIndex++;
                currentNumber = currentNumber/1024;
                level = ByteLevels.values()[levelIndex];
            }
            return String.format("%.2f %s", currentNumber, level.suffix);
        }
    }
}
