package com.internal.netatlas.normalize.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishResponse;

class AristaEosInterfaceMapperServiceTest {

    @Test
    void mapAndPublish_withOperationalStatus_publishesMessage() {
        String payload = """
                {
                  \"interfaces\": {
                    \"Ethernet1\": {\n                      \"operationalStatus\": \"UP\"\n                    },
                    \"Ethernet2\": {\n                      \"operationalStatus\": \"DOWN\"\n                    }
                  }
                }
                """;
        SnsClient snsMock = mock(SnsClient.class);
        when(snsMock.publish(any())).thenReturn(PublishResponse.builder().messageId("msg-1").build());
        AristaEosInterfaceMapperService service = new AristaEosInterfaceMapperService(snsMock);
        service.mapAndPublish(payload);
        verify(snsMock, times(2)).publish(any());
    }

    @Test
    void mapAndPublish_missingOperationalStatus_defaultsToUnknown() {
        String payload = """
                {
                  \"interfaces\": {
                    \"Ethernet1\": {}
                  }
                }
                """;
        SnsClient snsMock = mock(SnsClient.class);
        when(snsMock.publish(any())).thenReturn(PublishResponse.builder().messageId("msg-2").build());
        AristaEosInterfaceMapperService service = new AristaEosInterfaceMapperService(snsMock);
        service.mapAndPublish(payload);
        verify(snsMock, times(1)).publish(argThat(request ->
                request.message().contains("\"operationalStatus\":\"UNKNOWN\"")));
    }
}
