/*
 * KeyTypeUser.java
 *
 * Created on June 16, 2005, 3:07 PM 
 */

package servlets;

/**
 * This interface should be implemented by classes that either use or
 * provide internal database id numbers. Right now, this is only
 * Search objects, Dataview objects and Records.  The idea is this:
 * Search objects state which key types they can provide with the
 * getSupportedKeyTypes() method.  Dataviews state which key types
 * they can use to find data  with the same method.  Then a third class
 * is told which dataview and search object to use (by the user). 
 * This class calls the getSupportedKeyTypes on the search and dataview
 * objects and finds a common key type in the returned lists.  This is a
 * key that the search object can provide, and the datavew can use.  
 * Then both the search object and the dataview object are told which key 
 * type to use with the setKeyType() method. The getKeyType() can be used
 * later to find out which key type was set.  
 * 
 * They setKeyType() method must be called before the object can be used.
 * If an unsupported key type is given, an UnsupportedKeyTypeException  exception 
 * is thrown.
 * @author khoran
 */
public interface KeyTypeUser
{
    enum KeyType {  SEQ,        MODEL,
                    CLUSTER,    BLAST,
                    ACC,        QUERY,                    
                    COMP,       DETAIL,
                    PSK,        CORR,
                    EXP_DEF,    PSK_COMP,
                    PSK_EXP,    PSK_EXP_COMP
    };
    
    
    /**
     * A list of key types that this class can make use of.  Values
     * should be one of the Common.KEY_TYPE_* variables.
     * @return array of supported key types
     */
    public KeyType[] getSupportedKeyTypes();
        
    /**
     * After a key type has been decided upon by the used class, 
     * it should be set with this method so it knowns which of
     * it supported keys it should actually use.
     * 
     * This method must be called before the class is used.
     * @param keyType One of the Common.KEY_TYPE_* values
     * @throws servlets.exceptions.UnsupportedKeyTypeException if the given key type is not supported
     */
    public void setKeyType(KeyType keyType) throws servlets.exceptions.UnsupportedKeyTypeException;
    /**
     * Returns the key type in use.  Will return an invalid
     * id if the setKeyType() method has not been called yet.
     * @return key type in use
     */
    public KeyType  getKeyType();
}
