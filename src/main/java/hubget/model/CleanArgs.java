package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

/**
 * The {@code CleanArgs} class.
 */
@CliCommand(name = "clean", description = "Remove cached downloads")
public final class CleanArgs {

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public CleanArgs() {
    }
}
