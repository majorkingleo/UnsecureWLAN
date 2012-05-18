/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.UnsecureWLAN.views.images;

/**
 *
 * @author root
 */
public class DetectImage {
    
    public enum IMAGE_TYPES {
        UNKNOWN,
        GIF,
        JPEG,
        PNG
    };
    
    public static IMAGE_TYPES guessImageType( byte bytes[] ) {
        
        if( is_gif( bytes ) ) {
            return IMAGE_TYPES.GIF;
        }
        
        return IMAGE_TYPES.UNKNOWN;
    }
    
    public static boolean is_gif( byte bytes[] ) {                
        
        if( bytes.length > 3 ) {
            if( bytes[0] == 'G' &&
                bytes[1] == 'I' &&
                bytes[2] == 'F' ) {
                return true;
            }
        }
        
        return false;
    }
}
