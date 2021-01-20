package quadpaydemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold input/output Search Data
 * @author rakeshsharma
 *
 */
public class SearchModel {
	private String keyword = null; // Keyword
	private String lastFacet = null; // If not null, then user clicked on a facet
	
	// Used to print messages in 
	private StringBuilder colorCommaSeparatedValues = new StringBuilder();
	private StringBuilder brandCommaSeparatedValues = new StringBuilder();
	
	// Filter messages currently applied on json output
	private StringBuilder filterMessage = new StringBuilder();
	// Values to hold brand/color filter values
	private List<String> color = new ArrayList<String>();
	private List<String> brand = new ArrayList<String>();
	private List<String> gender = new ArrayList<String>();
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public void setLastFacet(String lastFacet) {
		this.lastFacet = lastFacet;
	}
	
	public String getLastFacet() {
		return this.lastFacet;
	}
	
	public void setFilterMessage(StringBuilder filterMessage) {
		this.filterMessage = filterMessage;
	}
	
	public StringBuilder getFilterMessage() {
		return this.filterMessage;
	}
	
	public void setBrandValues(List<String> brand) {
		this.brand = brand;
	}
	
	public List<String> getBrandValues() {
		return this.brand;
	}
	
	public void setGenderValues(List<String> gender) {
		this.gender = gender;
	}
	
	public List<String> getGenderValues() {
		return this.gender;
	}
	
	public void setColorValues(List<String> color) {
		this.color = color;
	}
	
	public List<String> getColorValues() {
		return this.color;
	}
	
	public void setColorCommaSeparatedValues(StringBuilder colorCommaSeparatedValues) {
		this.colorCommaSeparatedValues = colorCommaSeparatedValues;
	}
	
	public StringBuilder getColorCommaSeparatedValues() {
		return this.colorCommaSeparatedValues;
	}
	
	public void setBrandCommaSeparatedValues(StringBuilder brandCommaSeparatedValues) {
		this.brandCommaSeparatedValues = brandCommaSeparatedValues;
	}
	
	public StringBuilder getBrandCommaSeparatedValues() {
		return this.brandCommaSeparatedValues;
	}
	
}
