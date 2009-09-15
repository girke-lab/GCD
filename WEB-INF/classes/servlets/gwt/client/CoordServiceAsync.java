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
	public abstract void getComparableExperiments(int experimentId, AsyncCallback<String[][]> asyncCallback);


	public abstract void doQuery(java.lang.String expSetKey, java.lang.String intensityType, java.lang.Integer comparison, java.lang.Double minControlntensity, java.lang.Double minTreatmentIntensity, java.lang.String controlPma, java.lang.String treatmentPma, java.lang.String intensityOperation, java.lang.Double maxAdjPValue, java.lang.Double lowerRatio, java.lang.Double upperRatio, AsyncCallback<Integer> asyncCallback);

	public abstract void getProbeSetKeys(java.lang.String accession, AsyncCallback<String[]> asyncCallback);

	public abstract void getIntensities(java.lang.String probeSetKey, int[] experimentIds, AsyncCallback<double[]> asyncCallback);

}
