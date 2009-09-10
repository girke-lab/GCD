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
	public String[][] getComparableExperiments(int experimentId);
	public int doRatioQuery(String expSetKey, String intensityType, int comparison, double maxPval,
			double lowerRatio, double upperRatio);
	public int doIntensityQuery(String expSetKey, String intensityType, int comparison, double minIntensity);
	public int doQuery(String expSetKey, String intensityType, Integer comparison, 
							Double minControlntensity, Double minTreatmentIntensity,
							String controlPma, String treatmentPma, String intensityOperation,
							Double maxAdjPValue,  Double lowerRatio, Double upperRatio);
}
