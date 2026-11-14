package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

/**
 * The {@code SearchArgs} class.
 */
@CliCommand(name = "search", description = "Search GitHub for packages")
public final class SearchArgs {

    /**
     * The {@code query} field.
     */
    @CliPositional
    public String query;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public SearchArgs() {
    }
}
