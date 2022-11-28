package hcmute.puzzle.utils;


public enum Provider {
    LOCAL, GOOGLE;
    public static Provider asProvider(String str) {
        for (Provider provider : Provider.values()) {
            if (provider.name().equalsIgnoreCase(str))
                return provider;
        }
        return null;
    }
}



