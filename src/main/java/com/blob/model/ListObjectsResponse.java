package com.blob.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Response model for listing objects in the blob store.
 * Matches OCI Object Storage ListObjects response structure.
 */
public class ListObjectsResponse {

    private final List<ObjectSummary> objects;
    private final List<String> prefixes;
    private final String nextStartWith;

    private ListObjectsResponse(Builder builder) {
        this.objects = builder.objects;
        this.prefixes = builder.prefixes;
        this.nextStartWith = builder.nextStartWith;
    }

    /**
     * List of object summaries matching the request.
     *
     * @return list of ObjectSummary objects
     */
    public List<ObjectSummary> getObjects() {
        return objects;
    }

    /**
     * Common prefixes when delimiter is used (folder-like grouping).
     *
     * @return list of prefixes, or null if delimiter not used
     */
    public List<String> getPrefixes() {
        return prefixes;
    }

    /**
     * Pagination cursor for the next page of results.
     * Null if there are no more results.
     *
     * @return next start cursor, or null if no more results
     */
    public String getNextStartWith() {
        return nextStartWith;
    }

    /**
     * Converts this response to a JSON object.
     *
     * @return JsonObject representation
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        JsonArray objectsArray = new JsonArray();
        if (objects != null) {
            for (ObjectSummary obj : objects) {
                objectsArray.add(obj.toJson());
            }
        }
        json.put("objects", objectsArray);

        if (prefixes != null) {
            json.put("prefixes", new JsonArray(prefixes));
        }

        if (nextStartWith != null) {
            json.put("nextStartWith", nextStartWith);
        }

        return json;
    }

    /**
     * Creates a new builder for ListObjectsResponse.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for ListObjectsResponse.
     */
    public static class Builder {
        private List<ObjectSummary> objects;
        private List<String> prefixes;
        private String nextStartWith;

        /**
         * Sets the list of object summaries.
         *
         * @param objects the list of objects
         * @return this builder
         */
        public Builder objects(List<ObjectSummary> objects) {
            this.objects = objects;
            return this;
        }

        /**
         * Sets the common prefixes (for delimiter-based grouping).
         *
         * @param prefixes the list of prefixes
         * @return this builder
         */
        public Builder prefixes(List<String> prefixes) {
            this.prefixes = prefixes;
            return this;
        }

        /**
         * Sets the pagination cursor for the next page.
         *
         * @param nextStartWith the next start cursor
         * @return this builder
         */
        public Builder nextStartWith(String nextStartWith) {
            this.nextStartWith = nextStartWith;
            return this;
        }

        /**
         * Builds the ListObjectsResponse instance.
         *
         * @return a new ListObjectsResponse
         */
        public ListObjectsResponse build() {
            return new ListObjectsResponse(this);
        }
    }
}
