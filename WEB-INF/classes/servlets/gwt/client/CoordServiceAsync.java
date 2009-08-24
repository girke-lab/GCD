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
	public abstract void getPolygons(AsyncCallback<int[][][][]> asyncCallback);
}
