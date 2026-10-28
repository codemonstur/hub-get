package hubget;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

import java.util.List;

@CliCommand(name = "install", description = "Install one or more packages from GitHub releases")
public final class InstallArgs {

    @CliOption(longName = "tag", description = "Install a specific release tag")
    public String tag;

    @CliOption(longName = "dir", description = "Install directory")
    public String dir;

    @CliPositional
    public List<String> repos;


    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;
}
