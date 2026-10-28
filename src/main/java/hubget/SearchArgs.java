package hubget;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

@CliCommand(name = "search", description = "Search GitHub for packages")
public final class SearchArgs {

    @CliPositional
    public String query;


    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;
}
