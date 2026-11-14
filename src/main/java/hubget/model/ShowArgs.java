package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

/**
 * The {@code ShowArgs} class.
 */
@CliCommand(name = "show", description = "Show details about a package")
public final class ShowArgs {

    /**
     * The {@code repo} field.
     */
    @CliPositional
    public String repo;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public ShowArgs() {
    }
}
