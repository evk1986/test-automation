# Onboarding Notes: Implement Schema-Normalizer mapper for Cisco IOS-XR NETCONF responses
## Introduction
This document provides notes for onboarding a new developer to implement a new mapper in the Schema-Normalizer service to normalize raw NETCONF responses from Cisco IOS-XR devices into canonical internal DTOs.
## Prerequisites
* Familiarity with Java and Spring Boot
* Access to the Schema-Normalizer service codebase
## Steps
1. Review the Schema-Normalizer service codebase and familiarize yourself with the existing mappers and services.
2. Create a new Java class `CiscoIosXrNetconfMapper` in the `com.internal.netatlas.normalize.mapper` package.
3. Implement the `map` method in the `CiscoIosXrNetconfMapper` class to normalize the NETCONF response.
4. Create a new Java class `NetconfNormalizationService` in the `com.internal.netatlas.normalize.service` package.
5. Implement the `normalize` method in the `NetconfNormalizationService` class to use the `CiscoIosXrNetconfMapper` class.