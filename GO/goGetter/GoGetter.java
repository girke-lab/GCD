/*
 * SelectGo.java
 *
 * Created on January 28, 2004, 2:32 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.sql.*;
import java.io.*;
import khoran.debugPrint.Debug; 

public class GoGetter 
{
    Connection con;
    Debug d;
    
    
    /** Creates a new instance of SelectGo */
    public GoGetter() 
    {
        connect("common_loading");
        d=new Debug();
        d.setPrintLevel(0);        
    }
    
    /**
     *input: if -x is specified, a xml file should be given, if -d is specified, a serielized GoDag file
     *should be given.  The rest of the input needed is read from the mySql database
     *output: output is written directly to the mySql database.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
                        
        // khoran.debugPrint.Debug.setPrintStatus(false);  //turn debug printing on or off
        
        /////////////////  process input  ////////////////////////////////////        
        if(args.length!=2)
        {
            System.out.println("USAGE: GoGetter {-x go_xml_file} | {-d GoDag_serial_file}");
            return;
        }
        GoDag dag=null;
        if(args[0].equals("-x"))
        {
            dag=new GoDag(args[1],3674);  //molecular function is GO:0003674                 
            GoDag.store(args[1]+".goDag",dag);            
        }
        else if(args[0].equals("-d"))
            dag=GoDag.load(args[1]);
        else
        {
            System.out.println("invalid switch: "+args[0]);
            System.exit(0);
        }
        
        if(dag==null)
        {
            System.out.println("error: could not build dag");
            System.exit(0);
        }
        ///////////////////////////////////////////////////////////////////////////////////        
//        System.out.println("Dag:\n"+dag);
        
        GoGetter work=new GoGetter();
         
        System.out.println("setting unique go numbers for each key");
        work.findUniqueGoNumbers(dag);        
        
        System.out.println("setting cluster names");
        work.findClusterNmaes(dag);
        
        System.out.println("done");
        
    }
    public void findUniqueGoNumbers(GoDag dag)
    {//assign a unique go number to each At number
        
        GoSelector selector=new Selector1(dag); //assign an instance of a GoSelector
        
        //input structure should be a list of GoGroups
        ArrayList inputData=getInput("SELECT Seq_id,Go from Go order by Seq_id",
                                     "SELECT count(Seq_id) from Go group by Seq_id order by seq_id");        
        for(Iterator i=inputData.iterator();i.hasNext();)
        {
            GoGroup at=((GoGroup)i.next());
            at.selectedGO=selector.getGoNumber(at.goNums);
            GoNode gn=dag.find(at.selectedGO);
            if(gn!=null)
                at.text=gn.getText();       
            else
                System.out.println("uniqueNumbers: go "+at.selectedGO+" was not updated properly");
        }
        
        storeOutput(inputData); //write data back to db or file or whatever
        
    }
    public void findClusterNmaes(GoDag dag)
    {//assign a name to each cluster
        //1) read in sets of go numbers for each cluster.
        // 2) pick most common go number
        // 3) grab its text and write to the database, in the Cluster_Info table.

        GoSelector selector=new MostCommonSelector();
        ArrayList inputData=getInput("SELECT Cluster_id, Go_Number FROM Clusters "+
                                       "LEFT JOIN Sequences USING(Seq_id) WHERE "+
                                       "Go_Number IS NOT NULL ORDER BY Cluster_id",
                                     "SELECT count(Cluster_id) FROM Clusters "+
                                       "LEFT JOIN Sequences USING(Seq_id) WHERE "+
                                       "Go_Number IS NOT NULL GROUP BY Cluster_id ORDER BY Cluster_id");//return an array of GoGroups
        for(Iterator i=inputData.iterator();i.hasNext();)
        {
            GoGroup gg=(GoGroup)i.next();
            gg.selectedGO=selector.getGoNumber(gg.goNums);
            GoNode gn=dag.find(gg.selectedGO);
            if(gn!=null)
                gg.text=gn.getText();
            else
                System.out.println("clusterNames: go "+gg.selectedGO+" was not updated properly");
        }
        
        storeClusterNames(inputData);
        
    }
    public ArrayList getInput(String dataQuery, String countQuery)
    {
        /*dataQuery should return an id column, and a go number column.
         *           countQuery should return one column which contains the size of each
         *           group of id numbers in the dataQuery.
         */
        Statement stmt1,stmt2;
        ResultSet dataRS,countsRS;
        ArrayList ats=new ArrayList();
        
        try{
            stmt1=con.createStatement();
            stmt2=con.createStatement();

            dataRS=stmt1.executeQuery(dataQuery);
            countsRS=stmt2.executeQuery(countQuery); 

            while(countsRS.next()) //there will be one count entry for each group of go numbers
            {//create GoGroups
                int size=countsRS.getInt(1);//get size of this group
                d.print("size="+size);
                int id=-1;
                int[] gos=new int[size]; 

                //read in goups of seq_id numbers and store all go the numbers in one at object            
                for(int i=0;i<size && dataRS.next();i++)            
                {                    
                    id=dataRS.getInt(1);                    
                    gos[i]=Integer.parseInt(dataRS.getString(2).substring(3)); //chop off GO:
                    d.print("found set: id="+id+", go="+gos[i]);
                }

                GoGroup a=new GoGroup(id, gos);
                ats.add(a);            
            }
            stmt1.close();
            stmt2.close();
        }catch(SQLException e){
            System.out.println("sql error: "+e.getMessage());
        }
        return ats;
    }
    public void storeOutput(ArrayList data)
    {

        try{
            con.setAutoCommit(false);
            Statement stmt1=con.createStatement();        
            GoGroup gg;

            for(Iterator i=data.iterator();i.hasNext();)
            {
                gg=(GoGroup)i.next();                        
                String goNum=buildGo(gg.selectedGO);
                stmt1.executeUpdate("UPDATE Sequences SET Go_Number='"+goNum+"' WHERE Seq_id="+gg.SeqId);
            }
            con.commit();
            con.setAutoCommit(true);        
            stmt1.close();
        }catch(SQLException e){
            System.out.println("sql error: "+e.getMessage());
        }
        
    }    
    public void storeClusterNames(ArrayList data)
    {
        GoGroup gg=null;
        try{
            con.setAutoCommit(false);
            Statement stmt1=con.createStatement();        

            for(Iterator i=data.iterator();i.hasNext();)
            {
                gg=(GoGroup)i.next();                       
                //gg.text=gg.text.replaceAll("\'", "\\\'");
                String escaped="";
                for(int j=0;j<gg.text.length();j++)
                {
                    if(gg.text.charAt(j)=='\'')
                        escaped+="\\'";
                    else
                        escaped+=gg.text.charAt(j);
                }
                gg.text=escaped;
                stmt1.executeUpdate("UPDATE Cluster_Info SET Name='"+gg.text+"' WHERE Cluster_id="+gg.SeqId);
            }
            con.commit();
            con.setAutoCommit(true);        
            stmt1.close();
        }catch(SQLException e){
            System.out.println("sql error: "+e.getMessage());
            if(gg!=null)
                System.out.println("text="+gg.text+", seq_id="+gg.SeqId);
        }
        
    }
