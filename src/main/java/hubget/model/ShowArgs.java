package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

@CliCommand(name = "show", description = "Show details about a package")
public final class ShowArgs {

    @CliPositional
    public String repo;

    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;

}
