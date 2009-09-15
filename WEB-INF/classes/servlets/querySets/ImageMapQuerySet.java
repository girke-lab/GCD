/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.querySets;

import java.util.Collection;

/**
 *
 * @author khoran
 */
public interface ImageMapQuerySet  extends QuerySet
{

	public String getPolygonQuery(int image_id);
	public String getPolygonCountQuery(int image_id);
	public String getExperimentSetImageInfo(String key);
	public String getImage(int image_id);
	public String getComparisonQuery(int experiment_id);
	public String getRatioComparisonQuery(String expSetKey,
			String intensityType, int comparison, double maxPval,
			double lowerRatio, double upperRatio);
	public String getIntensityComparisonQuery(String expSetKey,
			String intensityType, int comparison, double minIntensity);

	public String getComparisonQuery(String expSetKey, String intensityType, Integer comparison,
			Double minControlntensity, Double minTreatmentIntensity,
			String controlPma, String treatmentPma, String intensityOperation,
			Double maxAdjPValue, Double lowerRatio, Double upperRatio);
	public String getProbeSetKeyQuery(String accession);
	public String getIntensityesByProbeSetKeyQuery(String probeSetKey,  Collection exerimentIds);
}
