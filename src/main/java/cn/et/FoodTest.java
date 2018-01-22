package cn.et;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class FoodTest {

	//请求的URL
	static String url = "http://192.168.6.128:8080/solr/core1";
	//创建solr客户端
	static HttpSolrClient client;
	static{
		client = new HttpSolrClient(url);
	}
	public static void main(String[] args) throws IOException, SolrServerException {
		//writeFood();
		readFood();
	}
	
	
	/**
	 * 将food数据写入Solr
	 * @throws SolrServerException 
	 * @throws IOException 
	 */
	public static void writeFood() throws IOException, SolrServerException{
		Food food = new Food();
		food.setId("20");
		food.setFoodname_ik("黑椒牛柳");
		client.addBean(food);
		client.commit();
		client.close();
	}
	
	
	/**
	 * 从solr中读取数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void readFood() throws SolrServerException, IOException{
		SolrQuery sq=new SolrQuery();  
        sq.setQuery("foodname_ik:*");   
        List<Food> sdl=client.query(sq).getBeans(Food.class);  
        for(Food sd:sdl){  
            System.out.println(sd.getId()+":"+sd.getFoodname_ik());  
        }  
        client.commit();
        client.close();
	}

}
