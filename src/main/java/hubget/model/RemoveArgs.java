package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

import java.util.List;

/**
 * The {@code RemoveArgs} class.
 */
@CliCommand(name = "remove", description = "Remove one or more installed packages")
public final class RemoveArgs {

    /**
     * The {@code repos} field.
     */
    @CliPositional
    public List<String> repos;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public RemoveArgs() {
    }
}
