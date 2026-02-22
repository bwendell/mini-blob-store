package com.blob.model.s3;

/**
 * S3-compatible common prefix for delimiter-based grouping in ListObjectsV2.
 */
public class CommonPrefix {

    private final String prefix;

    public CommonPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
