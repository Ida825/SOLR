package cn.et;

import org.apache.solr.client.solrj.beans.Field;

public class Food{

	public Food() {
	
	}
	
	@Field  
    private String id;  
    @Field  
    private String foodname_ik;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFoodname_ik() {
		return foodname_ik;
	}
	public void setFoodname_ik(String foodname_ik) {
		this.foodname_ik = foodname_ik;
	}
}