///////////////////////////////////////////
    private String buildGo(int go)
    {//add GO: to front of number and padd to 7 digits.        
        String number=Integer.toString(go);
        int padding=7-number.length();
        for(int i=0;i<padding;i++)
            number="0"+number; //put a zero in front of it
        return "GO:"+number;                
    }
    private void connect(String DB)
    {        
        //open a connnection with the database server
        //String url="jdbc:mysql://138.23.191.152/"+DB+"?autoReconnect=false"; //was true
        String url="jdbc:postgresql://138.23.191.152/"+DB;
        try{
            //Class.forName("org.gjt.mm.mysql.Driver").newInstance();        
            Class.forName("org.postgresql.Driver").newInstance();
            con=DriverManager.getConnection(url,"updater","");
        }catch(SQLException e){
            System.out.println("connection error:"+e.getMessage());
            con=null;       
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class GoGroup
{
    public int SeqId;
    public int[] goNums;
    public int selectedGO;
    public String text;
    
    public GoGroup(int id,int[] g)
    {
        SeqId=id;
        goNums=g;
        text="";
        selectedGO=-1;
    }
    public String toString()
    {
        StringBuffer out=new StringBuffer();
        out.append(SeqId);
        
//        out.append(" goNums=");
//        for(int i=0;i<goNums.length;i++)
//            out.append(goNums[i]+",");
        
        out.append(" seleced "+selectedGO+":\""+text+"\"\n");
        return out.toString();        
    }
}
