/**
*  @Title HouseCoordinate.java 
*  @package  com.weiwei.job 
*  @Description  this program aims at crawling location data of rental hotels in Carlsbad California from the website:https://www.flipkey.com
*  @author Weiwei Jiang 
*  @date: 2015/9/5
 */
package com.weiwei.job;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * Main Application of the program 
 *
 *Contains methods to parse web pages,crawl website links introducing rental hotels,
 *and crawl location data(longtitude and latitude) of each rental hotel
 */
public class HouseCoordinate {
   HttpClient httpClient;

    public static void main(String[] args) {
        HouseCoordinate housecoordinate = new HouseCoordinate();
      //method index() is definited below to combine several other methods to achieve main aims 
        housecoordinate.index();
    }
  //method to parse web pages and return its string format
    public String getHtml(String url) {

        try {
        	//initialize a http client
            httpClient = HTTPClientIgnoreVerification.newHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpget);
            StatusLine statusLine = response.getStatusLine();
          //if  request succeed  ,then the statuscode will be 200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }else{
                System.out.println(statusLine.getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    //method index() is definited below to combine several other methods to achieve main aims 
    public void index() {
        String url = "https://www.flipkey.com/carlsbad-vacation-rentals/g32171/";
        try {
           String html = getHtml(url);
            Map<String, Object> map1 = getId(html);
            Document doc = Jsoup.parse(html);

            Element element = doc.getElementById("search-pages");
            String pageTxt = element.text();
            int idx = pageTxt.indexOf("of") + 2;
            String page = pageTxt.substring(idx);
            Integer p = Integer.parseInt(page.trim());
            for (int i = 2; i <= p; i++) {
                url = url + "?page=" + p;
                html = getHtml(url);
                //store HashMap result returned by getId(),where key contails information referring to render hotels
                map1.putAll(getId(html));
            }
            for (String key : map1.keySet()) {
            	//crawl location data of each rental hotel
                detail(key);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // crawl  rental hotels'id(essential for finding its introducing web page) using regex expression 
    public Map getId(String html) throws IOException {
        Pattern p = Pattern.compile("FlipKey\\.Search\\.properties\\s=(.*)FlipKey.Search.list_name", Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(html);
        if (m.find()) {
            System.out.println(m.group(1));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(m.group(1), HashMap.class);
        }

        return new HashMap();

    }
     //crawl location data of each rental hotel
    public void detail(String id) {
        String url = "https://www.flipkey.com/carlsbad-vacation-rentals/p" + id + "/";
        try {
            Document doc = Jsoup.parse(getHtml(url));
            doc.html();
          
            Elements elements = doc.getElementsByTag("meta");
            String longitude = "";
            String latitude = "";
            for (Element element : elements) {
            	//get longitude value
                if ( "og:longitude".equals(element.attr("property"))){
                     longitude = element.attr("content");
                }
                //get latitude value
                if ( "og:latitude".equals(element.attr("property"))){
                     latitude = element.attr("content");
                }

            }
            System.out.println(id+","+longitude+","+latitude);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   

}



