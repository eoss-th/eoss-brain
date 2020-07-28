package com.brainy.service.http.ext.line;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Base64;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LINEHttpEndpointTest {

    String contextName = "examples/ocm";
    String channelAccessToken = "-";
    String secret = "iloveu";

    private String textPayload() {
        return "{\"destination\":\"xxxxxxxxxx\",\"events\":[{\"replyToken\":\"0f3779fba3b349968c5d07db31eab56f\",\"type\":\"message\",\"mode\":\"active\",\"timestamp\":1462629479859,\"source\":{\"type\":\"user\",\"userId\":\"U4af4980629...\"},\"message\":{\"id\":\"325708\",\"type\":\"text\",\"text\":\"greeting\"}},{\"replyToken\":\"8cf9239d56244f4197887e939187e19e\",\"type\":\"follow\",\"mode\":\"active\",\"timestamp\":1462629479859,\"source\":{\"type\":\"user\",\"userId\":\"U4af4980629...\"}}]}";
    }

    @Test
    public void createRequestObject() throws Exception {

        String payload = textPayload();

        LINESignatureValidator lineSignatureValidator = new LINESignatureValidator(secret.getBytes());
        String signedPayload = Base64.getEncoder().encodeToString(lineSignatureValidator.generateSignature(payload.getBytes()));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader("X-Line-Signature")).thenReturn(signedPayload);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(payload)));

        LINEHttpEndpoint lineHttpEndpoint = new LINEHttpEndpoint(
                contextName, channelAccessToken, secret, request, response);

        System.out.println(lineHttpEndpoint.process(request));

    }
}