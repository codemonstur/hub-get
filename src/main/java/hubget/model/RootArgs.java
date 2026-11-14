package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

/**
 * The {@code RootArgs} class.
 */
@CliCommand(name = "hub-get", description = "An apt-get clone that installs packages from GitHub releases")
public final class RootArgs {

    /**
     * The {@code verbose} field.
     */
    @CliOption(name = 'v', longName = "verbose", description = "Print verbose output")
    public boolean verbose;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;

    /**
     * The {@code version} field.
     */
    @CliOption(name = 'V', longName = "version", isVersion = true, description = "Print version")
    public boolean version;


    /**
     * Creates a new instance.
     */
    public RootArgs() {
    }
}
