package net.cogzmc.core.modular;

import net.cogzmc.core.config.YAMLConfigurationFile;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public final class Formatter {
    private String loadedPrefix;

    private final YAMLConfigurationFile formatsFile;

    public Formatter(YAMLConfigurationFile formatsFile) {
        this.formatsFile = formatsFile;
        loadedPrefix = formatsFile.getConfig().contains("prefix") ? ChatColor.translateAlternateColorCodes('&', formatsFile.getConfig().getString("prefix")) : null;
    }

    public FormatBuilder begin(String path) {
        return new FormatBuilder(formatsFile.getConfig().getString(path));
    }

    public boolean has(String path) {
        return formatsFile.getConfig().contains(path);
    }

    public FormatBuilder withValue(String value) {
        return new FormatBuilder(value);
    }

    public final class FormatBuilder {
        private final String formatString;
        private final Map<String, String> modifiers = new HashMap<String, String>();
        private boolean prefix = true, coloredInputs = true;

        private FormatBuilder(String formatString) {
            this.formatString = formatString;
        }

        public FormatBuilder withModifier(String key, Object value) {
            modifiers.put(key, value.toString());
            return this;
        }

        public FormatBuilder withPrefix(boolean p) {
            prefix = p;
            return this;
        }

        public FormatBuilder withColoredInputs(boolean c) {
            coloredInputs = c;
            return this;
        }

        public String get() {
            if (formatString == null) return "Not found!";
            String s = ChatColor.translateAlternateColorCodes('&', formatString);
            for (Map.Entry<String, String> stringStringEntry : modifiers.entrySet()) {
                String value = stringStringEntry.getValue();
                if (coloredInputs) value = ChatColor.translateAlternateColorCodes('&', value);
                s = s.replaceAll(String.format("\\{\\{%s\\}\\}", stringStringEntry.getKey()), value);
            }
            if (prefix && loadedPrefix != null) return loadedPrefix + s;
            return s;
        }
    }
}
