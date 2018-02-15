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
public class BizTalkCommandNode2 extends CommandNode {

    private final List<String> lowConfidenceKeys;
    private final List<String> confirmKeys;
    private final List<String> cancelKeys;
    private final String cancelMsg;
    private final List<String> confirmMsg;
    private float MIN_LOW = 0.05f;
    private float Percentile = 0.00f;
    public BizTalkCommandNode2(Session session, List<String> lowConfidenceKeys, List<String> confirmKeys, List<String> cancelKeys, String cancelMsg, List<String> confirmMsg) {
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
            MIN_LOW = 0.20f;
            Percentile = 0.90f;
        }else{
            MIN_LOW = 0.05f;
            Percentile = 0.50f;
        }
        List<Node> maxActiveNodeList = Context.findActiveNodes(activeNodeSet, Percentile);
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
/*
            if (maxActiveNodeList.size()==1) {
                confidenceRate = maxActiveNode.maxActiveResponse.active;
            } else {
                confidenceRate = maxActiveNode.maxActiveResponse.active / maxActiveNodeList.size();
            }
            */
        }
        System.out.println("CFR : "+confidenceRate);


        if (confidenceRate <= MIN_LOW) {

            if (session.learning) {
                session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2)));
                responseText = "Learning: " + messageObject + " " + lowConfidenceKeys.get(3);
            } else {
/*                String query = messageObject.toString().trim();
                if (session.context.domain!=null && !session.context.domain.trim().isEmpty()) {
                    query += " site:" + session.context.domain;
                }*/
/*                new GoogleCommandNode(session, null, 1).execute(MessageObject.build(messageObject,  query));*/
                responseText = messageObject +"? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ";
            }

        } else if (confidenceRate < 0.5f) {
/*            System.out.println(messageObject.toString()+"2");
            List<Response> responseList = new ArrayList<>();
            for (Node node:maxActiveNodeList) {
                responseList.add(node.maxActiveResponse);
            }*/

            /**
             * Input > send me the document
             * Bot > found tomany choice of word "sen me the document" please explain more..
             *
             */
            if(maxActiveNodeList.size() > 2){
                return messageObject +"? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ";

            }else {
                System.out.println("CFR2 : "+confidenceRate);
                System.out.println("size=<2");
                session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), confirmKeys,cancelKeys, confirmMsg, cancelMsg, maxActiveNodeList, lowConfidenceKeys));

                String multiResponse;
/*            if (maxActiveNodeList.size()>1) {
                String responseText2 = maxActiveNodeList.get(1).response().split("\\s+", 2)[0];
                multiResponse = confirmMsg.get(0) + System.lineSeparator() + responseText.split("\\s+", 2)[0] + System.lineSeparator() + confirmMsg.get(1) + System.lineSeparator() + responseText2;
            } else {*/
                responseText = Hook.toString(maxActiveNode.hookList());
                multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
           /* }*/

                if (session.context.listener!=null) {
                    session.context.listener.callback(new NodeEvent(this, MessageObject.build(messageObject, multiResponse), NodeEvent.Event.LateReply));
                    responseText = "";
                } else {
                    responseText = multiResponse.replace(System.lineSeparator(), " ");
                }

            }

        } else if (confidenceRate < 0.9f && maxActiveNodeList.size()>1) {
            System.out.println("CFR2 : "+confidenceRate);
            System.out.println("size=<2");
            session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), confirmKeys,cancelKeys, confirmMsg, cancelMsg, maxActiveNodeList, lowConfidenceKeys));

            String multiResponse;
/*            if (maxActiveNodeList.size()>1) {
                String responseText2 = maxActiveNodeList.get(1).response().split("\\s+", 2)[0];
                multiResponse = confirmMsg.get(0) + System.lineSeparator() + responseText.split("\\s+", 2)[0] + System.lineSeparator() + confirmMsg.get(1) + System.lineSeparator() + responseText2;
            } else {*/
            responseText = Hook.toString(maxActiveNode.hookList());
            multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
           /* }*/

            if (session.context.listener!=null) {
                session.context.listener.callback(new NodeEvent(this, MessageObject.build(messageObject, multiResponse), NodeEvent.Event.LateReply));
                responseText = "";
            } else {
                responseText = multiResponse.replace(System.lineSeparator(), " ");
            }

        }

/*        else if (confidenceRate <= 0.75f) {

            //responseText += "?";
            session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), confirmKeys,cancelKeys, confirmMsg, cancelMsg, maxActiveNodeList, lowConfidenceKeys));
            String multiResponse;
            responseText = maxActiveNode.hooksString();
            multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
            if (session.context.listener!=null) {
                session.context.listener.callback(new NodeEvent(this, MessageObject.build(messageObject, multiResponse), NodeEvent.Event.LateReply));
                responseText = "";
            } else {
                responseText = multiResponse.replace(System.lineSeparator(), " ");
            }
        }*/ /*else if (confidenceRate > 1) {
            //Super Confidence

        }
        */
        System.out.println(maxActiveNodeList.size());
        if (confidenceRate > MIN_LOW) {
            session.setLastEntry(messageObject, maxActiveNode);
            maxActiveNode.release();
        }

        session.add(activeNodeSet);

        return responseText;
    }
}
