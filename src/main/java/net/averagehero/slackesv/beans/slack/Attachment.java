package net.averagehero.slackesv.beans.slack;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Example request JSON:
    "attachments": [
        {
            "fallback": "Required plain-text summary of the attachment.",
            "color": "#36a64f",
            "title": "John 1:3",
            "title_link": "https://api.esv.org/v3/passage/text/?q=1001001-1001001,43001001-43001001",
            "text": "All things were made through him, and without him was not any thing made that was made."
        }
    ]
 */
public class Attachment {

    private String fallback;

    private String color;

    private String title;

    @SerializedName("title_link")
    private String titleLink;

    private String text;

    private Attachment() {}

    public String getFallback() {
        return fallback;
    }

    public Attachment setFallback(String fallback) {
        this.fallback = fallback;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Attachment setColor(String color) {
        this.color = color;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Attachment setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitleLink() {
        return titleLink;
    }

    public Attachment setTitleLink(String titleLink) {
        this.titleLink = titleLink;
        return this;
    }

    public String getText() {
        return text;
    }

    public Attachment setText(String text) {
        this.text = text;
        return this;
    }

    public static Attachment create() {
        return new Attachment();
    }
}
