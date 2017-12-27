package controller;

import org.apache.commons.cli.Options;

public class Command {
	private String cmd;
	private Options opts;

	public Command(String pCmd, Options pOpts) {
		cmd = pCmd;
		opts = pOpts;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String pCmd) {
		cmd = pCmd;
	}

	public Options getOpts() {
		return opts;
	}

	public void setOpts(Options pOpts) {
		opts = pOpts;
	}

}
