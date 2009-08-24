/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;


/**
 *
 * @author khoran
 */
public class PolygonUtils 
{

	/**
	 *  Authors: Bob Stein  Craig Yap
	 *  code taken from web site: http://www.visibone.com/inpoly/
	 * ported to java by Kevin Horan
	 *
		int[][] poly,              polygon points, [0]=x, [1]=y       
		int npoints,             number of points in polygon 
		int xt,                     x (horizontal) of target point    
		int yt                      y (vertical) of target point    
	 */
	public static boolean   inpoly(  int[][] poly,  int xt,  int yt)               
	{
		int xnew,ynew;
		int xold,yold;
		int x1,y1;
		int x2,y2;
		int i;
		int npoints = poly.length;
		boolean inside =false;

		if (npoints < 3) {
			return false;
		}
		xold=poly[npoints-1][0];
		yold=poly[npoints-1][1];
		for (i=0 ; i < npoints ; i++) {
			xnew=poly[i][0];
			ynew=poly[i][1];
			if (xnew > xold) {
				x1=xold;
				x2=xnew;
				y1=yold;
				y2=ynew;
			}
			else {
				x1=xnew;
				x2=xold;
				y1=ynew;
				y2=yold;
			}
			if ((xnew < xt) == (xt <= xold)          /* edge "open" at one end */
			&& ((long)yt-(long)y1)*(long)(x2-x1)
				< ((long)y2-(long)y1)*(long)(xt-x1)) {
				inside=!inside;
			}
			xold=xnew;
			yold=ynew;
		}
		return inside;
	}
}
