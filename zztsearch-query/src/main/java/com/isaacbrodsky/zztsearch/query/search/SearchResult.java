package com.isaacbrodsky.zztsearch.query.search;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class SearchResult {
    long totalHits;
    float maxScore;
    List<Doc> docs;

    @Value
    public static class Doc {
        int doc;
        float score;
        int shardIndex;
        Map<String, String> fields;

        String canonicalUrl;
    }
}
