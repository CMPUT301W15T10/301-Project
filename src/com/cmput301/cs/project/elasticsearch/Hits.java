package com.cmput301.cs.project.elasticsearch;

//https://github.com/blainelewis1/AndroidElasticSearch/ [blaine1 april 05 2014]


import java.util.ArrayList;
import java.util.List;


public class Hits<T> {
	private int total;
	private float max_score;
	private List<SearchHit<T>> hits;
	
	public Hits() {}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public float getMax_score() {
		return max_score;
	}

	public void setMax_score(float max_score) {
		this.max_score = max_score;
	}

	public List<SearchHit<T>> getHits() {
		return hits;
	}

	public void setHits(List<SearchHit<T>> hits) {
		this.hits = hits;
	}

	@Override
	public String toString() {
		return "com.cmput301.cs.project.elasticsearch.Hits [total=" + total + ", max_score=" + max_score + ", hits="
				+ hits + "]";
	}
	
}