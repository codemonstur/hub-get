package hubget;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hubget.model.InstallArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HubGetTest {

    @Test
    void installer_osKeyAndArchKey() {
        assertNotNull(Installer.osKey());
        assertNotNull(Installer.archKey());
    }

    @Test
    void installer_installDir_override() {
        assertEquals(Path.of("/tmp/x"), Installer.installDir("/tmp/x"));
    }

    @Test
    void installer_selectAsset() {
        final JsonArray assets = new JsonArray();
        final JsonObject a = new JsonObject();
        a.addProperty("name", "tool-linux-amd64.tar.gz");
        a.addProperty("browser_download_url", "http://example.com/x");
        a.addProperty("size", 10);
        assets.add(a);
        final Installer.SelectedAsset sel = Installer.selectAsset(assets);
        assertNotNull(sel);
        assertEquals("tool-linux-amd64.tar.gz", sel.name());
    }

    @Test
    void installer_selectAsset_nullReturnsNull() {
        org.junit.jupiter.api.Assertions.assertNull(Installer.selectAsset(null));
    }

    @Test
    void state_loadMissing() {
        // stateFile points to user.home; load should not throw
        assertNotNull(State.load());
    }

    @Test
    void gitHub_tokenAndRequest() {
        final var req = GitHub.request("https://example.com");
        assertNotNull(req);
    }
}
