package com.brainy.service.http.ext.facebook;

import com.brainy.service.http.ext.facebook.FacebookHttpEndpoint;
import com.brainy.service.http.ext.facebook.FacebookSignatureValidator;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.Mockito.*;

public class FacebookHttpEndpointTest {

    String contextName = "examples/ocm";
    String pageAccessToken = "-";
    String appSecret = "iloveu";

    private String textPayload() {
        return "{\"object\":\"page\",\"entry\":[{\"id\":\"<PAGE_ID>\",\"time\":1583173667623,\"messaging\":[{\"sender\":{\"id\":\"12345\"},\"recipient\":{\"id\":\"<PAGE_ID>\"},\"timestamp\":1583173666767,\"message\":{\"mid\":\"m_toDnmD...\",\"text\":\"greeting\",\"attachments\":[{\"type\":\"fallback\",\"payload\":{\"url\":\"<ATTACHMENT_URL >\",\"title\":\"TAHITI - Heaven on Earth\"}}]}}]}]}";
    }

    @Test
    public void createRequestObject() throws Exception {

        String payload = textPayload();

        FacebookSignatureValidator facebookSignatureValidator = new FacebookSignatureValidator(appSecret.getBytes());
        /**
         * I dont know why facebook need to padding 5?
         */
        String signedPayload = "01234" + facebookSignatureValidator.generateSignature(payload.getBytes());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader("X-Hub-Signature")).thenReturn(signedPayload);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(payload)));

        FacebookHttpEndpoint facebookHttpEndpoint = new FacebookHttpEndpoint(
                contextName, pageAccessToken, appSecret, request, response);

        System.out.println(facebookHttpEndpoint.process(request));

    }
}