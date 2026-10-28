package hubget;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

import java.util.List;

@CliCommand(name = "remove", description = "Remove one or more installed packages")
public final class RemoveArgs {

    @CliPositional
    public List<String> repos;


    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;
}
