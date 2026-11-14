package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

/**
 * The {@code ListArgs} class.
 */
@CliCommand(name = "list", description = "List installed or available packages")
public final class ListArgs {

    /**
     * The {@code installed} field.
     */
    @CliOption(longName = "installed", description = "List only installed packages")
    public boolean installed;

    /**
     * The {@code notInstalled} field.
     */
    @CliOption(longName = "not-installed", description = "List only not-installed packages")
    public boolean notInstalled;

    /**
     * The {@code raw} field.
     */
    @CliOption(longName = "raw", description = "Raw output")
    public boolean raw;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public ListArgs() {
    }
}
