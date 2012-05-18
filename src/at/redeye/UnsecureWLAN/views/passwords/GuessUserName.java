/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.passwords;

/**
 *
 * @author root
 */
public class GuessUserName {
    
    
    public static UserName guess_stringbases( String data ) 
    {
        int uindex = data.indexOf("user");
        int pindex = data.indexOf("pass");              
        
        String username = null;
        String password = null;
        
        if( uindex > 0 ) {                                    
            int issign = data.indexOf("=",uindex);
            int endsign = data.indexOf("&",uindex);
            
            if( issign > 0 ) {
                if( endsign > 0 ) {
                    username = data.substring(issign+1, endsign);
                } else {
                    username = data.substring(issign+1);
                }
            }
        }
        
        if( pindex > 0 ) {                                    
            int issign = data.indexOf("=",pindex);
            int endsign = data.indexOf("&",pindex);
            
            if( issign > 0 ) {
                if( endsign > 0 ) {
                    password = data.substring(issign+1, endsign);
                } else {
                    password = data.substring(issign+1);
                }                
            }
        }        
        
        if( username != null && password != null ) {
            return new UserName(username, password);
        }
        
        return null;
    }
    
    public static UserName guess( String data )
    {
        UserName username = null;
        
        if( ( username = guess_stringbases(data) ) != null )
            return username;
        
        return null;
    }
    
}
