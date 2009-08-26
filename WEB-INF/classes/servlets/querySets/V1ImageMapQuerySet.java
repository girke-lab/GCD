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
		return "SELECT area_id, area, experiment_id " +
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
}
