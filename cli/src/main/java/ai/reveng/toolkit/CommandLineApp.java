package ai.reveng.toolkit;

import org.apache.commons.cli.*;

public class CommandLineApp {
	public static void main(String[] args) {
		System.out.println("RevEng.AI Toolkit Command Line");

		Options options = new Options();

		Option binary = new Option("b", "binary", true,
				"Path of binary to analyse, use ./path:{exec_format} to specify executable format e.g. ./path:raw-x86_64");
		options.addOption(binary);
		Option dir = new Option("D", "dir", true, "Path of directory to recursively analyse");
		options.addOption(dir);
		Option help = new Option("h", "help", false, "show help message");
		options.addOption(help);

		if (args.length == 0) {
			printHelp(options);
			return;
		}

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			printHelp(options);
			System.exit(1);
		}

		if (cmd.hasOption("help")) {
			printHelp(options);
			return;
		}

	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("reait", options);
	}
}
