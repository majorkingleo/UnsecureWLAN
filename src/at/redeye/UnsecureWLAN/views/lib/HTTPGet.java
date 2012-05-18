/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.lib;

import java.util.HashMap;

/**
 *
 * @author root
 */
public class HTTPGet 
{
    String data;
    String url;
    HashMap<String,String> http_headers = new HashMap();
    
    public HTTPGet( String data )
    {
        this.data = data;
        parse();
    }
    
/**
 * Parses a HTTP Haader
 *  
 * Example Data: 
 *
 * 2012-05-17 22:19:10,323 DEBUG (BrowserView.java:92): data: GET /hprofile-ak-snc4/368995_1451523051_923485445_q.jpg HTTP/1.1
 * Host: profile.ak.fbcdn.net
 * User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:12.0) Gecko/20100101 Firefox/12.0
 * Accept: image/png,image/*;q=0.8,/*;q=0.5
 * Accept-Language: de-de,de;q=0.8,en-us;q=0.5,en;q=0.3
 * Accept-Encoding: gzip, deflate
 * Connection: keep-alive
 * Referer: http://www.facebook.com/messages/?action=read&tid=id.221941917908820
 * If-Modified-Since: Fri, 01 Jan 2010 00:00:00 GMT
 * Cache-Control: max-age=0
 */    
    private void parse()
    {
        String lines[] = data.split("\n");
        
        for( int i = 0; i < lines.length; i++ )
        {
            String line = lines[i];
            
            if( i == 0 ) {
               if( line.contains("GET") ) {
                   String parts[] = line.split(" ");
                   if( parts.length > 1 ) {
                       url = parts[1];
                   }
               }
            } else {
                
                String parts[] = lines[i].split(":");
                
                if( parts.length >= 2 ) {
                    http_headers.put(parts[0], parts[1].trim());
                }                                                
            }
        }
    }
    
    public String getRequestUrl()
    {
        String host =  http_headers.get("Host");
        
        if( host != null ) {
            return "http://" + host + url;
        }
        
        return url;
    }
    
}
