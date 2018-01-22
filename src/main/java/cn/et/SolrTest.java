package cn.et;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.GroupParams;

public class SolrTest {

	//请求的URL
	static String url = "http://192.168.6.128:8080/solr/core1";
	//创建solr客户端
	static HttpSolrClient client;
	static{
		client = new HttpSolrClient(url);
	}
	public static void main(String[] args) throws SolrServerException, IOException {
		group();

	}

	/**
	 * 往solr写入数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void write() throws SolrServerException, IOException{	
		//创建写入solr的SolrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "5");
		document.addField("name_ik", "鱼香肉丝");
		document.addField("price_d", "12.55");
		//添加document
		client.add(document);
		
		client.commit();
		client.close();		
	}
	
	
	/**
	 * 从solr读取数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void read() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery();
		//设置搜索条件
		//query.setQuery("foodname_ik:青椒");
		//q:考虑得分，效率低  fq:不考虑得分，效率高
		//查询
		query.setQuery("foodname_ik:青椒");
		//过滤
		//query.setFilterQueries("foodname_ik:青椒");
		//设置排序
		//query.set("sort","id desc");
		query.setSort("id",ORDER.asc);
		
		//分页
		//开始位置
		query.setStart(0);
		//设置每页显示的条数
		query.setRows(2);
		QueryResponse qr = client.query(query);

		//获取查询结果并遍历
		SolrDocumentList results = qr.getResults();
		for (SolrDocument solrDocument : results) {
			String id = solrDocument.get("id").toString();
			System.out.println(id+":"+solrDocument.get("foodname_ik"));
		}
	}
	
	/**
	 * 高亮显示
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void highlight() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery();
		//查询
		query.setQuery("foodname_ik:青椒");
	
		//设置是否高亮
		query.setHighlight(true);
		
		query.addHighlightField("foodname_ik");
		//设置前后缀
		query.setHighlightSimplePre("<font color=red>");
		query.setHighlightSimplePost("</font>");	
		
		QueryResponse qr = client.query(query);		
		//获取高亮
		Map<String, Map<String, List<String>>>  hlMap = qr.getHighlighting();
		
		//获取查询结果并遍历
		SolrDocumentList results = qr.getResults();
		for (SolrDocument solrDocument : results) {
			String id = solrDocument.get("id").toString();

			
			Map<String, List<String>> ml = hlMap.get(id);
			List<String> list = ml.get("foodname_ik");
			String hlStr = list.get(0);
			System.out.println(hlStr);
		}
	}
	
	/**
	 * 统计分类个数
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void facet() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery();
		//设置分类统计
		query.setFacet(true);
		//按字段分类  相同的归于一类
		query.addFacetField("type_s");
 
        //根据 count 数量 升序和降序 也可以根据索引   
		query.setQuery("*:*"); 
		//查询
		QueryResponse qr = client.query(query);
		//获取分类
		List<FacetField> facetFields = qr.getFacetFields();
		for (FacetField ff : facetFields) {
			List<Count> counts = ff.getValues();
			for (Count count : counts) {
				System.out.println(count.getName()+"--"+count.getCount());
			}
		}
	}
	
	/**
	 * 分组
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void group() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery("content_ik:*");
		//设置分组
		query.setParam(GroupParams.GROUP,true);
		query.setParam(GroupParams.GROUP_FIELD, "type_s");
		//query.setParam(GroupParams.GROUP_LIMIT,"10");
		query.setParam("group.ngroups", true);  
		query.setParam(GroupParams.GROUP_LIMIT, "5");	
		QueryResponse qr = client.query(query);
		GroupResponse ff = qr.getGroupResponse();
		List<GroupCommand> values = ff.getValues();
		for (GroupCommand me : values) {
			// System.out.println(me.getName());  
             List<Group> groups=me.getValues();  
             for (Group group : groups) {
            	System.out.println(group.getGroupValue());
				SolrDocumentList result = group.getResult();
				for (SolrDocument sd : result) {
					System.out.println(sd.getFieldValue("content_ik"));
				}
				System.out.println("--------------------");
			}
		}
		
		client.commit();
		client.close();
	}
	
	/**
	 * 从solr删除数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void deleteById() throws SolrServerException, IOException{
		//根据id删除
		client.deleteById("5");
		client.commit();
		client.close();
	}
	
	/**
	 * 根据条件删除
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void deleteByCondition() throws SolrServerException, IOException{
		client.deleteByQuery("name_ik:鱼香肉丝");
		client.commit();
		client.close();
	}
	
	
}
