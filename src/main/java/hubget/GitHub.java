package hubget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public final class GitHub {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static String token() {
        final String t = System.getenv("HUB_GET_TOKEN");
        if (t != null && !t.isBlank()) return t;
        final String g = System.getenv("GITHUB_TOKEN");
        if (g != null && !g.isBlank()) return g;
        return System.getenv("GH_TOKEN");
    }

    public static HttpRequest.Builder request(final String url) {
        final HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "hub-get");
        final String t = token();
        if (t != null && !t.isBlank()) {
            builder.header("Authorization", "Bearer " + t);
        }
        return builder;
    }

    public static JsonObject getJson(final String url) throws Exception {
        final HttpRequest req = request(url).GET().build();
        final HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("GitHub request failed: " + res.statusCode() + " " + res.body());
        }
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }

    public static JsonArray getArray(final String url) throws Exception {
        final HttpRequest req = request(url).GET().build();
        final HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("GitHub request failed: " + res.statusCode() + " " + res.body());
        }
        return JsonParser.parseString(res.body()).getAsJsonArray();
    }

    public static JsonObject release(final String repo, final String tag) throws Exception {
        final String url = (tag == null || tag.isBlank())
                ? "https://api.github.com/repos/" + repo + "/releases/latest"
                : "https://api.github.com/repos/" + repo + "/releases/tags/" + tag;
        return getJson(url);
    }

    public static List<String> searchRepos(final String query) throws Exception {
        final String url = "https://api.github.com/search/repositories?q=" + java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8) + "&per_page=20";
        final JsonObject obj = getJson(url);
        final JsonArray items = obj.getAsJsonArray("items");
        final List<String> result = new ArrayList<>();
        for (JsonElement e : items) {
            final JsonObject o = e.getAsJsonObject();
            result.add(o.get("full_name").getAsString() + " - " + (o.has("description") && !o.get("description").isJsonNull() ? o.get("description").getAsString() : ""));
        }
        return result;
    }

    public static void download(final String url, final java.nio.file.Path target) throws Exception {
        final HttpRequest req = request(url).GET().build();
        final HttpResponse<java.io.InputStream> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofInputStream());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("Download failed: " + res.statusCode());
        }
        try (var in = res.body()) {
            java.nio.file.Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
