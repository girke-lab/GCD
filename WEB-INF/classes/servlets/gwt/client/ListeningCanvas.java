/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

/**
 *
 * @author khoran
 */
public class ListeningCanvas  extends  GWTCanvas implements HasMouseUpHandlers, HasMouseOverHandlers, HasMouseMoveHandlers
{
	public ListeningCanvas() {
		super();
	}

	public ListeningCanvas(int coordX, int coordY) {
		super(coordX, coordY);
	}

	public ListeningCanvas(int coordX, int coordY, int pixelX, int pixelY) {
		super(coordX, coordY, pixelX, pixelY);
	}


	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return addDomHandler(handler, MouseUpEvent.getType() );
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

}
