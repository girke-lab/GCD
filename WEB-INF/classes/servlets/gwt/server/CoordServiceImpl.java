/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.postgresql.geometric.PGpolygon;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import servlets.advancedSearch.NewParametersHttpRequestWrapper;
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

	DbConnection dbc;

	public CoordServiceImpl()
	{
		if(QuerySetProvider.getImageMapQuerySet() == null)
			QuerySetProvider.setImageMapQuerySet(new V1ImageMapQuerySet());
		dbc = DbConnectionManager.getConnection("khoran");
	}

	public ExperimentAreas getPolygons(int image_id)
	{

		int[][][][] polys=null;
		int[] experimentIds=null;
		String[] descriptions=null;

		try{

			// first get some info about how many areas and components we have
			String query = QuerySetProvider.getImageMapQuerySet().getPolygonCountQuery(image_id);
			List results = dbc.sendQuery(query,false);

			int i=0,size;
			polys = new int[results.size()][][][];
			experimentIds = new int[results.size()];
			descriptions = new String[results.size()];
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

				//experimentIds[currentArea] = (int[])((Array)row.get(2)).getArray();
				experimentIds[currentArea] = (Integer)row.get(2);
				descriptions[currentArea] = (String)row.get(3);
				currentComponent++;
				prevAreaId = (Integer)row.get(0);
			}
		}catch(SQLException e){
			log.error("failed to fetch polygons: "+e,e);
		}

		return new ExperimentAreas(experimentIds,descriptions, polys);
	}

	public byte[] getImage(int image_id) 
	{

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
		try{
			List result = dbc.sendQuery(QuerySetProvider.getImageMapQuerySet().getExperimentSetImageInfo(experimentSetKey),false);
			if(result.size() > 0 )
			{
				List row = (List)result.get(0); // image_id, width, height
				return new int[] { (Integer)row.get(0), (Integer)row.get(1), (Integer)row.get(2) };
			}
		}catch(SQLException e){
			log.error(" failed to find an image for "+experimentSetKey+": "+e,e);
			throw new IllegalStateException(e);
		}
		throw new IllegalStateException("no image found for experiment set key: "+experimentSetKey);
	}


	public String[][] getComparableExperiments(int experimentId)
	{
		String[][] data =null;
		try{
			//List ids =new ArrayList(experimentIds.length);
			//for(int  value : experimentIds)
				//ids.add(value);
			List result = dbc.sendQuery(QuerySetProvider.getImageMapQuerySet().getComparisonQuery(experimentId  ));

			data =new String[result.size()][2];
			int i=0;
			for(Object o : result)
			{
				List row = (List)o;
				data[i][0] = (String)row.get(0);  // comparison
				data[i][1] = (String)row.get(1); // description
				i++;
			}
		}catch(SQLException e){
			log.error("failed to get comparable experiments: "+e,e);
		}
		return data;
	}
	public int doIntensityQuery(String expSetKey, String intensityType, int comparison,double minIntensity)
	{
		return doQuery( QuerySetProvider.getImageMapQuerySet().
					getIntensityComparisonQuery(expSetKey, intensityType, comparison, minIntensity) );
	}
	public int doRatioQuery(String expSetKey, String intensityType, int comparison, double maxPval,
			double lowerRatio, double upperRatio)
	{
		return doQuery( QuerySetProvider.getImageMapQuerySet().
					getRatioComparisonQuery(expSetKey, intensityType, comparison, maxPval, lowerRatio, upperRatio) );
	}

	public int doQuery(String expSetKey, String intensityType, Integer comparison,
			Double minControlntensity, Double minTreatmentIntensity,
			String controlPma, String treatmentPma, String intensityOperation,
			Double maxAdjPValue, Double lowerRatio, Double upperRatio)
	{
		return doQuery(QuerySetProvider.getImageMapQuerySet().
				getComparisonQuery(expSetKey, intensityType, comparison, minControlntensity, minTreatmentIntensity, 
															controlPma, treatmentPma, intensityOperation, maxAdjPValue, lowerRatio, upperRatio), intensityType);
	}
	int doQuery(String query)
	{
		return  doQuery(query,"mas5");
	}
	int doQuery(String query,String dataType)
	{

		HttpServletRequest req = this.getThreadLocalRequest();
		HttpServletResponse response = this.getThreadLocalResponse();
		return doQuery(query, dataType,req, response);
	}
	int doQuery(String query,String dataType,HttpServletRequest req, HttpServletResponse response)
	{

		Integer hid=-1;
		try{
			List result = dbc.sendQuery(query);
			if(result.size()==0)
				return -2;

			ServletRequest sr =getNewRequest(req, result, dataType);

			// TODO: need to check for errors with this request
			req.getSession().getServletContext().getRequestDispatcher("/QueryPageServlet").include(sr, response);
			HttpSession session = req.getSession(false);
			hid = (Integer)session.getAttribute("hid")-1;  // session stores the next  hid to use, but we want the one just used
			if(hid == null)
				hid = -1;

		}catch(ServletException ex) {
			ex.printStackTrace();
		}catch(IOException ex) {
			ex.printStackTrace();
		}catch(SQLException e){
			log.debug("sql error: "+e,e);
		}

		return hid;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String imageId = req.getParameter("imageId");
		String experimentSetKey = req.getParameter("experimentSetKey");

		if(imageId != null)
		{
			int image_id = Integer.parseInt(imageId);
			resp.setContentType("image/png");
			OutputStream os = resp.getOutputStream();
			os.write(getImage(image_id));
			os.close();
		}
		else if(experimentSetKey != null)
		{
			try{
				for(String name : new String[]{"comparison", "maxPValue", "lowerRatio","upperRatio","maxIntensity","intensityType","experimentSetKey" })
					log.debug(name+": " +req.getParameter(name));

				int comparison = Integer.parseInt(req.getParameter("comparison"));
				double maxPValue = Double.parseDouble(req.getParameter("maxPValue"));
				double lowerRatio = Double.parseDouble(req.getParameter("lowerRatio"));
				double upperRatio = Double.parseDouble(req.getParameter("upperRatio"));
				double maxIntensity = Double.parseDouble(req.getParameter("maxIntensity"));
				String intensityType = req.getParameter("intensityType");

				doQuery( QuerySetProvider.getImageMapQuerySet().
					getRatioComparisonQuery(experimentSetKey, intensityType, comparison, maxPValue, lowerRatio, upperRatio),
					intensityType, req, resp);
				//doQuery(req,resp, experimentSetKey,intensityType,comparison,maxPValue,lowerRatio,upperRatio,maxIntensity);

			}catch(NumberFormatException e){
				log.error(" bad entry : "+e,e);
			}
		}


	}

    protected ServletRequest getNewRequest(HttpServletRequest request,List results,String dataType)
    { //this can be overridden by sub classes to send different parameters
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");

        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", "100000");
        mRequest.getParameterMap().put("sortCol","psk_"+"affy.experiment_group_summary_view.probe_set_key");
        mRequest.getParameterMap().put("rpp","25");
        mRequest.getParameterMap().put("data_type",dataType);

        mRequest.getParameterMap().put("displayType","probeSetView");
        mRequest.getParameterMap().put("origin_page","unknownsSearch.jsp");
        mRequest.getParameterMap().put("printBorder","false");
        mRequest.getParameterMap().put("noRedirect","true");

        StringBuilder inputStr=new StringBuilder();
        List row;

        for(Iterator i=results.iterator();i.hasNext();)
        {
            row=(List)i.next();
            inputStr.append(row.get(0)+"_"+row.get(2)+" ");
        }

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        return mRequest;
    }




}
