package com.eoss.brain.net;

import com.eoss.brain.NodeEvent;

import java.io.Serializable;

/**
 * Created by eossth on 9/19/2017 AD.
 */
public interface SessionListener extends Serializable{
    void callback(NodeEvent nodeEvent);
}
