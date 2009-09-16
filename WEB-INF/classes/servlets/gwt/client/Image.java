/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

/**
 *
 * @author khoran
 */
public class Image extends Composite
{
	GWTCanvas background;
	ListeningCanvas imageCanvas;
	public Image(ImageElement image)
	{
		AbsolutePanel p = new AbsolutePanel();
		p.add(background,0,0);
		p.add(imageCanvas,0,0);

		initWidget(p);

		background.drawImage(image, 0, 0);
	}

	public GWTCanvas getCanvas()
	{
		return imageCanvas;
	}
}
