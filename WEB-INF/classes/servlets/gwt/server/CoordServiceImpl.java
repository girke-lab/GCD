/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import servlets.gwt.client.CoordService;
import servlets.querySets.QuerySetProvider;
import servlets.querySets.V1ImageMapQuerySet;

/**
 *
 * @author khoran
 */
public class CoordServiceImpl extends RemoteServiceServlet implements CoordService
{
    private static final Logger log=Logger.getLogger(CoordServiceImpl.class);

	CoordServiceImpl()
	{
		if(QuerySetProvider.getImageMapQuerySet() == null)
			QuerySetProvider.setImageMapQuerySet(new V1ImageMapQuerySet());
	}

	public int[][][][] getPolygons(int image_id)
	{
		int[][][][] polys=null;
		DbConnection dbc = DbConnectionManager.getConnection("khoran");

		try{

			// first get some info about how many areas and components we have
			String query = QuerySetProvider.getImageMapQuerySet().getPolygonCountQuery(image_id);
			List results = dbc.sendQuery(query,false);

			int i=0,size;
			polys = new int[results.size()][][][];
			for(Object o : results)  //each row has experiment_id,  number of components
			{
				List row = (List)o;
				//size = Integer.parseInt((String)row.get(2));
				size = (Integer)row.get(2);
				polys[i++]=new int[size][][];
			}

			// then fetch and parse the actual areas
			query = QuerySetProvider.getImageMapQuerySet().getPolygonQuery(image_id);
			results = dbc.sendQuery(query);

			String prevExpId=null;
			String[] tokens,coord;
			int currentArea=0,currentComponent=0,x,y;

			for(Object o : results)  //each row has experiment_id, and a polygon
			{
				List row = (List)o;
				tokens = stripOuterParinths((String)row.get(1)).split(",");
				polys[currentArea][currentComponent] = new int[tokens.length][2];

				for(int j=0; j < tokens.length; j++)
				{
					coord = stripOuterParinths(tokens[j]).split(",");
					x =  Integer.parseInt(coord[0]);
					y = Integer.parseInt(coord[1]);
					polys[currentArea][currentComponent][j][0]=x;
					polys[currentArea][currentComponent][j][1]=y;

				}

				if(prevExpId != null || ! prevExpId.equals((String)row.get(0)))
				{
					currentArea++;
					currentComponent=0;
				}
				currentComponent++;
				prevExpId = (String)row.get(0);
			}
		}catch(SQLException e){
			log.error("failed to fetch polygons: "+e,e);
		}

		return polys;
	}
	final static String stripOuterParinths(String s)
	{
		int l = s.length();
		if( l  > 1 && s.charAt(0)=='(')
		{
			s=s.substring(1);
			l--;
		}
		if( l > 1 && s.charAt(l-1)==')')
			s=s.substring(0, l-1);
		return s;
	}
	public int[][][][] getPolygons()
	{

		InputStream coordStream = CoordServiceImpl.class.getResourceAsStream("mapCoords");

		BufferedReader br =null;
		int[][][][] polys=null;
		try{
			br =new BufferedReader(new InputStreamReader(coordStream));
			String line;
			String[] polygonDefs, coordDefs;
			List<String> lines=new LinkedList<String>();
			while( (line = br.readLine()) != null)
				lines.add(line);
			polys=new int[lines.size()][][][];
			//log.debug("num lines: "+lines.size());

			int c=0;
			for( String l : lines)
			{
				polygonDefs = l.split("\\s+");
				polys[c] = new int[polygonDefs.length-1][][];
				//log.debug("num polys: "+polygonDefs.length);

				// first element is id number, ignored for now
				for(int i=0; i < polygonDefs.length-1; i++)
				{
					coordDefs = polygonDefs[i+1].split(",");
					//log.debug("c="+c+", i="+i);
					polys[c][i]= new int[coordDefs.length/2][2];
					for(int j=0; j < coordDefs.length/2; j++)
					{
						polys[c][i][j][0] = Integer.parseInt(coordDefs[j*2]);
						polys[c][i][j][1] = Integer.parseInt(coordDefs[j*2+1]);
					}
				}
				c++;
			}
		}catch(IOException e){
			log.error("failed to read map coords: "+e,e);
		}finally{
			if(br!= null)
				try{br.close();}catch(IOException e){}
		}

		//if(log.isDebugEnabled())
			//log.debug(Arrays.deepToString(polys));
		return polys;
	}

}
