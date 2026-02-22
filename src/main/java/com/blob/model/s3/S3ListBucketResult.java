package com.blob.model.s3;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * S3-compatible ListBucketResult for ListObjectsV2 API response.
 * Serializes to XML format matching AWS S3 response structure.
 */
public class S3ListBucketResult {

    private final String name;
    private final String prefix;
    private final String delimiter;
    private final int maxKeys;
    private final boolean isTruncated;
    private final int keyCount;
    private final String continuationToken;
    private final String nextContinuationToken;
    private final String startAfter;
    private final List<S3Object> contents;
    private final List<CommonPrefix> commonPrefixes;

    private S3ListBucketResult(Builder builder) {
        this.name = builder.name;
        this.prefix = builder.prefix;
        this.delimiter = builder.delimiter;
        this.maxKeys = builder.maxKeys;
        this.isTruncated = builder.isTruncated;
        this.keyCount = builder.keyCount;
        this.continuationToken = builder.continuationToken;
        this.nextContinuationToken = builder.nextContinuationToken;
        this.startAfter = builder.startAfter;
        this.contents = builder.contents;
        this.commonPrefixes = builder.commonPrefixes;
    }

    public String getName() { return name; }
    public String getPrefix() { return prefix; }
    public String getDelimiter() { return delimiter; }
    public int getMaxKeys() { return maxKeys; }
    public boolean isTruncated() { return isTruncated; }
    public int getKeyCount() { return keyCount; }
    public String getContinuationToken() { return continuationToken; }
    public String getNextContinuationToken() { return nextContinuationToken; }
    public String getStartAfter() { return startAfter; }
    public List<S3Object> getContents() { return contents; }
    public List<CommonPrefix> getCommonPrefixes() { return commonPrefixes; }

    /**
     * Converts this result to S3-compatible XML format.
     *
     * @return XML string matching AWS S3 ListObjectsV2 response
     */
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n");
        
        appendElement(xml, "Name", name);
        appendElement(xml, "Prefix", prefix != null ? prefix : "");
        appendElement(xml, "Delimiter", delimiter);
        appendElement(xml, "MaxKeys", String.valueOf(maxKeys));
        appendElement(xml, "IsTruncated", String.valueOf(isTruncated));
        appendElement(xml, "KeyCount", String.valueOf(keyCount));
        appendElement(xml, "ContinuationToken", continuationToken);
        appendElement(xml, "NextContinuationToken", nextContinuationToken);
        appendElement(xml, "StartAfter", startAfter);
        
        for (S3Object obj : contents) {
            xml.append("  <Contents>\n");
            appendElement(xml, "    Key", obj.getKey());
            appendElement(xml, "    LastModified", obj.getLastModified() != null 
                ? obj.getLastModified().toString() : null);
            appendElement(xml, "    ETag", obj.getETag() != null ? "\"" + obj.getETag() + "\"" : null);
            appendElement(xml, "    Size", obj.getSize() != null ? String.valueOf(obj.getSize()) : null);
            appendElement(xml, "    StorageClass", obj.getStorageClass());
            xml.append("  </Contents>\n");
        }
        
        for (CommonPrefix cp : commonPrefixes) {
            xml.append("  <CommonPrefixes>\n");
            appendElement(xml, "    Prefix", cp.getPrefix());
            xml.append("  </CommonPrefixes>\n");
        }
        
        xml.append("</ListBucketResult>");
        return xml.toString();
    }

    private void appendElement(StringBuilder xml, String name, String value) {
        if (value != null) {
            xml.append("  <").append(name).append(">")
               .append(escapeXml(value))
               .append("</").append(name).append(">\n");
        }
    }

    private String escapeXml(String value) {
        if (value == null) return "";
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String prefix;
        private String delimiter;
        private int maxKeys = 1000;
        private boolean isTruncated = false;
        private int keyCount;
        private String continuationToken;
        private String nextContinuationToken;
        private String startAfter;
        private List<S3Object> contents = new ArrayList<>();
        private List<CommonPrefix> commonPrefixes = new ArrayList<>();

        public Builder name(String name) { this.name = name; return this; }
        public Builder prefix(String prefix) { this.prefix = prefix; return this; }
        public Builder delimiter(String delimiter) { this.delimiter = delimiter; return this; }
        public Builder maxKeys(int maxKeys) { this.maxKeys = maxKeys; return this; }
        public Builder isTruncated(boolean isTruncated) { this.isTruncated = isTruncated; return this; }
        public Builder keyCount(int keyCount) { this.keyCount = keyCount; return this; }
        public Builder continuationToken(String token) { this.continuationToken = token; return this; }
        public Builder nextContinuationToken(String token) { this.nextContinuationToken = token; return this; }
        public Builder startAfter(String startAfter) { this.startAfter = startAfter; return this; }
        public Builder contents(List<S3Object> contents) { this.contents = contents; return this; }
        public Builder commonPrefixes(List<CommonPrefix> prefixes) { this.commonPrefixes = prefixes; return this; }

        public S3ListBucketResult build() {
            if (keyCount == 0 && !contents.isEmpty()) {
                this.keyCount = contents.size();
            }
            return new S3ListBucketResult(this);
        }
    }
}
