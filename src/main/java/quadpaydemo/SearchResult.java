package quadpaydemo;

import java.util.List;

/**
 * Hold search result fields like title, categories, price, brand etc
 * For the demo, just hold the json response string
 * @author rakeshsharma
 *
 */
public class SearchResult {
	/* In the real world, this class may hold below data structure.
	 * But for the demo, just hold the json string
	 * public List<String> productName;
	public List<String> price;
	public List<String> String categories*/
	private String result;
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return this.result;
	}
	
	public String toString() {
		return result;
	}
}
