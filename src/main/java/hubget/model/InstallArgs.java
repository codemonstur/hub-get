package hubget.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;

import java.util.List;

/**
 * The {@code InstallArgs} class.
 */
@CliCommand(name = "install", description = "Install one or more packages from GitHub releases")
public final class InstallArgs {

    /**
     * The {@code tag} field.
     */
    @CliOption(longName = "tag", description = "Install a specific release tag")
    public String tag;

    /**
     * The {@code dir} field.
     */
    @CliOption(longName = "dir", description = "Install directory")
    public String dir;

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
    public InstallArgs() {
    }
}
