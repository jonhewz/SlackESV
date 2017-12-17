package net.averagehero.slackesv.beans.slack;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Example request JSON:
    "attachments": [
        {
            "fallback": "Required plain-text summary of the attachment.",
            "text": "All things were made through him, and without him was not any thing made that was made."
            "footer": "https://api.esv.org/v3/passage/text/?q=1001001-1001001,43001001-43001001",
        }
    ]
 */
public class Attachment {

    private String fallback;

    private String text;

    private String footer;

    private Attachment() {}

    public String getFallback() {
        return fallback;
    }

    public Attachment setFallback(String fallback) {
        this.fallback = fallback;
        return this;
    }

    public String getText() {
        return text;
    }

    public Attachment setText(String text) {
        this.text = text;
        return this;
    }

    public String getFooter() {
        return footer;
    }

    public Attachment setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public static Attachment create() {
        return new Attachment();
    }
}
