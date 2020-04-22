package com.isaacbrodsky.zztsearch.web.resources;

import com.codahale.metrics.annotation.Timed;
import com.isaacbrodsky.zztsearch.query.search.GameTextSearcher;
import com.isaacbrodsky.zztsearch.query.search.Index;
import com.isaacbrodsky.zztsearch.query.search.SearchResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {
    private final GameTextSearcher searcher;

    public SearchResource(GameTextSearcher searcher) {
        this.searcher = searcher;
    }

    @GET
    @Timed
    @Path("/{index}/{field}")
    public SearchResult search(@PathParam("index") Index index,
                               @PathParam("field") String field,
                               @QueryParam("q") String query) throws Exception {
        return searcher.search(index, field, query);
    }
}
