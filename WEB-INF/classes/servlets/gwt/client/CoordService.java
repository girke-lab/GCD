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
	public ExperimentAreas getPolygons(int image_id);
	public byte[] getImage(int image_id) ;
	public int[] getImageInfo(String experimentSetKey);
}
