package net.averagehero.slackesv.beans.slack;

/**
 *  Example TextResponse.
 *  {"responseType":"ephemeral","text":"u dun goofed"}
 *
 */
public class TextResponse extends SlackResponse {

    private final String text;

    private TextResponse(String responseType, String text) {
        super(responseType);
        this.text = text;
    }

    // Convenience factory  methods to abstract the details of Slack's method for showing messages publicly or privately
    // in a channel.
    public static TextResponse createPrivate(String text) {
        return new TextResponse(PRIVATE, text);
    }

    public static TextResponse createPublic(String text) {
        return new TextResponse(PUBLIC, text);
    }

    public String getText() {
        return text;
    }

}
