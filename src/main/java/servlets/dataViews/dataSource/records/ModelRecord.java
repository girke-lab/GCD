/*
 * ModelRecord.java
 *
 * Created on September 27, 2007, 11:10 AM
 *
 */

package servlets.dataViews.dataSource.records;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.LeafRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.dataViews.dataSource.structure.MultiChildRecord;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ModelRecord  extends MultiChildRecord
{
    
    private static final Logger log=Logger.getLogger(ModelRecord.class);
    private static final int reqSize=3;
    
    public Integer model_id;
    public String key;
	 public String description;


    /** Creates a new instance of ModelRecord */
    public ModelRecord(List values)
    {
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        

        model_id=new Integer((String)values.get(0));
        key=(String)values.get(1);
        description=(String)values.get(2);
    }

    @Deprecated public void printHeader(Writer out, RecordVisitor visitor) throws IOException { }
    @Deprecated public void printRecord(Writer out, RecordVisitor visitor) throws IOException { }
    @Deprecated public void printFooter(Writer out, RecordVisitor visitor) throws IOException { }

    public Object getPrimaryKey()
    {
        return  model_id;
    }

    public KeyTypeUser.KeyType getPrimaryKeyType()
    {
        return KeyType.MODEL;
    }

    public KeyTypeUser.KeyType[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
    }

    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,1,reqSize+1){
            public Record createRecord(List l)
            {
                return new ModelRecord(l);
            }
            public Class getRecordType()
            {
                return ModelRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getModelRecordQuery(qp.getIds(),keyType);
            }
            public KeyType[] getSupportedKeyTypes() 
            { 
                return new KeyType[]{KeyType.ACC,KeyType.SEQ};
            }
                       
        };
    }                    
}
