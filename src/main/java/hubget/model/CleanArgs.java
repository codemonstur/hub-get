package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

@CliCommand(name = "clean", description = "Remove cached downloads")
public final class CleanArgs {

    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;

}
