/*  
 * AffyDataVew.java
 *
 * Created on August 4, 2005, 9:53 AM
 * 
 */

package servlets.dataViews;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.dataViews.queryWideViews.DefaultQueryWideView;
import servlets.dataViews.records.*;
import servlets.search.Search;

/**
 *
 * @author khoran
 */

public class AffyDataView implements DataView
{
    private static Logger log=Logger.getLogger(AffyDataView.class);
    
    private int keyType, hid;
    private String sortDir, sortCol,action;
    private int[] dbNums;        
    private Set nodeSet=null;
    private List[] newIds;
    DbConnection dbc=null;  
    
    //indexes into idLists list 
    public static final int ACC=0, PSK=1, ES=2,GROUP=3;
    
    /** Creates a new instance of AffyDataVew */
    public AffyDataView(HttpServletRequest request)
    {
        sortDir="ASC";        
        newIds=new List[4]; //temp storage.
        for(int i=0;i<4;i++)
            newIds[i]=new LinkedList();
        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
        
        processRequest(request);
    }

    public int getKeyType()
    {
        return keyType;
    }

    public servlets.dataViews.queryWideViews.QueryWideView getQueryWideView()
    {
        return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out, "Total Query",
                    new String[]{"Keys found"},new Integer[]{new Integer(search.getResults().size())});
            }
            public void printButtons(PrintWriter out, int hid, int pos, int c, int d)
            {                
            }          
            public void printGeneral(PrintWriter out, Search search, String position, Map storage)
            {
                if(newIds[ACC].size()==0)
                { //make sure setIds has been called first
                    log.error("no accession ids, has setIds() been called yet?");
                    nodeSet=null;
                    return;
                }
                
                nodeSet=(Set)storage.get(newIds[ACC]); //use the accession_id list as a per-page key
                if(nodeSet==null) //first page of query
                {
                    nodeSet=new HashSet();
                    storage.put(newIds[ACC],nodeSet);                    
                }                                
                //now that we have a new/existing set of keys, add the ids for this request
                log.debug("acc ids: "+newIds[ACC]);
                if(action==null)
                    for(Iterator i=newIds[ACC].iterator();i.hasNext();)
                        nodeSet.add(new Node(new Integer((String)i.next()),null,null));
                else 
                {
                    Iterator accItr,pskItr,esItr,groupItr;
                    accItr=newIds[ACC].iterator();
                    pskItr=newIds[PSK].iterator();
                    esItr=newIds[ES].iterator();
                    groupItr=newIds[GROUP].iterator();
                    
                    boolean add=action.equals("expand");
                    if(!add && !action.equals("collapse"))
                    {
                        log.warn("invalid action: "+action);
                        return;
                    }
                    
                    while(accItr.hasNext() && pskItr.hasNext() && esItr.hasNext())
                    {
                        Integer group=null;
                        Integer acc=new Integer((String)accItr.next());
                        
                        if(groupItr.hasNext())
                            group=(Integer)groupItr.next();
                        Node n=new Node(acc, (Integer)pskItr.next(), (Integer)esItr.next(),group);
                        
                        //remove nodes with just an acc now that we have a psk and es.
                        nodeSet.remove(new Node(acc,null,null));
                        
                        if(add)
                            nodeSet.add(n);
                        else
                            nodeSet.remove(n);
                        
                    }
                    log.debug("nodeSet="+nodeSet);
                    
//                    while(accItr.hasNext() && pskItr.hasNext())
//                        updateTree(add, (Integer)accItr.next(), (Integer)pskItr.next(),
//                                        (Integer)esItr.next(),(Integer)groupItr.next());

                }
            }
         };
        
    }
    private List getIdLists()
    {
        List lists=new ArrayList(4);
        Node n;
        lists.add(new LinkedList());
        for(int i=PSK;i<=GROUP;i++)
            lists.add(new HashSet()); //avoid duplicates
        for(Iterator i=nodeSet.iterator();i.hasNext();)
        {
            n=(Node)i.next();
            ((List)lists.get(ACC)).add(n.getAcc());
            ((Collection)lists.get(PSK)).add(n.getPsk());
            ((Collection)lists.get(ES)).add(n.getEs());
            if(n.getGroup()!=null)
                ((Collection)lists.get(GROUP)).add(n.getGroup());
        }
        return lists;
    }
