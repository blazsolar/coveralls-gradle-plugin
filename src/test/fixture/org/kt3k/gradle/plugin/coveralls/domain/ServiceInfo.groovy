package com.github.blazsolar.gradle.coveralls.domain

// model for ci service info
class ServiceInfo {
    String serviceName;
    String serviceJobId;

    public ServiceInfo(String serviceName, String serviceJobId) {
        this.serviceName = serviceName;
        this.serviceJobId = serviceJobId;
    }
}

