/*
 * RecordBuilder.java
 *
 * Created on January 21, 2005, 2:16 PM
 */

package servlets.dataViews.dataSource.structure;

import servlets.dataViews.dataSource.*;

/**
 * This class is no longer used.
 * @author khoran
 * @deprecated replaced by {@link RecordInfo }
 */
@Deprecated
public abstract class RecordSource 
{
    abstract Record buildRecord(java.util.List l);
    
    RecordGroup buildRecordGroup()
    {
        return new RecordGroup();
    }
}