//    private void updateTree(boolean add, Integer acc, Integer psk, Integer es, Integer group)            
//    {
//        Map accMap=(Map)idTree.get(acc);
//        if(accMap==null)
//        {
//            accMap=new HashMap();
//            idTree.put(acc,accMap);
//        }
//        
//        Map pskMap=(Map)accMap.get(psk);
//        if(pskMap==null)
//        {
//            pskMap=new HashMap();
//            accMap.put(psk, pskMap);
//        }
//        
//        // if add is false, we are removing.
//        // if group is null, remove es, otherwise
//        // remove the group
//        if(!add && group==null)
//        {
//            pskMap.remove(es);
//            return;
//        }
//        //else add es
//        Map esMap=(Map)pskMap.get(es);
//        if(esMap==null)
//        {
//            esMap=new HashMap();
//            pskMap.put(es,esMap);
//        }
//        
//        if(!add)
//        { //remove group 
//            esMap.remove(group);
//            return;
//        }        
//        if(group==null) //don't add group if its null
//            return;
//        Map groupMap=(Map)esMap.get(group);
//        if(groupMap==null)
//        {
//            groupMap=new HashMap();
//            esMap.put(group,groupMap);
//        }
//        
//    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_MODEL};
    }
   
    public void setData(String sortCol, int[] dbList, int hid)
    {
        this.hid=hid;
        this.sortCol=sortCol;
        this.dbNums=dbList;     
    }

    public void setIds(java.util.List ids)
    {
        newIds[ACC]=ids;        
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyType
    {
        this.keyType=keyType;
    }

    public void setSortDirection(String dir)
    {
         if(dir!=null && (dir.equalsIgnoreCase("asc") || dir.equalsIgnoreCase("desc")))
            sortDir=dir;   
    }
    
    
    public void printHeader(java.io.PrintWriter out)
    {        
        Common.printUnknownHeader(out);
        Common.printUnknownsSearchLinks(out);
        
        out.println(
                "<style type='text/css'>" +
                    ".test a {color: #006699}\n" +
                    ".test a:hover {background-color: #AAAAAA}\n" +
                "</style>");
        out.println("<div class='test'>");
    }

    public void printStats(java.io.PrintWriter out)
    {
         Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(newIds[ACC].size())});
    }
    public void printData(java.io.PrintWriter out)
    {
        printData(out,getRecords());
        out.println("</td></table>"); //close page level table
    }
////////////////////////////////////////////////////////////////////////////////
    private void processRequest(HttpServletRequest request)
    {
        action=request.getParameter("action");
        
        String[] psk_ids=request.getParameterValues("psk_ids");
        String[] es_ids=request.getParameterValues("es_ids");
        String[] groups=request.getParameterValues("groups");
        
        log.debug("action="+action);
        log.debug("psk_ids: "+Common.printArray(psk_ids)); 
        if(psk_ids!=null)
            log.debug(psk_ids.length+" psk ids");
        log.debug("es_ids: "+Common.printArray(es_ids));
        if(es_ids!=null)
            log.debug(es_ids.length+" es ids");
        log.debug("groups: "+Common.printArray(groups));
        if(groups!=null)
            log.debug(groups.length+" groups");               
                
        
        
        // the index of each array in this array must 
        // match the values of PSK,ES, GROUP, respectivly.
        String[][] idSets=new String[][]{psk_ids,es_ids,groups};
        List l;
        for(int i=PSK-1;i<=GROUP-1;i++)
        { //loop over PSK, ES,GROUP            
            l=(List)newIds[i+1];
            for(int j=0;idSets[i]!=null && j<idSets[i].length;j++)
                l.add(new Integer(idSets[i][j]));    
        }
        log.debug("newIds="+Common.printArray(newIds));
    }
    private Collection getRecords()
    {        
        List idLists=getIdLists();
        Map[] subRecordMaps=new Map[]{
            AffyExpSetRecord.getData(dbc,idLists)            
        };
        Map records=UnknownRecord.getData(dbc,(List)idLists.get(ACC),sortCol,sortDir,subRecordMaps);
        return records.values();                                 
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        //log.debug("printing "+data.size()+" records");
        out.println("<TABLE bgcolor='"+Common.dataColor+"' width='100%'" +
            " align='center' border='1' cellspacing='0' cellpadding='0'>");
        RecordGroup rec;
        RecordVisitor visitor=new HtmlRecordVisitor();
        ((HtmlRecordVisitor)visitor).setHid(hid);
        try{
            for(Iterator i=data.iterator();i.hasNext();)
                ((RecordGroup)i.next()).printRecords(out,visitor);  
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</TABLE></div>");
    }
    
    class Node
    {
        private Integer acc,psk,es,group;
        
        public Node(Integer acc,Integer psk, Integer es, Integer group)
        {
            this.acc=acc;
            this.psk=psk;
            this.es=es;
            this.group=group;
        }
        public Node(Integer acc,Integer psk, Integer es)
        {
            this.acc=acc;
            this.psk=psk;
            this.es=es;
            this.group=null;
        }
        public boolean equals(Object o)
        {
            if(this==o)
                return true;
            if(!(o instanceof Node))
                return false;
            Node n=(Node)o;
            if(n.group==null) //if we don't have a group, don't use it
                return n.acc==acc && n.psk==psk && n.es==es;
            return n.acc==acc && n.psk==psk && n.es==es && n.group==group;
        }
        public int hashCode()
        {            
            if(psk==null || es==null)
                return acc;
            return acc+psk+es;
        }
        public String toString()
        {
            return acc+"_"+psk+"_"+es+"_"+group;
        }

        public Integer getAcc()
        {
            return acc;
        }
        public Integer getPsk()
        {
            return psk;
        }
        public Integer getEs()
        {
            return es;
        }
        public Integer getGroup()
        {
            return group;
        }
    }
    
    
}
