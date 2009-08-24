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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.gwt.client.CoordService;

/**
 *
 * @author khoran
 */
public class CoordServiceImpl extends RemoteServiceServlet implements CoordService
{
    private static final Logger log=Logger.getLogger(CoordServiceImpl.class);

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
