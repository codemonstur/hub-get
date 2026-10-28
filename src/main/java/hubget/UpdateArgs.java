package hubget;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

@CliCommand(name = "update", description = "Refresh the local package index from GitHub")
public final class UpdateArgs {

    @CliOption(longName = "repos-only", description = "Only update repositories, do not check installed packages")
    public boolean reposOnly;

    @CliOption(longName = "quiet", description = "Suppress progress output")
    public boolean quiet;


    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Print this help")
    public boolean help;
}
