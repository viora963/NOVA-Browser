package service;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Lightweight settings store for NOVA.
 * Persists configuration to ~/.nova/config.properties so the API key
 * survives between runs without ever being checked into source control.
 */
public class Settings {

    private static final String DIR_NAME = ".nova";
    private static final String FILE_NAME = "config.properties";

    private static final String KEY_GEMINI = "gemini.api.key";

    private final Path configFile;
    private final Properties props = new Properties();

    public Settings() {
        Path home = Paths.get(System.getProperty("user.home"));
        Path dir = home.resolve(DIR_NAME);
        this.configFile = dir.resolve(FILE_NAME);
        load();
    }

    private void load() {
        if (!Files.exists(configFile)) return;
        try (InputStream in = Files.newInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("[NOVA] Could not load settings: " + e.getMessage());
        }
    }

    private void save() {
        try {
            Files.createDirectories(configFile.getParent());
            try (OutputStream out = Files.newOutputStream(configFile)) {
                props.store(out, "NOVA browser settings");
            }
        } catch (IOException e) {
            System.err.println("[NOVA] Could not save settings: " + e.getMessage());
        }
    }

    public String getGeminiApiKey() {
        String v = props.getProperty(KEY_GEMINI);
        return (v == null) ? "" : v.trim();
    }

    public void setGeminiApiKey(String key) {
        if (key == null) key = "";
        props.setProperty(KEY_GEMINI, key.trim());
        save();
    }

    public boolean hasGeminiApiKey() {
        return !getGeminiApiKey().isEmpty();
    }
}
