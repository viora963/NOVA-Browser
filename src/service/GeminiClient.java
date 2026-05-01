package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Minimal client for the Google Gemini REST API.
 *
 * Uses the public generativelanguage.googleapis.com endpoint.
 * Requests are sent asynchronously via java.net.http.HttpClient so the
 * JavaFX application thread is never blocked.
 *
 * Get a free API key at https://aistudio.google.com/app/apikey
 */
public class GeminiClient {

    private static final String MODEL = "gemini-2.5-flash";
    private static final String ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    /** A single message in a conversation. */
    public static class Message {
        public final String role;   // "user" or "model"
        public final String text;

        public Message(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }

    /**
     * Sends the conversation to Gemini and returns the model's reply asynchronously.
     * The returned future completes with the assistant text, or completes
     * exceptionally on a network or API error.
     */
    public CompletableFuture<String> chat(String apiKey, List<Message> conversation) {
        if (apiKey == null || apiKey.isBlank()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("Gemini API key is not set. Open Settings to add one.")
            );
        }

        String body = buildRequestBody(conversation);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::parseResponse);
    }

    /** Convenience for one-shot prompts. */
    public CompletableFuture<String> ask(String apiKey, String prompt) {
        List<Message> single = new ArrayList<>();
        single.add(new Message("user", prompt));
        return chat(apiKey, single);
    }

    // ───────────── JSON construction (no external dependencies) ─────────────

    private String buildRequestBody(List<Message> conversation) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"contents\":[");
        for (int i = 0; i < conversation.size(); i++) {
            if (i > 0) sb.append(',');
            Message m = conversation.get(i);
            sb.append("{\"role\":\"").append(jsonEscape(m.role)).append("\",")
              .append("\"parts\":[{\"text\":\"").append(jsonEscape(m.text)).append("\"}]}");
        }
        sb.append("],\"generationConfig\":{\"temperature\":0.7,\"maxOutputTokens\":2048}}");
        return sb.toString();
    }

    private String parseResponse(HttpResponse<String> resp) {
        String body = resp.body();
        if (resp.statusCode() != 200) {
            // Surface the API error message to the user instead of a cryptic "HTTP 400".
            String apiError = extractErrorMessage(body);
            throw new RuntimeException(
                "Gemini API error (" + resp.statusCode() + "): " +
                (apiError != null ? apiError : body)
            );
        }
        String text = extractText(body);
        if (text == null || text.isBlank()) {
            throw new RuntimeException("Gemini returned an empty response.");
        }
        return text;
    }

    /**
     * Pulls the first candidate's text out of the JSON without bringing in a JSON
     * library. The structure we care about is:
     *   { "candidates": [ { "content": { "parts": [ { "text": "..." } ] } } ] }
     */
    private String extractText(String json) {
        // Find the first "text": "..." that appears after "candidates"
        int candIdx = json.indexOf("\"candidates\"");
        if (candIdx < 0) return null;
        int textIdx = json.indexOf("\"text\"", candIdx);
        if (textIdx < 0) return null;

        int colon = json.indexOf(':', textIdx);
        if (colon < 0) return null;
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;

        // Walk forward, respecting backslash escapes, until we hit the closing quote.
        StringBuilder out = new StringBuilder();
        for (int i = firstQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case 'n':  out.append('\n'); break;
                    case 'r':  out.append('\r'); break;
                    case 't':  out.append('\t'); break;
                    case '"':  out.append('"');  break;
                    case '\\': out.append('\\'); break;
                    case '/':  out.append('/');  break;
                    case 'u':
                        if (i + 5 < json.length()) {
                            try {
                                int code = Integer.parseInt(json.substring(i + 2, i + 6), 16);
                                out.append((char) code);
                                i += 4;
                            } catch (NumberFormatException ignored) {
                                out.append(next);
                            }
                        }
                        break;
                    default: out.append(next);
                }
                i++;
            } else if (c == '"') {
                return out.toString();
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private String extractErrorMessage(String json) {
        int idx = json.indexOf("\"message\"");
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;
        int endQuote = json.indexOf('"', firstQuote + 1);
        // Find the actual closing quote, accounting for escapes
        while (endQuote > 0 && json.charAt(endQuote - 1) == '\\') {
            endQuote = json.indexOf('"', endQuote + 1);
        }
        if (endQuote < 0) return null;
        return json.substring(firstQuote + 1, endQuote);
    }

    private String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                case '\b': out.append("\\b");  break;
                case '\f': out.append("\\f");  break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
