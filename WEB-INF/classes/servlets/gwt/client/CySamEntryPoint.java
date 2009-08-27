/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 *
 * @author khoran
 */
public class CySamEntryPoint  implements  EntryPoint, MouseMoveHandler, MouseUpHandler,MouseOverHandler, ClickHandler
{

	final String experimentSetKey="CySam";

	//final PopupPanel popupPanel = new PopupPanel(true);

	final Label status = new Label("");
	Panel selectionPanel = new VerticalPanel();
	final ListeningCanvas canvas = new ListeningCanvas(400,400);
	final ListeningCanvas backgroundCanvas = new ListeningCanvas(400,400);
	final Label expLabel = new Label();
	final Panel containerPanel = new VerticalPanel();
	Panel menuPanel;

	final TextBox maxIntensityTB = new TextBox();
	final TextBox maxPValueTB = new TextBox();
	final TextBox upperRatioTB = new TextBox();
	final TextBox  lowerRatioTB = new TextBox();
	final Button submitButton = new Button("Submit");

	boolean outlinePolys =false;

	RadioButton[] comparisonRadios;
	int[] comparisons;

	//ImageElement[] backgroundImage= new ImageElement[1];

	//final int testPolys[][][]= new int[][][] {
		//{ {1,1} , { 1,50}, {10,60}, {50,30},{30,10} },
		//{ {100,100},{100,200},{200,200} }
	//};

	int litPoly=-1;
	int[][][][] polygons=null;
	int[][] experimentIds=null;
	int image_id=-1;

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

		submitButton.addClickHandler(this);

		//String[] urls = new String[] {"http://localhost:8080/gwtTest/clan-vs-full-family-2500.png"};
		//selectionPanel = buildSelectionPanel();

		//popupPanel.setWidget(selectionPanel);

		menuPanel = buildMenuPanel();
		menuPanel.setVisible(false);

		RootPanel.get("CySam").add(status);

		AbsolutePanel canvasPanel = new AbsolutePanel();
		canvasPanel.add(backgroundCanvas,0,0);
		canvasPanel.add(canvas,0,0);
		canvasPanel.setSize("300", "300");
		RootPanel.get("CySam").add(canvasPanel);
		RootPanel.get("CySam").add(menuPanel);

		getPolygons();

	}

	public void onMouseMove(MouseMoveEvent event)
	{
		int x,y;
		x=event.getX();
		y=event.getY();

		if(getPolygons() == null)
			return;

		int initLitPoly = litPoly;
		litPoly=-1;

		polySearch:
			for(int i=0; i < getPolygons().length; i++)
				for(int j=0; j < getPolygons()[i].length; j++)
					if(PolygonUtils.inpoly(getPolygons()[i][j], x, y))
					{
						litPoly=i;
						expLabel.setText(""+experimentIds[litPoly][0]);
						break polySearch; // break out of both loops
					}

		if( initLitPoly != litPoly)
			redraw(canvas);
	}
	private void redraw(GWTCanvas canvas)
	{
		canvas.clear();

		if(getPolygons() != null)
			for(int i=0; i < getPolygons().length; i++)
				drawPoly(canvas,getPolygons()[i], i == litPoly );
	}

	int[][][][] getPolygons()
	{
		if(polygons == null)
		{
			// first fetch the image info ( id and dimensions) for this experiment set
			getCoordService().getImageInfo(experimentSetKey, new AsyncCallback<int[]>(){
				public void onFailure(Throwable caught) {
					status.setText("failed to get image id: "+caught);
				}
				public void onSuccess(int[] info)
				{
					image_id= info[0];
					final int width = info[1];
					final int height = info[2];
					// then we can load both the image and the polygons in parallel
					getCoordService().getPolygons(image_id,new AsyncCallback<ExperimentAreas>(){
						public void onFailure(Throwable caught) {
							status.setText("failed to get polygons "+caught.getLocalizedMessage());
						}
						public void onSuccess(ExperimentAreas result) {
							polygons = result.polys;
							experimentIds = result.experimentIds;
						}
					});

					// load image and redraw canvas when done
					String[] urls = new String[] {"/databaseWeb/servlets.gwt.CySam/coordservice?imageId="+image_id};
					ImageLoader.loadImages(urls, new ImageLoader.CallBack() {
						public void onImagesLoaded(ImageElement[] imageElements) {

							backgroundCanvas.drawImage(imageElements[0], 0, 0,width,height);
							redraw(canvas);
						}
					});

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
		//if( ! popupPanel.isShowing() && litPoly != -1)
		//{
			//popupPanel.show();
			//popupPanel.setPopupPosition(event.getClientX(), event.getClientY());
		//}

		buildSelectionPanel();
	}
	public void onMouseOver(MouseOverEvent event)
	{
	}

	void buildSelectionPanel()
	{
		menuPanel.setVisible(false);
		getCoordService().getComparableExperiments(experimentIds[litPoly],new AsyncCallback<String[][]>(){
			public void onFailure(Throwable caught) {
				status.setText("failed to get experiments: "+caught);
			}
			public void onSuccess(String[][] result) {
				Panel panel  = new VerticalPanel();
				comparisonRadios = new RadioButton[result.length];
				comparisons = new int[result.length];

				containerPanel.clear();

				for(int i=0; i < result.length; i++)
				{
					comparisonRadios[i] = new RadioButton("A", result[i][1]);
					comparisons[i] = Integer.parseInt(result[i][0]);

					containerPanel.add(comparisonRadios[i]);

				}


				menuPanel.setVisible(true);
			}
		});
	}
	Panel buildMenuPanel()
	{
		Panel panel = new VerticalPanel();
		panel.add(new Label("Compare to:"));
		panel.add(containerPanel);

		Panel h =new HorizontalPanel();
		h.add(new Label("Max Intensity: "));
		h.add(maxIntensityTB);
		panel.add(h);

		h=new HorizontalPanel();
		h.add(new Label("Max P-Value: "));
		h.add(maxPValueTB);
		panel.add(h);

		h=new HorizontalPanel();
		h.add(new Label("Change ratio less than "));
		h.add(lowerRatioTB);
		h.add(new Label(" or greater than "));
		h.add(upperRatioTB);
		panel.add(h);

		panel.add(submitButton);

		panel.setStylePrimaryName("selectionPanel");

		return panel;
	}
	public static CoordServiceAsync getCoordService()
	{
		CoordServiceAsync service = (CoordServiceAsync) GWT.create(CoordService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL()+"coordservice";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
	}
	void submitQuery()
	{
		int comparison=-1;
		double maxIntensity, maxPValue, upperRatio, lowerRatio;

		for(int i=0; i < comparisonRadios.length; i++)
			if(comparisonRadios[i].getValue())
			{
				comparison = comparisons[i];
				break;
			}
		maxIntensity = Double.parseDouble(maxIntensityTB.getText());
		maxPValue = Double.parseDouble(maxPValueTB.getText());
		upperRatio= Double.parseDouble(upperRatioTB.getText());
		lowerRatio = Double.parseDouble(lowerRatioTB.getText());

		status.setText("submitting query, comparison="+comparison);


		getCoordService().doQuery(experimentSetKey, "mas5",comparison,maxPValue,lowerRatio,upperRatio,
				maxIntensity,new AsyncCallback<Integer>(){

			public void onFailure(Throwable caught) {
				status.setText("query failed: "+caught);
			}
			public void onSuccess(Integer result)
			{
				status.setText("query returned");
			}
		});

	}

	public void onClick(ClickEvent event)
	{
		if(submitButton == event.getSource())
		{
			submitQuery();
		}
	}
}
