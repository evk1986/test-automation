package com.internal.netatlas.normalize.mapper;

import com.internal.netatlas.normalize.model.CanonicalInterface;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.NormalizedRecordBuilder;
import org.json.JSONObject;

public class CiscoIosXrNetconfMapper {
    public NormalizedRecord map(JSONObject netconfResponse) {
        NormalizedRecordBuilder builder = NormalizedRecordBuilder.create();
        builder.canonicalType("Cisco IOS-XR");
        // Extract and map relevant fields from the NETCONF response
        builder.normalizedPayload(netconfResponse);
        return builder.build();
    }
}