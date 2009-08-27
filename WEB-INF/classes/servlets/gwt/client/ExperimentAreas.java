/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author khoran
 */
public class ExperimentAreas implements IsSerializable
{

	public int[][][][] polys;
	public int[][] experimentIds;

	public ExperimentAreas()
	{
		polys=null;
		experimentIds=null;
	}
	public ExperimentAreas(int[][] experimentIds, int[][][][] polys)
	{
		this.polys=polys;
		this.experimentIds=experimentIds;
	}


}
