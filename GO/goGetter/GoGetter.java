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
        connect("common");
        d=new Debug();
        d.setPrintLevel(0);        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
       /* input: xmlfile to build the DAG with, and a list of keys to select Go numbers for.
                 *OR, we could just connect directly to the db and read the key and go numbers directly
                 *
                 *output: a list of keys associated with just one GO number. OR, store the go number in another
                 *table.
                 *
                 *We take in a list of At numbers, each of which has a list of GO numbers associated with it.  
                 *For each At number, we use the GoSelector to select on of these GO numbers.
                 */
        if(args.length!=2)
        {
            System.out.println("USAGE: GoGetter {-x go xml file} | {-d GoDag serial file}");
            return;
        }
        //khoran.debugPrint.Debug.setPrintStatus(false);  //turn debug printing on or off
        GoDag dag=null;
        if(args[0].equals("-x"))
        {
            dag=new GoDag(args[1]);
            System.out.print("writing dag...");
                        
            try{
                ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(args[1]+".goDag")));
                oos.writeObject(dag);        
                oos.close();
            }catch(IOException e){
                System.out.println("error writing dag: "+e.getMessage());
            }catch(Exception e){
                e.printStackTrace();
            }
            
            System.out.println("done writing dag.");
        }
        else if(args[0].equals("-d"))
        {               
            try{
                ObjectInputStream ois=new ObjectInputStream(new FileInputStream(args[1]));        
                dag=(GoDag)ois.readObject();
            }catch(IOException e){
                System.out.println("error loading dag: "+e.getMessage());
            }catch(ClassNotFoundException e){
                System.out.println("could not find a GoDag object in "+args[1]+": "+e.getMessage());
            }
        }
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
        
        //System.out.println("Dag:\n"+dag);
        
        
        GoSelector selector=new Selector1(dag); //assign an instance of a GoSelector
        GoGetter work=new GoGetter();
        
        //input structure should be a list of AtNumbers.
        ArrayList inputData=work.getInput();        
        for(Iterator i=inputData.iterator();i.hasNext();)
        {
            AtNumber at=((AtNumber)i.next());
            at.selectedGO=selector.getGoNumber(at.goNums);
            at.text=dag.find(at.selectedGO).getText();            
        }
        
        //work.storeOutput(inputData); //write data back to db or file or whatever
         
    }
    public ArrayList getInput()
    {
        Statement stmt1,stmt2;
        ResultSet dataRS,countsRS;
        ArrayList ats=new ArrayList();
        
        try{
            stmt1=con.createStatement();
            stmt2=con.createStatement();

            //get seq_id, go number from Go table, order by Seq_id
            dataRS=stmt1.executeQuery("SELECT Seq_id,Go from Go order by Seq_id");
            //get the size of each group of go numbers
            countsRS=stmt2.executeQuery("SELECT count(Seq_id) from Go group by Seq_id"); 


            while(countsRS.next()) //there will be one count entry for each group of go numbers
            {//create AtNumbers
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

                AtNumber a=new AtNumber(id, gos);
                ats.add(a);            
            }
        }catch(SQLException e){
            System.out.println("sql error: "+e.getMessage());
        }
        return ats;
    }
    public void storeOutput(ArrayList data)
    {
        System.out.println("output: "+data);
    }
///////////////////////////////////////////
    
    private void connect(String DB)
    {        
        //open a connnection with the database server
        String url="jdbc:mysql://138.23.191.152/"+DB+"?autoReconnect=false"; //was true
        try{
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();        
            con=DriverManager.getConnection(url,"servlet","512256");
        }catch(SQLException e){
            System.out.println("connection error:"+e.getMessage());
            con=null;       
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class AtNumber
{
    public int atId;
    public int[] goNums;
    public int selectedGO;
    public String text;
    
    public AtNumber(int id,int[] g)
    {
        atId=id;
        goNums=g;
    }
    public String toString()
    {
        StringBuffer out=new StringBuffer();
        out.append(atId);
        
//        out.append(" goNums=");
//        for(int i=0;i<goNums.length;i++)
//            out.append(goNums[i]+",");
        
        out.append(" seleced "+selectedGO+":\""+text+"\"\n");
        return out.toString();        
    }
}