package com.eoss.brain.command.talk;

public class Choice {
    public final String parent;
    public final String label;
    public final String imageURL;
    public final String linkURL;

    public Choice(String parent, String label, String imageURL, String linkURL) {
        this.parent = parent;
        this.label = label;
        this.imageURL = imageURL;
        this.linkURL = linkURL;
    }

    public boolean isLabel() {
        return imageURL == null && linkURL == null;
    }

    public boolean isImageLabel() {
        return imageURL != null && linkURL == null;
    }

    public boolean isLinkLabel() {
        return imageURL == null && linkURL != null;
    }

    public boolean isImageLinkLabel() {
        return imageURL != null && linkURL != null;
    }
}
