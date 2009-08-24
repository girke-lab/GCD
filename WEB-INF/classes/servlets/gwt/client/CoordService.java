/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 *
 * @author khoran
 */
public interface CoordService extends RemoteService
{
	public int[][][][] getPolygons();
}
