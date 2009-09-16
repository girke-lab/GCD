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
	public int[][] getImageInfo(String experimentSetKey);
	public String[][] getComparableExperiments(int experimentId);

	public int doQuery(String expSetKey, String intensityType, Integer comparison, 
							Double minControlntensity, Double minTreatmentIntensity,
							String controlPma, String treatmentPma, String intensityOperation,
							Double maxAdjPValue,  Double lowerRatio, Double upperRatio);
	public String[] getProbeSetKeys(String accession);
	public double[] getIntensities(String probeSetKey, int[] experimentIds);
}
