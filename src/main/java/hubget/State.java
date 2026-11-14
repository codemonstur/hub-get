package hubget;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@code State} class.
 */
public final class State {

    /**
     * The {@code Package} record.
     *
     * @param repo the repo
     * @param tag the tag
     * @param asset the asset
     * @param installDir the install dir
     * @param installedAt the installed at
     */
    public record Package(String repo, String tag, String asset, String installDir, long installedAt) {
        /**
         * Creates a new instance.
         *
         * @param repo the repo
         * @param tag the tag
         * @param asset the asset
         * @param installDir the install dir
         */
        public Package(final String repo, final String tag, final String asset, final String installDir) {
            this(repo, tag, asset, installDir, System.currentTimeMillis());
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path stateFile() {
        return Paths.get(System.getProperty("user.home"), ".hub-get", "installed.json");
    }

    /**
     * Returns the result of {@code load}.
     *
     * @return the result
     */
    public static Map<String, Package> load() {
        final Path file = stateFile();
        if (!Files.exists(file)) return new LinkedHashMap<>();
        try (Reader reader = Files.newBufferedReader(file)) {
            final Map<String, Package> loaded = GSON.fromJson(reader, new TypeToken<Map<String, Package>>() { }.getType());
            return loaded == null ? new LinkedHashMap<>() : new LinkedHashMap<>(loaded);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Performs the {@code save} operation.
     *
     * @param packages the packages
     */
    public static void save(final Map<String, Package> packages) {
        final Path file = stateFile();
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(packages, writer);
            }
        } catch (Exception ignored) {
        }
    }


    /**
     * Creates a new instance.
     */
    public State() {
    }
}
