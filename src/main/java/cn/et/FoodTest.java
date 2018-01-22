package cn.et;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class FoodTest {

	//�����URL
	static String url = "http://192.168.6.128:8080/solr/core1";
	//����solr�ͻ���
	static HttpSolrClient client;
	static{
		client = new HttpSolrClient(url);
	}
	public static void main(String[] args) throws IOException, SolrServerException {
		//writeFood();
		readFood();
	}
	
	
	/**
	 * ��food����д��Solr
	 * @throws SolrServerException 
	 * @throws IOException 
	 */
	public static void writeFood() throws IOException, SolrServerException{
		Food food = new Food();
		food.setId("20");
		food.setFoodname_ik("�ڽ�ţ��");
		client.addBean(food);
		client.commit();
		client.close();
	}
	
	
	/**
	 * ��solr�ж�ȡ����
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
