/*
 * GoDag.java
 *
 * Created on January 28, 2004, 2:30 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.File;
import java.util.regex.*;

import javax.xml.bind.*;
import GO.go_jaxb.*;
import khoran.debugPrint.Debug;


public class GoDag 
{
    private GoNode root; //first node in GO DAG.
    private HashMap index;     
    private HashMap tempMap; //temp storage used while loading the data
    private Pattern goPattern;
    private Debug d;
    
    /** Creates a new instance of GoDag */
    public GoDag(String xmlFilename) 
    {//open and parse the xml file
        goPattern=Pattern.compile(".*GO:(\\d{7})");
        index=new HashMap();        
        d=new Debug();
//        d.setPrintLevel(1);
        buildDag(xmlFilename);         
    }
    
    public GoNode find(int goNumber)
    {        
        return (GoNode)index.get(new Integer(goNumber));
    }
    public GoNode find(Integer goNum)
    {
        return (GoNode)index.get(goNum);
    }
    public String toString()
    {
        return root.toString();
    }
//////////////////////////////////////////////////////////    
    private void buildDag(String file)
    {
        int relation=0;
        System.out.print("building dag...");
        try{
            JAXBContext context=JAXBContext.newInstance("GO.go_jaxb");

            Unmarshaller um=context.createUnmarshaller();
            RDF goRoot=(RDF)((Go)um.unmarshal(new File(file))).getRDF();
            
            tempMap=new HashMap();
            readTerms((List)goRoot.getTerm()); //load tempMap            
            connectTerms(); //load index
            d.print("size of tempMap: "+tempMap.size()+", size of index: "+index.size());
            //delete tempMap
            tempMap=null;
            
        }catch(JAXBException e){
            System.out.println("jaxb error: "+e.getMessage());
            e.printStackTrace();
        }        
        System.out.println("done");
    }
    private void readTerms(List terms)
    {//read in all the terms first, since they do not neccasarly come in the correct order
            for(Iterator i=terms.iterator();i.hasNext();)
            {
                Term t=(Term)i.next();
                String goStr=t.getAccession();
                int goNum=getGo(goStr);
                if(goNum!=0)
                    tempMap.put(new Integer(goNum), t);
            }
    }
    private void connectTerms()
    {//using the terms in tempMap, build the dag and load index.
        for(Iterator i=tempMap.values().iterator();i.hasNext();)
        {
            Term t=(Term)i.next();            
            //make sure we have not already built this node
            //TODO: see if searching by term is any faster
            if(index.containsKey(new Integer(getGo(t.getAccession()))))
                continue;
            
            GoNode gn=buildNode(t);
                            
        }        
    }
    private GoNode buildNode(Term t)
    {//build a GoNode from the given term.
        
        int goNum=getGo(t.getAccession());
        String name=t.getName();
        GoNode gn=new GoNode(goNum,name);
        d.print("building node "+goNum);
        //setup parent and child links        
        for(Iterator linkItr=((List)t.getPartOfOrIsA()).iterator();linkItr.hasNext();)
        {//for each parent of this node
            d.print(1,"\t adding parent to "+goNum);                   
            String resource="";
            Object next=linkItr.next();
            int relation=-1;
            Integer parentGO=null;
            
            if(next instanceof PartOf)
            {
                relation=ParentLink.PART_OF;
                parentGO=new Integer(getGo(((PartOf)next).getResource()));
            }
            else if(next instanceof IsA)
            {
                relation=ParentLink.IS_A;
                parentGO=new Integer(getGo(((IsA)next).getResource()));                
            }

            GoNode parentNode;
            //first look for the parent node in index
            parentNode=(GoNode)index.get(parentGO);
            if(parentNode==null)//parent has not yet been proccessed
            {//so look for it in tempMap
                Term term=(Term)tempMap.get(parentGO);
                if(term==null)// if term is not found here, this is an error
                {
                    d.print("node "+parentGO+", parent of "+goNum+", was not found anywhere");
                    continue;
                }
                else
                    parentNode=buildNode(term);
            }
                
            parentNode.addChild(gn);

            //set parentNode as a parent of this node                    
            gn.addParent(new ParentLink(parentNode, parentNode.getMaxDepth()+1,relation));                            
        }                          
        
        if(!gn.hasParent())
            root=gn; 
        d.print("adding "+gn.getGoNumber()+" to index");
        index.put(new Integer(goNum), gn);        
        return gn;
    }
    private int getGo(String r)
    {
        d.print(2,"matching "+r);
        Matcher m=goPattern.matcher(r);             
        if(m.matches())
            return Integer.parseInt(m.group(1));
        return 0;
    }
        
}
