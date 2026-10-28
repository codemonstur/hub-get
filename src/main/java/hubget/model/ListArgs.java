package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

@CliCommand(name = "list", description = "List installed or available packages")
public final class ListArgs {

    @CliOption(longName = "installed", description = "List only installed packages")
    public boolean installed;

    @CliOption(longName = "not-installed", description = "List only not-installed packages")
    public boolean notInstalled;

    @CliOption(longName = "raw", description = "Raw output")
    public boolean raw;

    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;

}
