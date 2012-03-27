/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

/**
 *
 * @author khoran
 */
public class PaintableImage extends Composite implements HasMouseUpHandlers, HasMouseMoveHandlers, HasMouseOutHandlers
{
	GWTCanvas background;
	GWTCanvas imageCanvas;

	public PaintableImage(ImageElement image)
	{
		//Log.debug("width: "+image.getWidth()+", height: "+image.getHeight());
		String width = ""+image.getWidth();
		String height = ""+image.getHeight();

		AbsolutePanel p = new AbsolutePanel();
		p.setSize(width,height);

		background = new GWTCanvas(image.getWidth(),image.getHeight());
		imageCanvas = new GWTCanvas(image.getWidth(),image.getHeight());

		p.add(background,0,0);
		p.add(imageCanvas,0,0);

		initWidget(p);

		background.drawImage(image, 0, 0);
	}

	public GWTCanvas getCanvas()
	{
		return imageCanvas;
	}


	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return addDomHandler(handler, MouseUpEvent.getType() );
	}
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return addDomHandler(handler, MouseMoveEvent.getType());
	}
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return addDomHandler(handler,MouseOutEvent.getType());
	}



}
