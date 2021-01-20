package quadpaydemo;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilders.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class which runs the search functionality
 * It handles:- 
 * a) query generation  
 * b) execution of query (should be in a separate class but circumvented for demo)
 * c) retrieves results/filter from elasticsearch
 * @author rakeshsharma
 *
 */
public class SearchService {
	
	/***
	 * Runs the search query
	 * @param Search model
	 * @return Search Result which is just a json for demo purposes
	 */
	public SearchResult runSearch(SearchModel model) {
		SearchResult result = new SearchResult(); 
		RestClient restClient = null;
		try {
			// Low level Elasticsearch REST API
			RestClientBuilder builder = RestClient.builder(
				    new HttpHost("localhost", 9208, "http")); //9208 - local, 9200 - 54.211.201.55
				Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
				builder.setDefaultHeaders(defaultHeaders); 
						
			StringBuilder jsonString = CreateQuery(model);
			if (jsonString.length() <= 0) return result;

			// Should use logging classes but okay for now.
			System.out.println("========= Start Logging Elastic Query for keyword[" + model.getKeyword() + 
					"], filter[" + model.getFilterMessage() + "]=============");
			System.out.println(jsonString);
			System.out.println("========= End Logging Elastic Query =============");
			restClient = builder.build();
			Request request = new Request(
				    "GET",  
				    "/products_dev_2021-01-04-19-42-00/_search");
			request.setEntity(new NStringEntity(
					jsonString.toString(),
			        ContentType.APPLICATION_JSON));
			request.addParameter("pretty", "true");
			Response response = restClient.performRequest(request);
			
			RequestLine requestLine = response.getRequestLine(); 
			HttpHost host = response.getHost(); 
			int statusCode = response.getStatusLine().getStatusCode(); 
			Header[] headers = response.getHeaders(); 
			//System.out.println(responseBody);
			result.setResult(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (restClient != null) {
				try {
					restClient.close();
				} catch (Exception ignoreIt) {}
			}
		}
		
		return result;
	}
	
	/**
	 * Generates elasticsearch query
	 * The code can be made more readable wrt json query creation
	 * @param Search model
	 * @return StringBuilder
	 */
	private StringBuilder CreateQuery(SearchModel model) {
		StringBuilder buf = new StringBuilder();
		if (model == null || model.getKeyword() == null) return buf;
		buf.append("{");
		
		// Elastic query using keyword=XXX
		String querywithNoGenderFilter = "\"query\": {\n" + 
				"    \"bool\": { \n" + 
				"      \"must\": {\n" + 
				"        \"match\": {\n" + 
				"           \"group_text1_en\": {\n" + 
				"                \"query\": \"" + model.getKeyword() + "\",\n" + 
				"                \"operator\": \"and\"\n" + 
				"            }\n" + 
				"        }\n" + 
				"      }\n" + 
				"    }\n" + 
				"  }";
		String queryWithGenderFilter = null;
		if (model.getGenderValues().size() > 0) {
			// Elastic query using keyword=XXX&Gender:female
			queryWithGenderFilter = "\"query\": {\n" + 
					"    \"bool\": {\n" + 
					"      \"must\": {\n" + 
					"        \"match\": {\n" + 
					"           \"group_text1_en\": {\n" + 
					"                \"query\": \"" + model.getKeyword() + "\",\n" + 
					"                \"operator\": \"and\"\n" + 
					"            }\n" + 
					"        }\n" + 
					"      },\n" + 
					"      \"filter\": {\n" + 
					"        \"term\": {\n" + 
					"          \"e_gender_list\": \"" + model.getGenderValues().get(0) + "\"\n" + 
					"        }\n" + 
					"      }\n" + 
					"    }\n" + 
					"  }";
			buf.append(queryWithGenderFilter);
		}  else {
			buf.append(querywithNoGenderFilter);
		}
		
		String source = "\"_source\":false";
		buf.append(",");
		buf.append(source);
		buf.append(",");
		String size = "\"size\": 20"; // Get 20 results for now
		buf.append(size);
		buf.append(",");
		// Return fields
		String fields = "\"fields\":[\"e_matched_tokens_categories_formatted\", "
				+ "\"e_color_parent\", \"e_gender_list\", "
				+ "\"product_name\",\"e_product_name\", "
				+ "\"e_product_name_en\", \"e_price\", \"e_price_USD\"]";
		buf.append(fields);
		
		// Facets generation
		String facets = "\"aggs\": {\n" + 
				"    \"Color\": {\n" + 
				"      \"terms\": {\n" + 
				"         \"field\": \"e_color_parent\"\n" + 
				"       }\n" + 
				"    },\n" + 
				"    \"Brand\": {\n" + 
				"      \"terms\": {\n" + 
				"         \"field\": \"brand.keyword\"\n" + 
				"       }\n" + 
				"    }\n" + 
				"  }";
		
		if (model.getBrandValues().size() <= 0 && model.getColorValues().size() <= 0) {
			buf.append(",");
			buf.append(facets);
			buf.append("}");
		} else {
			if (model.getLastFacet() != null) { // Did user click on a facet?
				buf.append(",");
				buf.append("\n\"aggs\": {");
				StringBuilder brandAggr = new StringBuilder();
				StringBuilder colorAggr = new StringBuilder();
				
				// The below json query does elasticsearch facet generation
				// Can be made more elegant/readable by code refactoring.
				// Brand filter aggr
				brandAggr.append("\"Brand\": {\n");
				brandAggr.append("      \"filter\": {\n" + 
						"        \"bool\" : {\n" + 
						"           \"must\" : [\n");
				if (model.getColorCommaSeparatedValues().length() > 0) {
					
						brandAggr.append("             {\n");
				}
				if (model.getColorCommaSeparatedValues().length() > 0) {
					brandAggr.append("              \"terms\": {\n" + 
							"                \"e_color_parent\": [" + model.getColorCommaSeparatedValues() + "]\n" + 
							"              }\n");
				}
				
				if (model.getColorCommaSeparatedValues().length() > 0) {
					
						brandAggr.append("             }\n");
				}
					 
				brandAggr.append(		"           ]\n" + 
						"        }\n" + 
						"      },\n");
				
				
				brandAggr.append("      \"aggs\": { \n" + 
						"        \"Brand\" : {\n" + 
						"          \"terms\": { \n" + 
						"            \"field\": \"brand.keyword\"\n" + 
						"          }\n" + 
						"        }\n" + 
						"      }\n" + 
						"    }");		
				
				// Color filter aggr
				colorAggr.append("\"Color\": {\n"); 
				colorAggr.append("      \"filter\": {\n" + 
						"        \"bool\" : {\n" + 
						"           \"must\" : [\n");
				
				if (model.getBrandCommaSeparatedValues().length() > 0	) {
					
					colorAggr.append("  {\n");
				}
				if (model.getBrandCommaSeparatedValues().length() > 0) {
					colorAggr.append("              \"terms\": {\n" + 
							"                \"brand.keyword\": [" + model.getBrandCommaSeparatedValues() + "]\n" + 
							"              }\n"); 
				}
				
				if (model.getBrandCommaSeparatedValues().length() > 0	) {
					
					colorAggr.append("  }\n");
				}
						
				
				colorAggr.append("           ]\n" + 
						"        }\n" + 
						"      },\n");
				
				
				colorAggr.append("      \"aggs\": { \n" + 
						"        \"Color\" : {\n" + 
						"          \"terms\": { \n" + 
						"            \"field\": \"e_color_parent\"\n" + 
						"          }\n" + 
						"        }\n" + 
						"      }\n" + 
						"    }");
				
			
			buf.append(brandAggr);
			buf.append(",");
			buf.append(colorAggr);
				  
		}
		buf.append("},");	
		
		// Post filter to apply facet filters to search results
		StringBuilder postFilter = new StringBuilder("\"post_filter\": {\n" + 
				"        \"bool\" : {\n" + 
				"           \"filter\" : [\n");
				
		if (model.getColorCommaSeparatedValues().length() > 0) {
			postFilter.append("              {\"terms\": {\n" + 
					"                \"e_color_parent\": [" + model.getColorCommaSeparatedValues() + "]\n" + 
					"               }}\n");
		}
		if (model.getColorCommaSeparatedValues().length() > 0 && model.getBrandCommaSeparatedValues().length() > 0) {
			postFilter.append(",");
		}
		if (model.getBrandCommaSeparatedValues().length() > 0) {
			postFilter.append("               {\"terms\": {\n" + 
							"                \"brand.keyword\": [" + model.getBrandCommaSeparatedValues() + "]\n" + 
							"               }}\n");
		}
				
		postFilter.append(
				"           ]\n" + 
				"        }\n" + 
				"      }");
		
		buf.append(postFilter);
		buf.append("}");
	  }
			
			
	  return buf;
	}
	
}
