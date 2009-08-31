/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.querySets;

import java.util.Collection;
import org.apache.log4j.Logger;
import servlets.Common;

/**
 *
 * @author khoran
 */
public class V1ImageMapQuerySet  implements ImageMapQuerySet
{
    private static final Logger log=Logger.getLogger(V1ImageMapQuerySet.class);

	public String getPolygonQuery(int image_id)
	{
		return "SELECT area_id, area, experiment_ids " +
				"FROM affy_images.image_areas_view " +
				"WHERE image_id="+image_id+
				" ORDER by area_id";
	}
	public String getPolygonCountQuery(int image_id)
	{
		return " SELECT area_id, count(area_id) " +
				"FROM affy_images.image_areas_view " +
				"WHERE image_id= " + image_id+
				" GROUP by area_id "+
				"ORDER by area_id";
	}

	public String getExperimentSetImageInfo(String key)
	{
		return "SELECT image_id, width,height " +
				"FROM affy_images.experiment_image_view " +
				"WHERE experiment_set_key='"+key+"'";
	}

	public String getImage(int image_id)
	{
		return "SELECT image FROM affy_images.image_view " +
				"WHERE image_id="+image_id;
	}
	public String getComparisonQuery(Collection expIds)
	{
		return "SELECT  group_no, description "+
					"FROM  affy_images.comparable_experiments "+
					"WHERE "+Common.buildIdListCondition("experiment_id", expIds);
	}
	public String getProbeSetComparisonQuery(String expSetKey, String intensityType, int comparison, double maxPval, double lowerRatio, double upperRatio,double maxIntensity)
	{
		String query;

		query = "SELECT DISTINCT ON (experiment_group_summary_view.comparison_id,experiment_group_summary_view.probe_set_key_id) " +
						"affy.experiment_group_summary_view.probe_set_key_id,    affy.experiment_group_summary_view.probe_set_key,   experiment_group_summary_view.comparison_id " +
				" FROM affy.experiment_set_summary_view,affy.experiment_group_summary_view " +
				" WHERE affy.experiment_group_summary_view.probe_set_key_id = affy.experiment_set_summary_view.probe_set_key_id " +
					"and (affy.experiment_group_summary_view.t_c_ratio_lg < "+lowerRatio+" or affy.experiment_group_summary_view.t_c_ratio_lg > "+upperRatio+") " +
					"and affy.experiment_group_summary_view.experiment_set_key = '"+expSetKey+"' " +
					"and affy.experiment_group_summary_view.comparison = "+comparison+" and " +
					"affy.experiment_group_summary_view.adj_p_value < " +maxPval+" "+
					"and affy.experiment_group_summary_view.data_type = '"+intensityType+"' "+
					"and affy.experiment_set_summary_view.average <  " +maxIntensity+" "+
				"ORDER BY experiment_group_summary_view.comparison_id, experiment_group_summary_view.probe_set_key_id, affy.experiment_group_summary_view.probe_set_key asc " +
				" LIMIT 100000";

		query = " SELECT 7709, '267517_at', 367";
		return query;
	}
}
