/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 *
 * @author khoran
 */
public class CySamEntryPoint  implements  EntryPoint, MouseMoveHandler, MouseUpHandler,MouseOverHandler
{


	final Label status = new Label("init");
	final PopupPanel popupTest = new PopupPanel(true);
	final Panel selectionPanel = new VerticalPanel();
	final ListeningCanvas canvas = new ListeningCanvas(400,400);
	final ListeningCanvas backgroundCanvas = new ListeningCanvas(400,400);

	boolean outlinePolys =false;

	ImageElement[] backgroundImage= new ImageElement[1];

	//final int testPolys[][][]= new int[][][] {
		//{ {1,1} , { 1,50}, {10,60}, {50,30},{30,10} },
		//{ {100,100},{100,200},{200,200} }
	//};

	//boolean[] visiblePolys;
	int litPoly=-1;
	int[][][][] polygons=null;

	/** Creates a new instance of MainEntryPoint */
	public CySamEntryPoint()
	{
	}

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */
	public void onModuleLoad()
	{


		canvas.addMouseMoveHandler(this);
		canvas.addMouseOverHandler(this);
		canvas.addMouseUpHandler(this);



		//String[] urls = new String[] {"http://localhost:8080/gwtTest/clan-vs-full-family-2500.png"};
		String[] urls = new String[] {"/databaseWeb/images/Ram_SAM.png"};

		ImageLoader.loadImages(urls, new ImageLoader.CallBack() {
			public void onImagesLoaded(ImageElement[] imageElements) {
				status.setText("images loaded");

				backgroundCanvas.drawImage(imageElements[0], 0, 0,284,98);
				redraw(canvas);
			}
		});

		selectionPanel.add(new Label("select options"));
		selectionPanel.add(new CheckBox("option 1"));
		selectionPanel.add(new CheckBox("option 2"));
		selectionPanel.add(new CheckBox("option 3"));
		selectionPanel.add(new Button("submit"));

		selectionPanel.setStylePrimaryName("selectionPanel");


		popupTest.setWidget(selectionPanel);


		RootPanel.get("CySam").add(status);
		RootPanel.get("CySam").add(new Label(" space "));
		RootPanel.get("CySam").add(new Label(" space "));
		RootPanel.get("CySam").add(new Label(" space "));
		RootPanel.get("CySam").add(new Label(" space "));
		RootPanel.get("CySam").add(new Label(" space "));

		AbsolutePanel canvasPanel = new AbsolutePanel();
		canvasPanel.add(backgroundCanvas,0,0);
		canvasPanel.add(canvas,0,0);
		canvasPanel.setSize("400", "400");
		RootPanel.get("CySam").add(canvasPanel);

	}

	public void onMouseMove(MouseMoveEvent event)
	{
		int x,y;
		x=event.getX();
		y=event.getY();
		status.setText("position: "+x+", "+y);
		boolean needRedraw=false;

		if(getPolygons() == null)
			return;

		int initLitPoly = litPoly;
		litPoly=-1;
		for(int i=0; i < getPolygons().length; i++)
			for(int j=0; j < getPolygons()[i].length; j++)
				if(PolygonUtils.inpoly(getPolygons()[i][j], x, y))
				{
					if( ! popupTest.isShowing())
					{
						popupTest.show();
						popupTest.setPopupPosition(event.getClientX(), event.getClientY());
					}
					//needRedraw = i != litPoly;  // poly is not lit but should be
					//needRedraw = needRedraw || visiblePolys[i] == false;
					//visiblePolys[i]=true;
					litPoly=i;
				}
				else
				{
					//needRedraw = i == litPoly;  // poly is lit but should not be
					//needRedraw = needRedraw || visiblePolys[i] == true;
					//visiblePolys[i]=false;
				}
		//if(needRedraw)
		if( initLitPoly != litPoly)
		{
			redraw(canvas);
		}
	}
	private void redraw(GWTCanvas canvas)
	{
		canvas.clear();

		//if(backgroundImage[0] != null)
			//canvas.drawImage(backgroundImage[0], 0, 0,200,200);

		if(getPolygons() != null)
			for(int i=0; i < getPolygons().length; i++)
				//drawPoly(canvas,getPolygons()[i],visiblePolys[i]);
				drawPoly(canvas,getPolygons()[i], i == litPoly );


	}
	int[][][][] getPolygons()
	{
		if(polygons == null)
		{
			getCoordService().getPolygons(new AsyncCallback<int[][][][]>(){
				public void onFailure(Throwable caught) {
					status.setText("failed to get polygons "+caught.getLocalizedMessage());
				}
				public void onSuccess(int[][][][] result) {
					// init value is false
					//visiblePolys = new boolean[result.length];
					polygons = result;
				}
			});
		}
		// we  expect this to return null until the polygons are actually created by the async callback
		return polygons;
	}
	private void drawPoly( GWTCanvas canvas, int[][][] poly,boolean fill)
	{
		for(int i=0; i < poly.length; i++)
			drawPoly(canvas,poly[i],fill);
	}
	private void drawPoly( GWTCanvas canvas, int[][] poly,boolean fill)
	{
			if(poly.length < 3 )
				return;

			canvas.setLineWidth(1);
			canvas.setStrokeStyle(Color.GREEN);
			canvas.setFillStyle(Color.ALPHA_RED);

			canvas.beginPath();
			canvas.moveTo(poly[0][0], poly[0][1]);
			for(int i=0; i < poly.length; i++)
				canvas.lineTo(poly[i][0], poly[i][1]);

			canvas.closePath();
			if(outlinePolys)
				canvas.stroke();
			if(fill)
				canvas.fill();
	}
	public void onMouseUp(MouseUpEvent event)
	{
		status.setText("mouse up");
	}
	public void onMouseOver(MouseOverEvent event)
	{
		status.setText("mouse is over");
	}

	public static CoordServiceAsync getCoordService()
	{
		CoordServiceAsync service = (CoordServiceAsync) GWT.create(CoordService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL()+"coordservice";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
	}
}
