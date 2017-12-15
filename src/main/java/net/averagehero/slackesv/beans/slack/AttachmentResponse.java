package net.averagehero.slackesv.beans.slack;

import java.util.ArrayList;
import java.util.List;

/**
 * Example JSON AttachmentResponse.
    {
        "responseType":"in_channel",
        "attachments": [
            {
                "fallback": "John 1:3 | All things were made through him, and without him was not any thing made that was made.",
                "color": "#36a64f",
                "title": "John 1:3",
                "title_link": "https://api.esv.org/v3/passage/text/?q=43001003-43001003",
                "text": "All things were made through him, and without him was not any thing made that was made."
            }
        ]
    }
 */
public class AttachmentResponse extends SlackResponse {

    private final List<Attachment> attachments;

    private AttachmentResponse(String responseType, Attachment attachment) {
        super(responseType);

        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(attachment);
        this.attachments = attachments;
    }

    // Convenience factory  methods to abstract the details of Slack's method for showing messages publicly or privately
    // in a channel.
    public static AttachmentResponse createPrivate(Attachment attachment) {
        return new AttachmentResponse(PRIVATE, attachment);
    }

    public static AttachmentResponse createPublic(Attachment attachment) {
        return new AttachmentResponse(PUBLIC, attachment);
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
