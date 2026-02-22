package com.blob.model.s3;

import java.time.Instant;

/**
 * S3-compatible object representation for ListObjectsV2 response.
 */
public class S3Object {

    private final String key;
    private final Long size;
    private final String eTag;
    private final Instant lastModified;
    private final String storageClass;

    private S3Object(Builder builder) {
        this.key = builder.key;
        this.size = builder.size;
        this.eTag = builder.eTag;
        this.lastModified = builder.lastModified;
        this.storageClass = builder.storageClass;
    }

    public String getKey() { return key; }
    public Long getSize() { return size; }
    public String getETag() { return eTag; }
    public Instant getLastModified() { return lastModified; }
    public String getStorageClass() { return storageClass; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String key;
        private Long size;
        private String eTag;
        private Instant lastModified;
        private String storageClass = "STANDARD";

        public Builder key(String key) { this.key = key; return this; }
        public Builder size(Long size) { this.size = size; return this; }
        public Builder eTag(String eTag) { this.eTag = eTag; return this; }
        public Builder lastModified(Instant lastModified) { this.lastModified = lastModified; return this; }
        public Builder storageClass(String storageClass) { this.storageClass = storageClass; return this; }

        public S3Object build() {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("key is required");
            }
            return new S3Object(this);
        }
    }
}
