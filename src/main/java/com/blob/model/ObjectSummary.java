package com.blob.model;

import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * Summary of an object in the blob store.
 * Matches OCI Object Storage ObjectSummary structure.
 */
public class ObjectSummary {

    private final String name;
    private final Long size;
    private final String md5;
    private final Instant timeCreated;
    private final Instant timeModified;
    private final String etag;

    private ObjectSummary(Builder builder) {
        this.name = builder.name;
        this.size = builder.size;
        this.md5 = builder.md5;
        this.timeCreated = builder.timeCreated;
        this.timeModified = builder.timeModified;
        this.etag = builder.etag;
    }

    /**
     * The name of the object (key/path).
     *
     * @return object name
     */
    public String getName() {
        return name;
    }

    /**
     * Size of the object in bytes.
     *
     * @return size in bytes, or null if not available
     */
    public Long getSize() {
        return size;
    }

    /**
     * Base64-encoded MD5 hash of the object data.
     *
     * @return MD5 hash, or null if not available
     */
    public String getMd5() {
        return md5;
    }

    /**
     * The date and time the object was created.
     *
     * @return creation timestamp, or null if not available
     */
    public Instant getTimeCreated() {
        return timeCreated;
    }

    /**
     * The date and time the object was last modified.
     *
     * @return modification timestamp, or null if not available
     */
    public Instant getTimeModified() {
        return timeModified;
    }

    /**
     * The entity tag (ETag) for the object.
     *
     * @return ETag, or null if not available
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Converts this ObjectSummary to a JSON object.
     * Only includes fields that are non-null.
     *
     * @return JsonObject representation
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("name", name);
        if (size != null) {
            json.put("size", size);
        }
        if (md5 != null) {
            json.put("md5", md5);
        }
        if (timeCreated != null) {
            json.put("timeCreated", timeCreated.toString());
        }
        if (timeModified != null) {
            json.put("timeModified", timeModified.toString());
        }
        if (etag != null) {
            json.put("etag", etag);
        }
        return json;
    }

    /**
     * Creates a new builder for ObjectSummary.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for ObjectSummary.
     */
    public static class Builder {
        private String name;
        private Long size;
        private String md5;
        private Instant timeCreated;
        private Instant timeModified;
        private String etag;

        /**
         * Sets the object name.
         *
         * @param name the object name/key
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the object size in bytes.
         *
         * @param size the size in bytes
         * @return this builder
         */
        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        /**
         * Sets the MD5 hash (base64-encoded).
         *
         * @param md5 the MD5 hash
         * @return this builder
         */
        public Builder md5(String md5) {
            this.md5 = md5;
            return this;
        }

        /**
         * Sets the creation timestamp.
         *
         * @param timeCreated the creation time
         * @return this builder
         */
        public Builder timeCreated(Instant timeCreated) {
            this.timeCreated = timeCreated;
            return this;
        }

        /**
         * Sets the modification timestamp.
         *
         * @param timeModified the modification time
         * @return this builder
         */
        public Builder timeModified(Instant timeModified) {
            this.timeModified = timeModified;
            return this;
        }

        /**
         * Sets the ETag.
         *
         * @param etag the entity tag
         * @return this builder
         */
        public Builder etag(String etag) {
            this.etag = etag;
            return this;
        }

        /**
         * Builds the ObjectSummary instance.
         *
         * @return a new ObjectSummary
         */
        public ObjectSummary build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("name is required");
            }
            return new ObjectSummary(this);
        }
    }
}
