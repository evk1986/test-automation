# Onboarding Notes: Device-Probe Hazelcast & Micrometer Integration

## Local Development Setup

1. **Infrastructure Pre-requisites**
   - Start local dependencies using `docker-compose up -d consul hazelcast localstack`.
   - AWS SQS queues `probe.commands` and `platform.results.dlq` are auto-created by init scripts.

2. **Consul Discovery & Vault Credentials**
   - Consul Key/Value path: `config/device-probe,dev/data`
   - HashiCorp Vault path: `secret/data/dev/device-probe`

3. **Running the Service Locally**
   - Active Spring profile: `dev`
   - Command: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

4. **Verifying Metrics**
   - Access Actuator metrics endpoint: `http://localhost:8080/actuator/metrics/probe.protocol.failures`
