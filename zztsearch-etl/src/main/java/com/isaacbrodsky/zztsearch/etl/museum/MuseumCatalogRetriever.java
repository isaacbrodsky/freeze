package com.isaacbrodsky.zztsearch.etl.museum;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value
@Slf4j
public class MuseumCatalogRetriever {
    /**
     * Wait between requests
     */
    private final long DELAY_MS = 2000;

    private String urlBase;
    private File output;

    public void retrieve() throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        List<JsonNode> allResults = new ArrayList<>();
        long start = System.currentTimeMillis();
        int offset = 0;
        int resultCount = 0;
        int requestCount = 0;
        do {
            log.info("Requesting {}", offset);
            // https://museumofzzt.com/api/v1/search/files?sort=published&offset=1000
            HttpUrl url = HttpUrl.parse(urlBase + "search/files")
                    .newBuilder()
                    .addQueryParameter("sort", "published")
                    .addQueryParameter("offset", Long.toString(offset))
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                JsonNode root = objectMapper.readTree(response.body().string());
                if (!root.get("status").asText().equalsIgnoreCase("SUCCESS")) {
                    throw new IOException(String.format("API reports error! Status: %s", root.get("status").asText()));
                }
                resultCount = root.get("count").asInt();
                offset = root.get("next_offset").asInt();
                requestCount++;
                if (resultCount > 0) {
                    JsonNode results = root
                            .get("data")
                            .get("results");
                    allResults.addAll(IntStream.range(0, resultCount)
                            .mapToObj(results::get)
                            .collect(Collectors.toList()));
                }
            }

            Thread.sleep(DELAY_MS);
        } while (resultCount > 0);

        File toWrite = output.toPath().resolve("catalog.json").toFile();
        log.info("Took {} ms", System.currentTimeMillis() - start);
        log.info("Writing results {} details, {} requests to {}", allResults.size(), requestCount, toWrite.toString());
        objectMapper.writeValue(toWrite, allResults);
        log.info("Done!");
    }
}
