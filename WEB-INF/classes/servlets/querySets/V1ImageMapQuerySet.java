/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.querySets;

import org.apache.log4j.Logger;

/**
 *
 * @author khoran
 */
public class V1ImageMapQuerySet  implements ImageMapQuerySet
{
    private static final Logger log=Logger.getLogger(V1ImageMapQuerySet.class);

	public String getPolygonQuery(int image_id)
	{
		return "SELECT area_id, area, experiment_id,description " +
				"FROM affy_images.image_areas_view " +
				"WHERE image_id="+image_id+
				" ORDER by description";
	}
	public String getPolygonCountQuery(int image_id)
	{
		return " SELECT area_id, count(area_id) " +
				"FROM affy_images.image_areas_view " +
				"WHERE image_id= " + image_id+
				" GROUP by area_id , description "+
				"ORDER by description";
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
	public String getComparisonQuery(int experiment_id)
	{
		return "SELECT  group_no, comparable_experiment_description "+
					"FROM  affy_images.comparable_experiments "+
					"WHERE experiment_id="+experiment_id+
					" ORDER BY comparable_experiment_description";
	}

	public String getIntensityComparisonQuery(String expSetKey, String intensityType, int comparison, double minIntensity)
	{
		String query;
		query = "SELECT DISTINCT ON (experiment_group_summary_view.comparison_id,experiment_group_summary_view.probe_set_key_id) " +
						"affy.experiment_group_summary_view.probe_set_key_id,    affy.experiment_group_summary_view.probe_set_key,   experiment_group_summary_view.comparison_id " +
				" FROM  affy.experiment_group_summary_view "+
				" WHERE " +

					"(affy.experiment_group_summary_view.control_mean > "+minIntensity+" or affy.experiment_group_summary_view.treatment_mean > "+minIntensity+") " +
					"and affy.experiment_group_summary_view.experiment_set_key = '"+expSetKey+"' " +
					"and affy.experiment_group_summary_view.comparison = "+comparison+ " "+
					"and affy.experiment_group_summary_view.data_type = '"+intensityType+"' "+
				"ORDER BY experiment_group_summary_view.comparison_id, experiment_group_summary_view.probe_set_key_id, affy.experiment_group_summary_view.probe_set_key asc " +
				" LIMIT 100000";

		query = "SELECT * FROM ("+query+") as t ORDER BY 3,2";

		return query;

	}
	public String getRatioComparisonQuery(String expSetKey, String intensityType, int comparison,
			double maxPval, double lowerRatio, double upperRatio)
	{
		String query;
		String lowerRatioValue, upperRatioValue;
		lowerRatioValue = Double.isInfinite(lowerRatio) ? "'"+lowerRatio+"'::float" : ""+lowerRatio;
		upperRatioValue = Double.isInfinite(upperRatio) ? "'"+upperRatio+"'::float" : ""+upperRatio;

		query = "SELECT DISTINCT ON (experiment_group_summary_view.comparison_id,experiment_group_summary_view.probe_set_key_id) " +
						"affy.experiment_group_summary_view.probe_set_key_id,    affy.experiment_group_summary_view.probe_set_key,   experiment_group_summary_view.comparison_id " +
				" FROM  affy.experiment_group_summary_view "+ 
				" WHERE " +
					"(affy.experiment_group_summary_view.t_c_ratio_lg < "+lowerRatioValue+" or affy.experiment_group_summary_view.t_c_ratio_lg > "+upperRatioValue+") " +
					"and affy.experiment_group_summary_view.experiment_set_key = '"+expSetKey+"' " +
					"and affy.experiment_group_summary_view.comparison = "+comparison +
					"and affy.experiment_group_summary_view.adj_p_value < " +maxPval+" "+
					"and affy.experiment_group_summary_view.data_type = '"+intensityType+"' "+
				"ORDER BY experiment_group_summary_view.comparison_id, experiment_group_summary_view.probe_set_key_id, affy.experiment_group_summary_view.probe_set_key asc " +
				" LIMIT 100000";

		query = "SELECT * FROM ("+query+") as t ORDER BY 3,2";

		//query = " SELECT 7709, '267517_at', 367";
		return query;
	}
	public String getComparisonQuery(String expSetKey, String intensityType, Integer comparison,
								Double minControllntensity, Double minTreatmentIntensity,
								String controlPma, String treatmentPma, String intensityOperation,
								Double maxAdjPValue, Double lowerRatio, Double upperRatio)
	{

		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ( ");
		query.append( "SELECT DISTINCT ON (experiment_group_summary_view.comparison_id,experiment_group_summary_view.probe_set_key_id) " +
						"affy.experiment_group_summary_view.probe_set_key_id,    affy.experiment_group_summary_view.probe_set_key,   experiment_group_summary_view.comparison_id " +
				" FROM  affy.experiment_group_summary_view "+
				" WHERE " +
					" affy.experiment_group_summary_view.experiment_set_key = '"+expSetKey+"' " +
					" and affy.experiment_group_summary_view.comparison = "+comparison +
					" and affy.experiment_group_summary_view.data_type = '"+intensityType+"' "
		);
		if(minControllntensity != null)
		{
			query.append(" and ( affy.experiment_group_summary_view.control_mean > "+minControllntensity);
			if( minTreatmentIntensity != null)
				query.append(intensityOperation+" affy.experiment_group_summary_view.treatment_mean > "+minTreatmentIntensity);
			query.append(")");
		}
		else if(minTreatmentIntensity != null)  // minControlIntensity must be null if we get here
			query.append(" and  affy.experiment_group_summary_view.treatment_control < "+minTreatmentIntensity);

		if(controlPma != null)
		{
			query.append(" and ( affy.experiment_group_summary_view.control_pma  ~ "+pmaRe(controlPma));
			if( treatmentPma != null)
				query.append(intensityOperation+" affy.experiment_group_summary_view.treatment_pma ~ "+pmaRe(treatmentPma));
			query.append(")");
		}
		else if(treatmentPma != null)  // controlPma must be null if we get here
			query.append(" and  affy.experiment_group_summary_view.treatment_pma  ~ "+pmaRe(treatmentPma));

		if(maxAdjPValue != null)
			query.append(" and affy.experiment_group_summary_view.adj_p_value < "+maxAdjPValue);

		if(lowerRatio != null)
		{
			query.append(" and ( affy.experiment_group_summary_view.t_c_ratio_lg < "+lowerRatio);
			if(upperRatio != null)
				query.append(" or affy.experiment_group_summary_view.t_c_ratio_lg > "+upperRatio);
			query.append(")");
		}
		else if(upperRatio != null)
				query.append(" and  affy.experiment_group_summary_view.t_c_ratio_lg > "+upperRatio);

		query.append(" ORDER BY experiment_group_summary_view.comparison_id, experiment_group_summary_view.probe_set_key_id, affy.experiment_group_summary_view.probe_set_key asc " +
				" LIMIT 100000");

		query.append( ") as t ORDER BY 3,2");
		return query.toString();
	}
	private final String pmaRe(String pma)
	{
		return "'^"+pma+"+$'";
	}
	/*
	private String binarySubExpression(Object v1, Object v2, String operation)
	{
		StringBuilder query=new StringBuilder();
		if(v1 != null)
		{
			query.append(" and ( affy.experiment_group_summary_view.control_mean < "+minControllntensity);
			if( minTreatmentIntensity != null)
				query.append(intensityOperation+" affy.experiment_group_summary_view.treatment_mean < "+minTreatmentIntensity);
			query.append(")");
		}
		else if(minTreatmentIntensity != null)  // minControlIntensity must be null if we get here
			query.append(" and  affy.experiment_group_summary_view.treatment_control < "+minTreatmentIntensity);
	
		return query.toString();
	}
	 */
}
