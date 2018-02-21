package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class FeedbackCommandNode extends CommandNode {

    private float feedback;
    private String feedbackResponse;
    private final List<String> rejectKeys;

    public FeedbackCommandNode(Session session, String [] hooks, String feedbackResponse, float feedback) {
        this(session, hooks, feedbackResponse, feedback, null);
    }

    public FeedbackCommandNode(Session session, String [] hooks, String feedbackResponse, float feedback, List<String> rejectKeys) {
        super(session, hooks, Hook.Match.All);
        this.feedbackResponse = feedbackResponse;
        this.feedback = feedback;

        if (rejectKeys!=null && rejectKeys.size()!=4) throw new IllegalArgumentException("Reject keys must have 4 elements");
        this.rejectKeys = rejectKeys;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return super.matched(messageObject) && session.lastEntry() != null;
    }

    @Override
    public String execute(MessageObject messageObject) {

        Session.Entry lastActiveEntry = session.lastEntry();

        if (rejectKeys!=null) {

            if (session.learning) {
                session.insert(new RejectProblemCommandNode(session, lastActiveEntry, rejectKeys.get(0), rejectKeys.get(1), rejectKeys.get(2), rejectKeys.get(3)));
                feedbackResponse = lastActiveEntry.messageObject.toString().trim() + " ?";
            }

        } else if (feedback > 0) {

            Node lastActiveNode = lastActiveEntry.node;

            Node newNode = session.context.build(lastActiveEntry.messageObject);

            if (!lastActiveNode.coverHooks(newNode)) {
               lastActiveNode.addHook(newNode);
            }

            session.context.save();
        }

        lastActiveEntry.node.feedback(lastActiveEntry.messageObject, feedback);

        session.clearLastEntry();
        session.clearPool();

        return feedbackResponse;
    }
}
