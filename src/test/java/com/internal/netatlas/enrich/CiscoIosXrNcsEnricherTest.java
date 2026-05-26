package com.internal.netatlas.enrich;

import com.internal.netatlas.enrich.enricher.CiscoIosXrNcsEnricher;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CiscoIosXrNcsEnricherTest {
    @Autowired
    private CiscoIosXrNcsEnricher enricher;

    @Test
    public void testEnrich() {
        // Create a sample NormalizedRecord
        NormalizedRecord record = new NormalizedRecord();
        // Call the enricher to perform data enrichment
        EnrichmentResult result = enricher.enrich(record);
        // Assert the expected enrichment result
        assert result.getEnrichedFields().containsKey("derivedField");
    }
}