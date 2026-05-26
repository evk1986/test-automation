# Runbook: Implement Schema-Normalizer mapper for Cisco IOS-XR NETCONF responses
## Introduction
This runbook provides a step-by-step guide to implementing a new mapper in the Schema-Normalizer service to normalize raw NETCONF responses from Cisco IOS-XR devices into canonical internal DTOs.
## Prerequisites
* Familiarity with Java and Spring Boot
* Access to the Schema-Normalizer service codebase
## Steps
1. Create a new Java class `CiscoIosXrNetconfMapper` in the `com.internal.netatlas.normalize.mapper` package.
2. Implement the `map` method in the `CiscoIosXrNetconfMapper` class to normalize the NETCONF response.
3. Create a new Java class `NetconfNormalizationService` in the `com.internal.netatlas.normalize.service` package.
4. Implement the `normalize` method in the `NetconfNormalizationService` class to use the `CiscoIosXrNetconfMapper` class.