package com.internal.netatlas.normalize.mapper;

import com.internal.netatlas.normalize.model.CanonicalInterfaceRecord;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.eapi.AristaEosResponse;

import org.springframework.stereotype.Component;

@Component
public class AristaEosMapper {
    public NormalizedRecord map(AristaEosResponse response) {
        CanonicalInterfaceRecord interfaceRecord = new CanonicalInterfaceRecord();
        interfaceRecord.setInterfaceName(response.getInterfaceName());
        interfaceRecord.setOperationalStatus(response.getOperationalStatus() != null ? response.getOperationalStatus() : "unknown");
        return new NormalizedRecord(interfaceRecord);
    }
}