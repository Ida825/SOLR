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

	//�����URL
	static String url = "http://192.168.6.128:8080/solr/core1";
	//����solr�ͻ���
	static HttpSolrClient client;
	static{
		client = new HttpSolrClient(url);
	}
	public static void main(String[] args) throws SolrServerException, IOException {
		group();

	}

	/**
	 * ��solrд������
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void write() throws SolrServerException, IOException{	
		//����д��solr��SolrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "5");
		document.addField("name_ik", "������˿");
		document.addField("price_d", "12.55");
		//���document
		client.add(document);
		
		client.commit();
		client.close();		
	}
	
	
	/**
	 * ��solr��ȡ����
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void read() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery();
		//������������
		//query.setQuery("foodname_ik:�ཷ");
		//q:���ǵ÷֣�Ч�ʵ�  fq:�����ǵ÷֣�Ч�ʸ�
		//��ѯ
		query.setQuery("foodname_ik:�ཷ");
		//����
		//query.setFilterQueries("foodname_ik:�ཷ");
		//��������
		//query.set("sort","id desc");
		query.setSort("id",ORDER.asc);
		
		//��ҳ
		//��ʼλ��
		query.setStart(0);
		//����ÿҳ��ʾ������
		query.setRows(2);
		QueryResponse qr = client.query(query);

		//��ȡ��ѯ���������
		SolrDocumentList results = qr.getResults();
		for (SolrDocument solrDocument : results) {
			String id = solrDocument.get("id").toString();
			System.out.println(id+":"+solrDocument.get("foodname_ik"));
		}
	}
	
	/**
	 * ������ʾ
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void highlight() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery();
		//��ѯ
		query.setQuery("foodname_ik:�ཷ");
	
		//�����Ƿ����
		query.setHighlight(true);
		
		query.addHighlightField("foodname_ik");
		//����ǰ��׺
		query.setHighlightSimplePre("<font color=red>");
		query.setHighlightSimplePost("</font>");	
		
		QueryResponse qr = client.query(query);		
		//��ȡ����
		Map<String, Map<String, List<String>>>  hlMap = qr.getHighlighting();
		
		//��ȡ��ѯ���������
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
	 * ͳ�Ʒ������
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void facet() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery();
		//���÷���ͳ��
		query.setFacet(true);
		//���ֶη���  ��ͬ�Ĺ���һ��
		query.addFacetField("type_s");
 
        //���� count ���� ����ͽ��� Ҳ���Ը�������   
		query.setQuery("*:*"); 
		//��ѯ
		QueryResponse qr = client.query(query);
		//��ȡ����
		List<FacetField> facetFields = qr.getFacetFields();
		for (FacetField ff : facetFields) {
			List<Count> counts = ff.getValues();
			for (Count count : counts) {
				System.out.println(count.getName()+"--"+count.getCount());
			}
		}
	}
	
	/**
	 * ����
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void group() throws SolrServerException, IOException{
		SolrQuery query = new SolrQuery("content_ik:*");
		//���÷���
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
	 * ��solrɾ������
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void deleteById() throws SolrServerException, IOException{
		//����idɾ��
		client.deleteById("5");
		client.commit();
		client.close();
	}
	
	/**
	 * ��������ɾ��
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void deleteByCondition() throws SolrServerException, IOException{
		client.deleteByQuery("name_ik:������˿");
		client.commit();
		client.close();
	}
	
	
}
