package hubget;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

@CliCommand(name = "hub-get", description = "An apt-get clone that installs packages from GitHub releases")
public final class RootArgs {

    @CliOption(name = 'v', longName = "verbose", description = "Print verbose output")
    public boolean verbose;

    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;

    @CliOption(name = 'V', longName = "version", isVersion = true, description = "Print version")
    public boolean version;

}
