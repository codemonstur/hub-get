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

public final class State {

    public static final class Package {
        public String repo;
        public String tag;
        public String asset;
        public String installDir;
        public long installedAt;

        public Package() { }

        public Package(final String repo, final String tag, final String asset, final String installDir) {
            this.repo = repo;
            this.tag = tag;
            this.asset = asset;
            this.installDir = installDir;
            this.installedAt = System.currentTimeMillis();
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path stateFile() {
        return Paths.get(System.getProperty("user.home"), ".hub-get", "installed.json");
    }

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

}
