package greencity.enums;

public enum NotificationContentType  {
    LIKE_NEWS_EN(" liked your news ", "en"),
    LIKE_NEWS_UA(" вподобав Вашу новину ", "ua");

    private final String description;
    private final String languageCode;

    NotificationContentType(String description, String languageCode) {
        this.description = description;
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public String toString() {
        return description;
    }
}
