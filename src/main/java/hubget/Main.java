package hubget;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jcli.CommandResult;
import jcli.CliParserBuilder;

import static jcli.CliParserBuilder.newCliParser;

public enum Main {;

    public static void main(final String... args) {
        try {
            final CliParserBuilder<RootArgs> root = newCliParser(RootArgs::new).name("hub-get")
                    .onErrorPrintHelpAndExit()
                    .onHelpPrintHelpAndExit()
                    .subcommand("install", "Install a package from GitHub releases", newCliParser(InstallArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("remove", "Remove an installed package", newCliParser(RemoveArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("update", "Refresh package index", newCliParser(UpdateArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("upgrade", "Upgrade installed packages", newCliParser(UpgradeArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("search", "Search GitHub for packages", newCliParser(SearchArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("list", "List packages", newCliParser(ListArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("show", "Show package details", newCliParser(ShowArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit())
                    .subcommand("clean", "Remove cached downloads", newCliParser(CleanArgs::new).onErrorPrintHelpAndExit().onHelpPrintHelpAndExit());

            final CommandResult<RootArgs> result = root.parseWithSubcommands(args);
            final RootArgs rootArgs = result.getRoot();

            if (!result.hasSubcommand()) {
                System.out.println("hub-get - an apt-get clone that installs from GitHub");
                System.out.println("Run 'hub-get --help' for usage.");
                return;
            }

            final String command = result.getSubcommand();
            switch (command) {
                case "install" -> handleInstall(result.get(InstallArgs.class), rootArgs);
                case "remove" -> handleRemove(result.get(RemoveArgs.class));
                case "update" -> handleUpdate(result.get(UpdateArgs.class));
                case "upgrade" -> handleUpgrade(result.get(UpgradeArgs.class));
                case "search" -> handleSearch(result.get(SearchArgs.class));
                case "list" -> handleList(result.get(ListArgs.class));
                case "show" -> handleShow(result.get(ShowArgs.class));
                case "clean" -> handleClean(result.get(CleanArgs.class));
                default -> System.out.println("Unknown command: " + command);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        }
    }

    private static void handleInstall(final InstallArgs args, final RootArgs root) throws Exception {
        if (args.repos == null || args.repos.isEmpty()) {
            throw new IllegalArgumentException("Missing package name. Usage: hub-get install owner/repo");
        }
        for (String repoSpec : args.repos) {
            String repo = repoSpec;
            String tag = args.tag;
            if (repo.contains("@")) {
                final int at = repo.indexOf('@');
                tag = repo.substring(at + 1);
                repo = repo.substring(0, at);
            }
            System.out.println("Installing " + repo + (tag == null ? "" : " @ " + tag) + "...");
            final var release = GitHub.release(repo, tag);
            Installer.install(repo, tag, release, args.dir);
        }
    }

    private static void handleRemove(final RemoveArgs args) throws Exception {
        if (args.repos == null || args.repos.isEmpty()) {
            throw new IllegalArgumentException("Missing package name.");
        }
        final var state = State.load();
        for (String repo : args.repos) {
            if (state.remove(repo) != null) {
                System.out.println("Removed " + repo + " from tracking.");
            } else {
                System.out.println("Package not tracked: " + repo);
            }
        }
        State.save(state);
    }

    private static void handleUpdate(final UpdateArgs args) throws Exception {
        if (args.quiet) {
            // quiet
        }
        System.out.println("Updating package index...");
        if (!args.reposOnly) {
            final var state = State.load();
            for (String repo : state.keySet()) {
                try {
                    final var rel = GitHub.release(repo, null);
                    final String latest = rel.get("tag_name").getAsString();
                    final String current = state.get(repo).tag;
                    if (!latest.equals(current)) {
                        System.out.println(repo + ": update available (" + current + " -> " + latest + ")");
                    } else {
                        System.out.println(repo + ": up to date");
                    }
                } catch (Exception e) {
                    System.out.println(repo + ": failed to check (" + e.getMessage() + ")");
                }
            }
        }
        System.out.println("Update complete.");
    }

    private static void handleUpgrade(final UpgradeArgs args) throws Exception {
        final var state = new java.util.LinkedHashMap<>(State.load());
        for (String repo : state.keySet()) {
            try {
                final var rel = GitHub.release(repo, null);
                final String latest = rel.get("tag_name").getAsString();
                final String current = state.get(repo).tag;
                if (!latest.equals(current)) {
                    System.out.println("Upgrading " + repo + " (" + current + " -> " + latest + ")...");
                    Installer.install(repo, null, rel, state.get(repo).installDir);
                } else {
                    System.out.println(repo + ": already up to date");
                }
            } catch (Exception e) {
                System.out.println(repo + ": failed (" + e.getMessage() + ")");
            }
        }
    }

    private static void handleSearch(final SearchArgs args) throws Exception {
        if (args.query == null || args.query.isBlank()) {
            throw new IllegalArgumentException("Missing search query.");
        }
        final var results = GitHub.searchRepos(args.query);
        for (String r : results) System.out.println(r);
        if (results.isEmpty()) System.out.println("No results.");
    }

    private static void handleList(final ListArgs args) throws Exception {
        final var state = State.load();
        if (state.isEmpty()) {
            System.out.println("No packages installed/tracked.");
            return;
        }
        for (var entry : state.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().tag + " (" + entry.getValue().asset + ")");
        }
    }

    private static void handleShow(final ShowArgs args) throws Exception {
        if (args.repo == null || args.repo.isBlank()) {
            throw new IllegalArgumentException("Missing package name.");
        }
        final var obj = GitHub.getJson("https://api.github.com/repos/" + args.repo);
        System.out.println(obj.get("full_name").getAsString());
        if (obj.has("description") && !obj.get("description").isJsonNull()) System.out.println(obj.get("description").getAsString());
        System.out.println("Stars: " + obj.get("stargazers_count").getAsInt());
        System.out.println("URL: " + obj.get("html_url").getAsString());
    }

    private static void handleClean(final CleanArgs args) throws Exception {
        final Path cache = Paths.get(System.getProperty("user.home"), ".hub-get", "cache");
        if (Files.exists(cache)) {
            try (var s = Files.walk(cache)) {
                s.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (Exception ignored) { }
                });
            }
            System.out.println("Cache cleared.");
        } else {
            System.out.println("Nothing to clean.");
        }
    }

}
