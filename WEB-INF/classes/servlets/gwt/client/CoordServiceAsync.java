/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author khoran
 */
public interface CoordServiceAsync 
{
	public abstract void getPolygons(int image_id, AsyncCallback<ExperimentAreas> asyncCallback);

	public abstract void getImage(int image_id, AsyncCallback<byte[]> asyncCallback);

	public abstract void getImageInfo(java.lang.String experimentSetKey, AsyncCallback<int[]> asyncCallback);
}
