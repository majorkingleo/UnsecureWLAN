/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.passwords;

/**
 *
 * @author root
 */
public class UserName {
    String username;
    String password;
    
    public UserName( String user_name, String password )
    {
        this.username = user_name;
        this.password = password;
    }
    
    public String getUserName() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
}
