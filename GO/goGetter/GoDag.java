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
import java.io.*;
import java.util.regex.*;

import javax.xml.bind.*;
import GO.go_jaxb.*;
import khoran.debugPrint.Debug;


public class GoDag implements Serializable
{
    private GoNode root; //first node in GO DAG.
    private HashMap index;     
    private int subNode;
    private transient HashMap tempMap; //temp storage used while loading the data
    private transient Pattern goPattern;
    private transient Debug d;
    
    /** Creates a new instance of GoDag */
    public GoDag(String xmlFilename) 
    {//open and parse the xml file
        goPattern=Pattern.compile(".*GO:(\\d{7})");
        index=new HashMap();        
        d=new Debug();
        d.setPrintLevel(1);
        subNode=-1; //no sub node given
        buildDag(xmlFilename);         
    }
    public GoDag(String xmlFilename,int subNode) 
    {//open and parse the xml file
        goPattern=Pattern.compile(".*GO:(\\d{7})");
        index=new HashMap();        
        d=new Debug();
        d.setPrintLevel(0);
        this.subNode=subNode;
        buildDag(xmlFilename);         
    }

    //this is bad.
//    public GoDag(GoDag dg,int subNode)
//    {//build a new dag as a sub dag of the given dag, staring at the given node
//        goPattern=Pattern.compile(".*GO:(\\d{7})");
//        index=new HashMap();        
//        d=new Debug();
//        d.setPrintLevel(0);
//        
//        //first find this node
//        //TODO:  this will return a reference from the dg dag.  We should
//        //make a copy of it, or have find return a copy, so that we don't end
//        //up with two seperate dag objects with links between them.
//        GoNode gn=dg.find(subNode);
//        root=gn; //set new root node
//        if(gn==null)
//        {
//            index=null;
//            return;
//        }//else we have a valid node
//        
//        addToSubDag(gn,dg);
//        
//    }
    
    public GoNode find(int goNumber)
    {//return a copy of the node        
        return (GoNode)index.get(new Integer(goNumber));
    }
    public GoNode find(Integer goNum)
    {//return a copy of the node
        return find(goNum.intValue());
    }
    public static void store(String file,GoDag dag)
    {
        try{
            ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            oos.writeObject(dag);        
            oos.close();
        }catch(IOException e){
            System.out.println("could not write to file "+file+", "+e.getMessage());
        }            
    }
    public static GoDag load(String file)
    {
        try{
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file));        
            return (GoDag)ois.readObject();
        }catch(IOException e){
            System.out.println("could not read from file "+file+", "+e.getMessage());
        }catch(ClassNotFoundException e){
            System.out.println("could not find a GoDag object in "+file+": "+e.getMessage());
        }
        return null;
    }
    public String toString()
    {
        if(root==null)
            return "null root";
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
            d.print(-1,"size of tempMap: "+tempMap.size()+", size of index: "+index.size());
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
            
            //this will build a GoNode and add it to the index
            buildNode(t);                            
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
                    d.print(-1,"node "+parentGO+", parent of "+goNum+", was not found anywhere");
                    continue;
                }
                else
                    parentNode=buildNode(term);
            }
            
            //once we have the parent node of this node, we need to see if it is part of the correct
            //subtree.  If parentNode.isChildOf(subNode), then we add a link, other wise, we don't            
            GoNode subGoNode=(GoNode)index.get(new Integer(subNode));            
            if(subNode==-1 || subNode==goNum || parentNode==subGoNode || 
                (subGoNode!=null && parentNode.isChildOf(subGoNode)))
            {           
                parentNode.addChild(gn);
                d.print(2,"\t adding parent to "+goNum+": "+parentNode);
                //set parentNode as a parent of this node                    
                gn.addParent(new ParentLink(parentNode, relation, parentNode.getMaxDepth()+1));                            
            }
        }                          
        
        if(t.getPartOfOrIsA()==null || ((List)t.getPartOfOrIsA()).size()==0)//no parent links given
            root=gn; 
        
        if(gn.hasParent() || gn==root)
        {
            d.print("adding "+gn.getGoNumber()+" to index");            
            index.put(new Integer(goNum), gn);        
            for(Iterator si=t.getSynonym().iterator();si.hasNext();)
            {
                Synonym s=(Synonym)si.next();
                //test to see if this is a go number, if so, add to index
                //make sure this go is not already in index, error if it is
            }
        }
        return gn;
    }
    private void addToSubDag(GoNode gn,GoDag dg)
    {
        //to add a node, first remove all links to nodes which
        //do not have root as their parent in the dg dag
        
        for(Iterator i=gn.parents.iterator();i.hasNext();)
        {
            ParentLink pl=(ParentLink)i.next();
            if(!pl.getLink().isChildOf(root)) 
                gn.parents.remove(pl);
        }
        
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
