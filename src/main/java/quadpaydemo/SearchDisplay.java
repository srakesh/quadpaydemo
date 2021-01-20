package quadpaydemo;

import java.util.*;

import org.json.simple.JSONObject;

/**
 * Search json response that has filters selected, 
 * search results and Exceptions, if any
 * @author rakeshsharma
 *
 */
public class SearchDisplay {
	private JSONObject filters = new JSONObject();
	private JSONObject results = new JSONObject();
	private JSONObject exceptions = new JSONObject();
	private JSONObject output = new JSONObject();
	
	public SearchDisplay() {
		output.put("filters", filters);
		output.put("results", results);
		output.put("exceptions", exceptions);
		
	}
	
	public void setFilters(JSONObject filters) {
		this.filters = filters;
	}
	
	public void setResults(JSONObject results) {
		this.results = results;
	}
	
	public void setExceptions(JSONObject exceptions) {
		this.exceptions = exceptions;
	}
	
	public JSONObject getExceptions() {
		return this.exceptions;
	}
	
	public JSONObject getFilters() {
		return this.filters;
	}
	
	public JSONObject getResults() {
		return this.results;
	}
	
	public JSONObject getOutput() {
		return this.output;
	}
	
	public String toString() {
		return output.toJSONString();
	}

}
