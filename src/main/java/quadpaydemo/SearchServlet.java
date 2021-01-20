package quadpaydemo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Search Servlet that handles /search?keyword=XXX&filter=XYZ search request
 * @author rakeshsharma
 *
 */
public class SearchServlet extends HttpServlet {
	 
	   public void init() throws ServletException {

	   }

	   /**
	    * Method in servlet to handle get queries from browser
	    * doPost() should do the same but ignored for the demo
	    */
	   @SuppressWarnings("unchecked")
	   public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
	      
	      // Set response content type
	      response.setContentType("application/json");

	    
	      // Actual logic goes here.
	      SearchDisplay display = new SearchDisplay();
	      SearchModel model = new SearchModel();
	      
	      /* Read Parameters here */
	      // Read keyword=XXX
	      String keyword = request.getParameter("keyword");
	      if (keyword != null) {
	    	  model.setKeyword(keyword);
	      } 
	      // Read filter=XXX&filter=YYY&filter=ZZZ here
	      String[] filters=request.getParameterValues("filter");
	      String parsedFilter;
	      StringBuilder filterMessage = new StringBuilder();
	      if (filters != null) {
	    	  for (int i = 0; i < filters.length; i++) {
	    		  String filter = filters[i];
	    		  if (filter.startsWith("Color:") || filter.startsWith("color:") ) {
	    			  parsedFilter = parseValue(filter);
	    			  if (parsedFilter != null && !"".equals(parsedFilter)) {
	    				  model.getColorValues().add(parsedFilter);
	    				  
	    				  if (i == filters.length - 1) {
	    					  filterMessage.append(filter); 
	    					  model.getColorCommaSeparatedValues().append("\"" + parsedFilter + "\"");
	    				  }
	    				  else {
	    					  filterMessage.append(filter + ", ");
	    					  model.getColorCommaSeparatedValues().append("\"" + parsedFilter + "\",");
	    				  }
	    				  model.setLastFacet("Color");
	    			  }
	    		  } else {
	    			  if (filter.startsWith("Brand:") || filter.startsWith("brand:") ) {
	    				  parsedFilter = parseValue(filter);
	    				  if (parsedFilter != null && !"".equals(parsedFilter))  {
	    					  model.getBrandValues().add(parsedFilter);
	    					  					  
	    					  if (i == filters.length - 1) {
	    						  filterMessage.append(filter);			  
	    						  model.getBrandCommaSeparatedValues().append("\"" + parsedFilter + "\"");
	    					  }
		    				  else {
		    					  filterMessage.append(filter + ", ");
		    					  model.getBrandCommaSeparatedValues().append("\"" + parsedFilter + "\",");
		    				  }
	    					  model.setLastFacet("Brand");
	    				  }
	    				 
	    			  } else if (filter.startsWith("Gender:") || filter.startsWith("gender:")) {
	    				  parsedFilter = parseValue(filter);
	    				  if (parsedFilter != null && !"".equals(parsedFilter))  {
	    					  model.getGenderValues().add(parsedFilter);
	    					  if (i == filters.length - 1) filterMessage.append(filter);
		    				  else filterMessage.append(filter + ", ");
	    					 
	    				  }
	    				  
	    			  }
	    			  
	    		  }
	    	  }
	    	  display.getFilters().put("selectedfilters", filterMessage.toString());
	    	  model.getFilterMessage().append(filterMessage);
	    	  
	    	 
	    	  if (model.getBrandCommaSeparatedValues().length() > 0 &&
	    			  model.getBrandCommaSeparatedValues().charAt(model.getBrandCommaSeparatedValues().length() - 1) == ',') {
	    		  model.getBrandCommaSeparatedValues().setCharAt(model.getBrandCommaSeparatedValues().length() - 1, ' ');
	    	  }
	    	  if (model.getColorCommaSeparatedValues().length() > 0 &&
	    			  model.getColorCommaSeparatedValues().charAt(model.getColorCommaSeparatedValues().length() - 1) == ',') {
	    		  model.getColorCommaSeparatedValues().setCharAt(model.getColorCommaSeparatedValues().length() - 1, ' ');
	    	  }
	      }
	     
	      
	      SearchService service = new SearchService();
	      SearchResult result = service.runSearch(model);
	      
	      try {
	    	  if (result.getResult() != null) {
			      JSONParser parser = new JSONParser();
			      display.getOutput().put("results", 
			    		  (JSONObject)parser.parse(result.toString()));
	    	  }
		      
	      } catch (Exception e) {
	    	  JSONObject obj = new JSONObject();
	    	  obj.put("message", e.getMessage());
	    	  display.getOutput().put("exceptions", obj);
	      }
	      
	      PrintWriter out = response.getWriter();
	      request.setAttribute("output", out);
	      /* Output should be json */
	      out.println(display.toString());
	      
	   }

	   public void destroy() {
	      // do nothing.
	   
	   }
	   
	   /**
	    * Parses values in filter. "Color:blue" and returns "blue"
	    * @param input
	    * @return
	    */
	   private String parseValue(String input) {
		   String result = null;
		   if (input == null || "".equals(input)) return result;
		   int startIndex = input.indexOf(':');
		   if (startIndex != -1 && startIndex + 1 <= input.length() - 1) {
			   result = input.substring(startIndex + 1, input.length());
		   }
		   return result;
	   }
}