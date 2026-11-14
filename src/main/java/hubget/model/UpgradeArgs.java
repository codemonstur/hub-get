package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

/**
 * The {@code UpgradeArgs} class.
 */
@CliCommand(name = "upgrade", description = "Upgrade all installed packages to their latest release")
public final class UpgradeArgs {

    /**
     * The {@code dgOnly} field.
     */
    @CliOption(longName = "dg-only", description = "Only upgrade packages installed by hub-get")
    public boolean dgOnly;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public UpgradeArgs() {
    }
}
