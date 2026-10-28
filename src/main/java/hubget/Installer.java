package hubget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;

public final class Installer {

    public record SelectedAsset(String name, String url, long size) { }

    public static String osKey() {
        final String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("linux")) return "linux";
        if (os.contains("mac") || os.contains("darwin")) return "darwin";
        if (os.contains("win")) return "windows";
        return "linux";
    }

    public static String archKey() {
        final String arch = System.getProperty("os.arch", "").toLowerCase();
        if (arch.contains("aarch64") || arch.contains("arm64")) return "arm64";
        if (arch.contains("amd64") || arch.contains("x86_64")) return "amd64";
        if (arch.contains("x86") || arch.contains("i386") || arch.contains("i686")) return "386";
        return arch;
    }

    public static SelectedAsset selectAsset(final JsonArray assets) {
        if (assets == null || assets.isEmpty()) return null;
        final String os = osKey();
        final String arch = archKey();
        final List<SelectedAsset> candidates = new ArrayList<>();
        for (JsonElement e : assets) {
            final JsonObject o = e.getAsJsonObject();
            final String name = o.get("name").getAsString();
            final String url = o.get("browser_download_url").getAsString();
            final long size = o.has("size") ? o.get("size").getAsLong() : 0L;
            candidates.add(new SelectedAsset(name, url, size));
        }
        return candidates.stream().max(Comparator.comparingInt(a -> score(a.name.toLowerCase(), os, arch))).orElse(candidates.get(0));
    }

    private static int score(final String name, final String os, final String arch) {
        int s = 0;
        if (name.contains(os)) s += 2;
        if (name.contains(arch)) s += 2;
        if (os.equals("linux") && name.endsWith(".deb")) s += 3;
        if (name.endsWith(".tar.gz") || name.endsWith(".tgz") || name.endsWith(".zip") || name.endsWith(".tar.xz")) s += 1;
        if (name.contains("musl") && os.equals("linux")) s += 0;
        return s;
    }

    public static Path installDir(final String override) {
        if (override != null && !override.isBlank()) return Paths.get(override);
        return Paths.get(System.getProperty("user.home"), ".local", "bin");
    }

    public static void install(final String repo, final String tag, final JsonObject release, final String dirOverride) throws Exception {
        final JsonArray assets = release.has("assets") ? release.getAsJsonArray("assets") : new JsonArray();
        final SelectedAsset asset = selectAsset(assets);
        if (asset == null) {
            throw new RuntimeException("No release asset found for " + repo);
        }
        final Path installDir = installDir(dirOverride);
        Files.createDirectories(installDir);
        final Path cacheDir = Paths.get(System.getProperty("user.home"), ".hub-get", "cache");
        Files.createDirectories(cacheDir);
        final Path downloaded = cacheDir.resolve(asset.name);
        System.out.println("Downloading " + asset.name + " (" + asset.size + " bytes)...");
        GitHub.download(asset.url, downloaded);

        final String lower = asset.name.toLowerCase();
        if (lower.endsWith(".deb")) {
            run(new String[]{"sudo", "dpkg", "-i", downloaded.toString()});
        } else if (lower.endsWith(".tar.gz") || lower.endsWith(".tgz") || lower.endsWith(".tar.xz") || lower.endsWith(".zip")) {
            // Best effort: if a single executable inside, extract to install dir is not implemented; just copy archive? 
            // We'll attempt unzip/tar extraction.
            extract(downloaded, installDir);
        } else {
            final Path dest = installDir.resolve(asset.name);
            Files.move(downloaded, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            makeExecutable(dest);
            System.out.println("Installed to " + dest);
        }

        final String resolvedTag = release.has("tag_name") ? release.get("tag_name").getAsString() : (tag == null ? "latest" : tag);
        final State.Package pkg = new State.Package(repo, resolvedTag, asset.name, installDir.toString());
        final var state = State.load();
        state.put(repo, pkg);
        State.save(state);
        System.out.println("Registered " + repo + " " + resolvedTag);
    }

    private static void makeExecutable(final Path file) {
        try {
            file.toFile().setExecutable(true, false);
        } catch (Exception ignored) {
        }
        try {
            Files.setPosixFilePermissions(file, Set.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE,
                    GROUP_READ, GROUP_EXECUTE, OTHERS_READ, OTHERS_EXECUTE));
        } catch (Exception ignored) {
        }
    }

    private static void run(final String[] cmd) throws Exception {
        final ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        final Process p = pb.start();
        final int code = p.waitFor();
        if (code != 0) throw new RuntimeException("Command failed with exit code " + code + ": " + String.join(" ", cmd));
    }

    private static void extract(final Path archive, final Path dest) throws Exception {
        final String lower = archive.toString().toLowerCase();
        if (lower.endsWith(".zip")) {
            run(new String[]{"unzip", "-o", archive.toString(), "-d", dest.toString()});
        } else if (lower.endsWith(".tar.gz") || lower.endsWith(".tgz")) {
            run(new String[]{"tar", "-xzf", archive.toString(), "-C", dest.toString()});
        } else if (lower.endsWith(".tar.xz")) {
            run(new String[]{"tar", "-xJf", archive.toString(), "-C", dest.toString()});
        }
        System.out.println("Extracted to " + dest);
    }

}
