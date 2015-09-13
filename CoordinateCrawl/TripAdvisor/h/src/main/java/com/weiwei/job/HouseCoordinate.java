/**
*  @Title HouseCoordinate.java 
*  @package  com.weiwei.job 
*  @Description  this program aims at crawling location data of rental hotels in Carlsbad California from the website:http://www.tripadvisor.com
*  @author Weiwei Jiang 
*  @date: 2015/9/11
 */
package com.weiwei.job;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
           HttpClient httpClient = new DefaultHttpClient();
           HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY); 
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
        String urlstartpart = "http://www.tripadvisor.com/VacationRentals-g32171-Reviews";
        String urlendpart = "-Carlsbad_California-Vacation_Rentals.html";
        String urlmiddlepart="";
        String url=urlstartpart+urlendpart;
        String iteratorstring="";
        try{
        String html = getHtml(url);
        /**HashSet<String> hotelurls is to store and comnbine the name attribute of rental hotels 
        /*in total three web pages introducing Carlsbed California  
        */
        Set hotelurls= getId(html);
        for (int i = 2; i <= 3; i++) {
            	urlmiddlepart="-oa"+Integer.toString(50*(i-1));
                url = urlstartpart+urlmiddlepart+urlendpart;
                html = getHtml(url);
                //method allAll combines Set returned by getId()
                hotelurls.addAll(getId(html));
         }
        //iterate hotelurls
       for (Iterator iter = hotelurls.iterator(); iter.hasNext();)
       {   iteratorstring=iter.next().toString();
       //crawl loacation data(longtitude and latitude) of each rental hotel
    	   detail("http://www.tripadvisor.com/VacationRentalReview-"+iteratorstring+".html",iteratorstring);} 
       }catch(Exception e){e.printStackTrace();}
       } 
        //crawl rantal hotels'name using regex expression     
    public Set  getId(String html) throws IOException {
        Pattern p = Pattern.compile("g32171-d\\d+-.+.Carlsbad.California");
        Matcher m = p.matcher(html);
        Set s=new HashSet<String>();
        while(m.find())
        {
        	s.add(m.group());
        	m.find();
        }
        return s;
       
        }
    ////crawl loacation data(longtitude and latitude) of each rental hotel
	public void detail(String hotelurl,String hotelname) {
        String url=hotelurl;
        try {
        	Document doc = Jsoup.parse(getHtml(url));
            doc.html();
            Elements elements = doc.getElementsByTag("div");
            String longitude = "";
            String latitude = "";
            for (Element element : elements) 
            {
                if ( "mapContainer".equals(element.attr("class")))
                {
                     longitude = element.attr("data-lng");
                     latitude=element.attr("data-lat");
                     
                } 
            }
            System.out.println(hotelname+" : "+longitude+"   "+latitude);
           } catch (Exception e) {
            e.printStackTrace();
           }
    }
}



