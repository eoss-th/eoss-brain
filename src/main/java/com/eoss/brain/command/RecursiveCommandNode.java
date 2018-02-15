package com.eoss.brain.command;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.command.http.GetCommandNode;
import com.eoss.brain.command.talk.ConfirmProblemCommandNode;
import com.eoss.brain.command.talk.RejectProblemCommandNode;
import com.eoss.brain.command.talk.LowConfidenceProblemCommandNode;
import com.eoss.brain.command.talk.BizTalkCommandNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class RecursiveCommandNode extends CommandNode {

    private CommandNode commandNode;

    private List<Class<? extends CommandNode>> ignoreCommandList = new ArrayList<>(
            Arrays.asList(
                    RecursiveCommandNode.class,
                    LowConfidenceProblemCommandNode.class,
                    RejectProblemCommandNode.class,
                    BizTalkCommandNode.class,
                    ConfirmProblemCommandNode.class,
                    GetCommandNode.class,
                    AdminCommandNode.class));

    public RecursiveCommandNode(CommandNode commandNode) {
        this(commandNode, null);
    }

    public RecursiveCommandNode(CommandNode commandNode, List<Class<? extends CommandNode>> ignoreCommandList) {

        super(commandNode.session);
        this.commandNode = commandNode;

        if (ignoreCommandList!=null) {
            this.ignoreCommandList.addAll(ignoreCommandList);
        }
    }

    public String execute(MessageObject messageObject) {

        String response = commandNode.execute(messageObject);

        if (!session.hasProblem()) {

            String recursiveResponse = null;
            for (CommandNode c: session.commandList) {
                if (ignoreCommandList.contains(c.getClass())) continue;
                if (c.matched(MessageObject.build(messageObject, response))) {

                    recursiveResponse = c.execute(
                            MessageObject.build(messageObject,
                                    session.lastEntry().node.clean(messageObject.toString())));
                    break;
                }
            }

            if (recursiveResponse!=null) {
                if (session.context.listener !=null) {
                    session.context.listener.callback(
                            new NodeEvent(this,
                                    MessageObject.build(messageObject, recursiveResponse),
                                    NodeEvent.Event.Recursive));
                }
            }
        }

        return response;
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        return commandNode.matched(messageObject);
    }

}
