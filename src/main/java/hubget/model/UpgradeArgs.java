package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

@CliCommand(name = "upgrade", description = "Upgrade all installed packages to their latest release")
public final class UpgradeArgs {

    @CliOption(longName = "dg-only", description = "Only upgrade packages installed by hub-get")
    public boolean dgOnly;

    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;

}
