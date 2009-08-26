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
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.postgresql.geometric.PGpolygon;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import servlets.gwt.client.CoordService;
import servlets.gwt.client.ExperimentAreas;
import servlets.querySets.QuerySetProvider;
import servlets.querySets.V1ImageMapQuerySet;

/**
 *
 * @author khoran
 */
public class CoordServiceImpl extends RemoteServiceServlet implements CoordService
{
    private static final Logger log=Logger.getLogger(CoordServiceImpl.class);

	public CoordServiceImpl()
	{
		if(QuerySetProvider.getImageMapQuerySet() == null)
			QuerySetProvider.setImageMapQuerySet(new V1ImageMapQuerySet());
	}

	//TODO: need to include experiment_id  for each area
	public ExperimentAreas getPolygons(int image_id)
	{

		int[][][][] polys=null;
		int[] experimentIds=null;
		DbConnection dbc = DbConnectionManager.getConnection("khoran");

		try{

			// first get some info about how many areas and components we have
			String query = QuerySetProvider.getImageMapQuerySet().getPolygonCountQuery(image_id);
			List results = dbc.sendQuery(query,false);

			int i=0,size;
			polys = new int[results.size()][][][];
			experimentIds = new int[results.size()];
			for(Object o : results)  //each row has area_id,  number of components
			{
				List row = (List)o;
				size = ((Number)row.get(1)).intValue();
				//log.debug("size: "+size);
				polys[i++]=new int[size][][];
			}

			// then fetch and parse the actual areas
			query = QuerySetProvider.getImageMapQuerySet().getPolygonQuery(image_id);
			results = dbc.sendQuery(query,false);

			PGpolygon pgPoly;
			Integer prevAreaId=null;
			int currentArea=0,currentComponent=0;

			for(Object o : results)  //each row has area_id, polygon, experiment_id
			{
				List row = (List)o;

				if(prevAreaId != null && ! prevAreaId.equals((Integer)row.get(0)))
				{
					experimentIds[currentArea] = (Integer)row.get(2);
					currentArea++;
					currentComponent=0;
				}

				pgPoly = (PGpolygon)row.get(1);
				//log.debug(row.get(0)+"  ca: "+currentArea+",  cc: "+currentComponent);
				polys[currentArea][currentComponent] = new int[pgPoly.points.length][2];
				for(int j=0; j < pgPoly.points.length; j++)
				{
					polys[currentArea][currentComponent][j][0] = (int) pgPoly.points[j].x;
					polys[currentArea][currentComponent][j][1] = (int) pgPoly.points[j].y;
				}

				currentComponent++;
				prevAreaId = (Integer)row.get(0);
			}
		}catch(SQLException e){
			log.error("failed to fetch polygons: "+e,e);
		}


		return new ExperimentAreas(experimentIds, polys);
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

	public byte[] getImage(int image_id) 
	{
		DbConnection dbc = DbConnectionManager.getConnection("khoran");

		try{
			List result = dbc.sendQuery(QuerySetProvider.getImageMapQuerySet().getImage(image_id),false);
			if(result.size() > 0 &&  ((List)result.get(0)).size() > 0)
				return (byte[]) ((List)((List)result).get(0) ).get(0);
		}catch(SQLException e){
			log.error("failed to fetch image "+image_id+": "+e,e);
			throw new IllegalStateException(e);
		}
		throw new IllegalStateException("no image found for id: "+image_id);
	}

	public int[] getImageInfo(String experimentSetKey)
	{
		DbConnection dbc = DbConnectionManager.getConnection("khoran");
		try{
			List result = dbc.sendQuery(QuerySetProvider.getImageMapQuerySet().getExperimentSetImageInfo(experimentSetKey),false);
			if(result.size() > 0 )
			{
				List row = (List)result.get(0);
				return new int[] { (Integer)row.get(0), (Integer)row.get(1), (Integer)row.get(2) };
			}
		}catch(SQLException e){
			log.error(" failed to find an image for "+experimentSetKey+": "+e,e);
			throw new IllegalStateException(e);
		}
		throw new IllegalStateException("no image found for experiment set key: "+experimentSetKey);
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String imageId = req.getParameter("imageId");

		if(imageId != null)
		{
			int image_id = Integer.parseInt(imageId);
			resp.setContentType("image/png");
			OutputStream os = resp.getOutputStream();
			os.write(getImage(image_id));
			os.close();
		}


	}

}
