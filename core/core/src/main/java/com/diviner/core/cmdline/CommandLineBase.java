package com.diviner.core.cmdline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public abstract class CommandLineBase {

	private boolean willExit = false;

	public int run(String[] args) throws Exception {
		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				willExit = true;
				System.out.println("system exit!!!");
				try {
					mainThread.join(1000);
				} catch (InterruptedException e) {
					// nothing useful to do here
				}
			}
		});

		Options options = getOptions();
		CommandLine cmd;
		CommandLineParser parser = new GnuParser();
		cmd = parser.parse(options, args);
		processOptions(cmd);

		int result = run(cmd);

		return result;
	}

	protected abstract int run(CommandLine cmd) throws Exception;

	protected Options getOptions() {
		Options options = new Options();

		options.addOption(OptionBuilder.withLongOpt("help")
				.withDescription("Print help").create());

		return options;
	}

	protected void processOptions(CommandLine cmd) throws Exception {
	}
}
