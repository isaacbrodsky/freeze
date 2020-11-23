package com.isaacbrodsky.zztsearch.query.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        /**
         * Produce a URL to a canonical source for the world
         */
        @JsonProperty("canonicalUrl")
        public String canonicalUrl() {
            if (!fields.containsKey("world_path")) {
                return "";
            }
            String[] parts = fields.get("world_path").split("/");
            List<String> zipFiles = Arrays.stream(parts)
                    .filter(s -> s.toLowerCase().endsWith(".zip"))
                    .collect(Collectors.toList());
            if (zipFiles.size() > 0) {
                String zipFile = zipFiles.get(zipFiles.size() - 1);
                char firstLetter = zipFile.toLowerCase().charAt(0);
                String zztFile = parts[parts.length - 1];
                String boardIndex = fields.get("board_index");
                return "https://museumofzzt.com/file/" + firstLetter + "/" + zipFile + "?file=" + zztFile + "&board=" + boardIndex;
            } else {
                return "";
            }
        }
    }
}
