package com.cmput301.cs.project.serialization.elasticsearch;

//https://github.com/blainelewis1/AndroidElasticSearch/ [blaine1 april 05 2014]



import java.util.ArrayList;
import java.util.List;
/**
 * This class acts as a wrapper from data obtained when using RemoteSaver to pull data from Elastic Search
 * getSources() is the only important method and returns all of the objects retrieved
 *
 */
public class SearchResponse<T> {

	private int took;
	private boolean timed_out;
	private Shard _shards;
	private Hits<T> hits;
	
	public SearchResponse() {}

	public int getTook() {
		return took;
	}

	public void setTook(int took) {
		this.took = took;
	}

	public boolean isTimed_out() {
		return timed_out;
	}

	public void setTimed_out(boolean timed_out) {
		this.timed_out = timed_out;
	}

	public Shard get_shards() {
		return _shards;
	}

	public void set_shards(Shard _shards) {
		this._shards = _shards;
	}

	public Hits<T> getHits() {
		return hits;
	}

	public void setHits(Hits<T> hits) {
		this.hits = hits;
	}

    public List<T> getSources() {
        List<T> sources = new ArrayList<T>();

        for(SearchHit<T> hit : hits.getHits()) {
            sources.add(hit.getSource());
        }

        return sources;
    }
}


/**
 * This class acts as a wrapper from data obtained when using RemoteSaver to pull data from Elastic Search
 *
 */
class Shard {
	private int total;
	private int successful;
	private int failed;
	
	public Shard() {}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSuccessful() {
		return successful;
	}
	public void setSuccessful(int successful) {
		this.successful = successful;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
}