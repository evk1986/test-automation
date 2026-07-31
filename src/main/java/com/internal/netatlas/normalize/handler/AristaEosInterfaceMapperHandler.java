package com.internal.netatlas.normalize.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import com.internal.netatlas.normalize.service.AristaEosInterfaceMapperService;

@Service
@RequiredArgsConstructor
public class AristaEosInterfaceMapperHandler {

    private final AristaEosInterfaceMapperService mapperService;

    @SqsListener("normalize.ingest")
    public void handle(String rawPayload) {
        // rawPayload contains the Arista EOS eAPI "show interfaces" JSON response
        mapperService.mapAndPublish(rawPayload);
    }
}
