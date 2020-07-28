package com.brainy.service;

import com.brainy.MessageObject;

public interface RequestObject {
    MessageObject messageObject();
    String sessionId();
    String contextName();
}
