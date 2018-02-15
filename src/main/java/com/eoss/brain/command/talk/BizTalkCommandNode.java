package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import com.eoss.brain.NodeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class BizTalkCommandNode extends CommandNode {

    private final List<String> lowConfidenceKeys;
    private final List<String> confirmKeys;
    private final List<String> cancelKeys;
    private final String cancelMsg;
    private final List<String> confirmMsg;
    private float MIN_LOW = 0.05f;
    private float Percentile = 0.00f;
    public BizTalkCommandNode(Session session, List<String> lowConfidenceKeys, List<String> confirmKeys, List<String> cancelKeys, String cancelMsg, List<String> confirmMsg) {
        super(session);
        if (lowConfidenceKeys ==null|| lowConfidenceKeys.size()!=4) throw new IllegalArgumentException("lowConfidenceKeys must have 4 elements");
        this.lowConfidenceKeys = lowConfidenceKeys;

        if (confirmKeys ==null|| confirmKeys.isEmpty()) throw new IllegalArgumentException("confirmKeys must have elements");
        this.confirmKeys = confirmKeys;

        if (cancelKeys ==null|| cancelKeys.isEmpty()) throw new IllegalArgumentException("cancelKeys must have elements");
        this.cancelKeys = cancelKeys;

        this.cancelMsg = cancelMsg;

        if (confirmMsg ==null|| confirmMsg.size()!=3) throw new IllegalArgumentException("confirmMsg must have 3 elements");
        this.confirmMsg = confirmMsg;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return true;
    }

    @Override
    public String execute(final MessageObject messageObject) {

        messageObject.attributes.put("wordCount", session.context.split(messageObject.toString()).length);

        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        final Set<Node> activeNodeSet = new HashSet<>();

        //Short Term Memory
        session.context.matched(messageObject, session.activeNodePool, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                nodeEvent.node.feed(messageObject);
                activeNodeSet.add(nodeEvent.node);
            }
        });

        //Long Term Memory
        if (activeNodeSet.isEmpty()) {
            session.context.matched(messageObject, new ContextListener() {
                @Override
                public void callback(NodeEvent nodeEvent) {
                    nodeEvent.node.feed(messageObject);
                    if (!activeNodeSet.add(nodeEvent.node)) {
                        activeNodeSet.remove(nodeEvent.node);
                        activeNodeSet.add(nodeEvent.node);
                    }
                }
            });
        }

        if(session.learning){
            MIN_LOW = 0.60f;
            Percentile = 0.5f;
        }else{
            MIN_LOW = 0.05f;
            Percentile = 0.80f;
        }

        List<Node> maxActiveNodeList = Context.findActiveNodes(activeNodeSet, Percentile);

        System.out.println("ACTLIST " + activeNodeSet);
        System.out.println("MAXLIST " + maxActiveNodeList);
        Node maxActiveNode;
        float confidenceRate;
        String responseText;
        if (maxActiveNodeList.isEmpty()) {
            maxActiveNode = null;
            confidenceRate = 0.0f;
            responseText = cancelMsg;
        } else {

            maxActiveNode = maxActiveNodeList.get(0);
            responseText = maxActiveNode.response();
            confidenceRate = maxActiveNode.active();
            /*if (maxActiveNodeList.size()==1) {
                confidenceRate = maxActiveNode.maxActiveResponse.active;
            } else {
                confidenceRate = maxActiveNode.maxActiveResponse.active / maxActiveNodeList.size();
            }*/
        }
        System.out.println("CFR : "+confidenceRate);


        if (confidenceRate <= MIN_LOW) {

            if (session.learning) {
                session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2)));
                responseText = "Learning: " + messageObject + " " + lowConfidenceKeys.get(3);
            } else {
                responseText = messageObject +"? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ";
            }

        } else if (confidenceRate < 0.90f) {

            /**
             * Input > send me the document
             * Bot > found tomany choice of word "sen me the document" please explain more..
             *
             */
            if(maxActiveNodeList.size() > 2){
                return messageObject +"? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ";

            }else {
                System.out.println("CFR2 : "+confidenceRate);
                System.out.println(maxActiveNodeList.size());

                if (session.learning){
                    session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), confirmKeys,cancelKeys, confirmMsg, cancelMsg, maxActiveNodeList, lowConfidenceKeys));
                    String multiResponse;
                    responseText = Hook.toString(maxActiveNode.hookList());
                    multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
                    if (session.context.listener!=null) {
                        session.context.listener.callback(new NodeEvent(this, MessageObject.build(messageObject, multiResponse), NodeEvent.Event.LateReply));
                        responseText = "";
                    } else {
                        responseText = multiResponse.replace(System.lineSeparator(), " ");
                    }

                }else {

                    if(maxActiveNodeList.size() > 1){
                        Node maxActive = maxActiveNodeList.get(0);
                        Node maxActive1 = maxActiveNodeList.get(1);
                        responseText = confirmMsg.get(0) + " " + Hook.toString(maxActive.hookList()) + " หรือ " + Hook.toString(maxActive1.hookList()) +" คะ?";
                    }else {
                        responseText = maxActiveNodeList.get(0).response();
                        if(confidenceRate < 0.50f){
                            responseText = messageObject +"? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ";
                        }

                    }

                }

            }

        }

        session.add(activeNodeSet);

        if (confidenceRate > MIN_LOW && maxActiveNodeList.size()==1) {
            System.out.println("Clear!!!");
            session.setLastEntry(messageObject, maxActiveNode);
            maxActiveNode.release();
            session.clearPool();
        }


        return responseText;
    }
}
