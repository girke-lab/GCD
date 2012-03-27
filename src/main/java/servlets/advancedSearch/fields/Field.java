/*
 * Field.java
 *
 * Created on September 7, 2004, 11:41 AM
 */

package servlets.advancedSearch.fields;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;
import org.apache.log4j.Logger;

 /**
 * This class is used to store information about a searchable field and
 * and can also render the search field in html.
 * 
 * All 'set' methods will return the modified Field object also, so that
 * you can string several modifications together in one line.
 */
 abstract public class Field
 {

    /**
     * Defines a list of supported databases. The {@link like } method
     * returns the correct keyword for a caseless pattern match for each database.
     * This is the only relavent difference between these databases.
     */
    public enum DbType{
        POSTGRESQL("ILIKE"),
        MYSQL("LIKE");

        private final String like;
        DbType(String like)
        {
            this.like=like;
        }

        public String like()
        {
            return like;
        }

    };
    protected static Logger log=Logger.getLogger(Field.class);

    public String displayName,dbName;       

    private DbType dbType=DbType.POSTGRESQL; 

    private boolean sortable=false; //fields are only sortable if they are in a 1-1 relation with the primary key.
    private boolean hidden=false; //hidden fields are not displayed to user

    /**
     * Create a new field with the given display name and 
     * database name.
     * @param name name to display on web page
     * @param dbn a fully qualified database column name
     */
    public Field(String name, String dbn)
    {
        displayName=name;
        dbName=dbn;
    }

    /**
     * set the database type. Currenly this is either postgresql or mysql (
     * although mysql has not been used for a long time, so I can't say if it
     * will still work for everything anymore).
     * @param dt the type of database to use
     * @return The modified Field object
     */
    public Field setDbType(DbType dt)
    {
        dbType=dt;
        return this;
    }
    public DbType getDbType()
    {
        return dbType;
    }

    /**
     * Returns html view of this field with the current value.
     * This is just a default implementation.  Sub classes should
     * override this.
     * @param currentValue value of field, as entered by user
     * @return html required to draw this field
     */
    public String render(String currentValue)
    { //provide a defualt action 
        return "<INPUT type=text name='values' value='"+currentValue+"'>";
    }

    /**
     * The type of the value this field represents.
     * @return a Class object representing the type of this field
     */
    abstract public Class getType();
    /**
     * defines a set of valid operations for this type of field
     * @return a list of operations
     */
    abstract public String[] getValidOps();

    public boolean isSortable()
    {
        return sortable;
    }

    /**
     * Tag this field as being sortable. Any tagged 
     * fields will automatically show up in the 'sort' drop down box.
     */
    public Field setSortable(boolean sortable)
    {
        this.sortable = sortable;
        return this;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    /**
     * A hidden field will not be rendered.  This is not currently
     * used for anything.  The ideas was that you could use this to 
     * add additional conditions to the query, regardless of whether or not
     * the user added them.
     * @param hidden 
     * @return 
     */
    public Field setHidden(boolean hidden)
    {
        this.hidden = hidden;
        return this;
    }
    public String toString()
    {        
        return displayName+", "+dbName+", type: "+getType();
    }
}