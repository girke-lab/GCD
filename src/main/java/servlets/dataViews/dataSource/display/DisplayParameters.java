/*
 * DisplayParameters.java
 *
 * Created on January 3, 2007, 3:39 PM
 *
 */

package servlets.dataViews.dataSource.display;

import java.io.Writer;

/**
 *
 * @author khoran
 */
public class DisplayParameters
{
    private int hid=-1;
    private String sortDir="ASC";
    private String sortCol="";
    private String compView = "";
    private Writer out;
    
    /** Creates a new instance of DisplayParameters */
    public DisplayParameters(Writer out)
    {
        this.out=out;        
    }

    public Writer getWriter()
    {
        return out;
    }
    public void setWriter(Writer out)
    {
        this.out=out;
    }
    public int getHid()
    {
        return hid;
    }

    public void setHid(int hid)
    {
        this.hid = hid;
    }

    public String getSortDir()
    {
        return sortDir;
    }

    public void setSortDir(String sortDir)
    {
        this.sortDir = sortDir;
    }

    public String getSortCol()
    {
        return sortCol;
    }

    public void setSortCol(String sortCol)
    {
        this.sortCol = sortCol;
    }

    public String getCompView()
    {
        return compView;
    }

    public void setCompView(String compView)
    {
        this.compView = compView;
    }
    
}
