/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.querySets;

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
}
