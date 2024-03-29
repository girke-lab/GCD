/*
 * PfamOptionsBean.java
 *
 * Created on January 17, 2005, 9:50 AM
 */

package servlets;

/**
 *
 * @author khoran
 */

import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;

import servlets.advancedSearch.SearchState;
import servlets.querySets.QuerySetProvider;

public class PfamOptionsBean 
{
    private static Logger log=Logger.getLogger(PfamOptionsBean.class);
    
    private String accession;
    private Map domainDescriptions;
    
    /** Creates a new instance of PfamOptionsBean */
    public PfamOptionsBean() 
    {
        domainDescriptions=new HashMap();
    }
    
    
    
    public void processesInput(HttpServletRequest request)
    {
        setAccession(request.getParameter("accession"));
        if(getAccession()==null)
            setAccession("");
        List data=Common.sendQuery(QuerySetProvider.getDataViewQuerySet().getPfamOptionsQuery(getAccession()));
         
        for(Iterator i=data.iterator();i.hasNext();)
        {
            List row=(List)i.next();         
            domainDescriptions.put(row.get(0),row.get(1));
        }
        
    }

    public void printPfamLinks(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        Set domains=getUniqueDomains();
        String n,desc;
        
        for(Iterator i=domains.iterator();i.hasNext();)
        {
            n=(String)i.next();                                            
            if(n.startsWith("noHit"))
                out.println("<li>"+n+"</li>");
            else
            {
                int j=n.indexOf('.');
                if(j!=-1)
                    n=n.substring(0,j);
                desc=(String)domainDescriptions.get(n);
                if(desc==null)
                    desc="";
                out.println("<li><a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?"+
                        n+"'>"+n+"</a>&nbsp&nbsp "+desc+"</li>");
            }
            
        }              
    }
    public void printDomainSearchLinks(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        Set domains=getUniqueDomains(); 
        String domain,desc;
        
        SearchState domainSearch=new SearchState();
        
        //setup domain search here.
        //limit=100000&sortField=0&database=common&fields=2&ops=6&bools=0&values=%PF00069%
        List fields=new ArrayList(1),ops=new ArrayList(1),bools=new ArrayList(1);
        
                
        fields.add(new Integer(2));
        ops.add(new Integer(2));
        bools.add(new Integer(0));
        
        domainSearch.setDatabase("common");
        domainSearch.setLimit("0");
        domainSearch.setSortField(0); //sets to default sort field
        
        domainSearch.setSelectedFields(fields);
        domainSearch.setSelectedOps(ops);
        domainSearch.setSelectedBools(bools);
        
        for(Iterator i=domains.iterator();i.hasNext();)
        {
            domain=(String)i.next();
            if(domain.startsWith("noHit"))
                out.println("<li>"+domain+"</li>");
            else
            {
                List l=new ArrayList(1);
                l.add("%"+domain+"%");
                domainSearch.setValues(l);
                desc=(String)domainDescriptions.get(domain);
                if(desc==null)
                    desc="";
                out.println("<li><a href='advancedSearch.jsp?"+domainSearch.getParameterString()+"'>"+
                        domain+"</a>:&nbsp&nbsp "+desc+"</li>");
            }
        }
        
    }
    private Set getUniqueDomains()
    {
        StringTokenizer tok=new StringTokenizer(getAccession(),"_");
        Set domains=new HashSet();
        while(tok.hasMoreTokens())
            domains.add(tok.nextToken());
        return domains;
    }
    
    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }
    
}
