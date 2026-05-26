package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class NetconfIosXrHandler {
    private final SqsClient sqsClient;

    public NetconfIosXrHandler(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Connect to Cisco IOS-XR device using NETCONF protocol
        // Run protocol commands and collect raw responses
        String rawResponse = "Raw response from Cisco IOS-XR device";

        // Publish raw response to SQS queue
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands")
                .messageBody(rawResponse)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
    }
}