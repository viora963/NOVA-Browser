package util;

public class UrlUtils {

    private static final String[] PROTOCOLS = {"http://", "https://", "file://", "ftp://"};

    public static String normalize(String input) {
        if (input == null || input.isBlank()) return "about:blank";
        String trimmed = input.trim();

        for (String proto : PROTOCOLS) {
            if (trimmed.toLowerCase().startsWith(proto)) return trimmed;
        }

        if (trimmed.startsWith("about:")) return trimmed;

        if (!trimmed.contains(" ") && trimmed.contains(".")) {
            return "https://" + trimmed;
        }

        return "https://duckduckgo.com/?q=" + trimmed.replace(" ", "+");
    }

    public static boolean isSecure(String url) {
        return url != null && url.toLowerCase().startsWith("https://");
    }
}
