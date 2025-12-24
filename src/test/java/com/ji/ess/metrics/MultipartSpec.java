package com.ji.ess.metrics;

public record MultipartSpec(String name, String filename, String contentType, byte[] content) {
}
