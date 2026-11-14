package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

/**
 * The {@code UpdateArgs} class.
 */
@CliCommand(name = "update", description = "Refresh the local package index from GitHub")
public final class UpdateArgs {

    /**
     * The {@code reposOnly} field.
     */
    @CliOption(longName = "repos-only", description = "Only update repositories, do not check installed packages")
    public boolean reposOnly;

    /**
     * The {@code quiet} field.
     */
    @CliOption(longName = "quiet", description = "Suppress progress output")
    public boolean quiet;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public UpdateArgs() {
    }
}
