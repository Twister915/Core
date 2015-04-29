package net.cogzmc.core.player.message.parser;

import java.util.regex.Pattern;

public enum ParserRegex {

	EMPHASIS("\\*((?!\\s)[^\\*]+(?<!\\s))\\*"),
	ITALIC("\\/((?:[^ \\*])[^\\/]+(?:[^ \\*]))\\/"),
	UNDERLINE("_((?!\\s)[^_]+(?<!\\s))_"),
	STRIKETHROUGH("~(?!\\s)([^~]+)(?<!\\s)~"),
	LINK("\\[(.+)\\]\\((.+)\\)"),
	COMMAND("\\{\\/(.+)\\}\\((.+)\\)"),
	SUGGEST_COMMAND("\\{\\*\\/(.+)\\*\\}\\((.+)\\)"),
	HOVER("\\[\\[(.+)\\]\\]\\((.+)\\)");

	private Pattern pattern;

	ParserRegex(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	public Pattern getPattern() {
		return pattern;
	}

}
