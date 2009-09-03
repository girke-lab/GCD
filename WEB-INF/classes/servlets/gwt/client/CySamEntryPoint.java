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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
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
public class CySamEntryPoint  implements  EntryPoint, MouseMoveHandler, 
		MouseUpHandler,MouseOverHandler, ClickHandler, FormPanel.SubmitCompleteHandler,
		SelectionHandler<Integer>, MouseOutHandler
{

	final String experimentSetKey="CySam";
	final String rootDivTag=experimentSetKey;
	final double maxIntensity = 1000, maxPValue = 0.01, lowerRatio = -1, upperRatio = 1;


	final Label status = new Label("");
	Panel selectionPanel = new VerticalPanel();
	final ListeningCanvas canvas = new ListeningCanvas(400,400);
	final ListeningCanvas backgroundCanvas = new ListeningCanvas(400,400);
	final Panel containerPanel = new VerticalPanel();
	final AbsolutePanel canvasPanel = new AbsolutePanel();
	Panel menuPanel;
	final Label experimentName=new Label();
	final Label compareToLabel=new Label();

	final TextBox maxIntensityTB = new TextBox();
	final TextBox maxPValueTB = new TextBox();
	final TextBox upperRatioTB = new TextBox();
	final TextBox  lowerRatioTB = new TextBox();
	final Button submitRatioSearchButton = new Button("Submit");
	final Button submitIntensitySearchButton = new Button("Submit");
	final HTML resultPage = new HTML();
	final CheckBox lowerRatioCB=new CheckBox("less than");
	final CheckBox upperRatioCB=new CheckBox("greater than");

	boolean outlinePolys =false;

	RadioButton[] comparisonRadios;
	int[] comparisons;

	int litPoly=-1;
	int[][][][] polygons=null;
	int[] experimentIds=null;
	String[] descriptions=null;
	int image_id=-1;
	boolean loadingPolygons=false;

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
		canvas.addMouseOutHandler(this);

		submitRatioSearchButton.addClickHandler(this);
		submitIntensitySearchButton.addClickHandler(this);

		lowerRatioCB.addClickHandler(this);
		upperRatioCB.addClickHandler(this);

		maxIntensityTB.setText(""+maxIntensity);
		maxPValueTB.setText(""+maxPValue);
		upperRatioTB.setText(""+upperRatio);
		lowerRatioTB.setText(""+lowerRatio);


		menuPanel = buildMenuPanel();
		menuPanel.setVisible(false);

		RootPanel.get(rootDivTag).add(status);

		// make sure these two canvases overlap each other
		canvasPanel.add(backgroundCanvas,0,0);
		canvasPanel.add(canvas,0,0);

		Panel searchPanel = new VerticalPanel();
		searchPanel.add(canvasPanel);
		searchPanel.add(menuPanel);

		//tabs.add(searchPanel,"Search");
		//tabs.add(resultPage,"Results");

		//tabs.selectTab(0);
		//tabs.getTabBar().setStyleName("tab-bar");
		//RootPanel.get(rootDivTag).add(tabs);

		RootPanel.get(rootDivTag).add(searchPanel);

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
						//expLabel.setText(""+experimentIds[litPoly][0]);
						break polySearch; // break out of both loops
					}

		if( initLitPoly != litPoly)
			redraw(canvas);
	}
	public void onMouseOut(MouseOutEvent event)
	{
		if(litPoly !=  -1) // some polygon is still lit
		{
			litPoly = -1;
			redraw(canvas);
		}
	}
	private void redraw(GWTCanvas canvas)
	{
		canvas.clear();

		if(getPolygons() != null)
			for(int i=0; i < getPolygons().length; i++)
				drawPoly(canvas,getPolygons()[i], i == litPoly );
		if(litPoly == -1)
			experimentName.setText("");
		else
			experimentName.setText( descriptions[litPoly]); //     comparisonRadios[litPoly].getText());
	}

	int[][][][] getPolygons()
	{
		if(polygons == null &&  ! loadingPolygons)  //try to avoid loading several times, but not a problem if a few slip through
		{
			loadingPolygons =true;
			// first fetch the image info ( id and dimensions) for this experiment set
			getCoordService().getImageInfo(experimentSetKey, new AsyncCallback<int[]>(){
				public void onFailure(Throwable caught) {
					status.setText("failed to get image id: "+caught);
					loadingPolygons=false;
				}
				public void onSuccess(int[] info)
				{

					image_id= info[0];
					final int width = info[1];
					final int height = info[2];
					canvasPanel.setSize(""+width,""+(height+30));
					canvasPanel.add(experimentName,width/2,height+10);

					// then we can load both the image and the polygons in parallel
					getCoordService().getPolygons(image_id,new AsyncCallback<ExperimentAreas>(){
						public void onFailure(Throwable caught) {
							loadingPolygons=false;
							status.setText("failed to get polygons "+caught.getLocalizedMessage());
						}
						public void onSuccess(ExperimentAreas result) {
							polygons = result.polys;
							experimentIds = result.experimentIds;
							descriptions = result.descriptions;
							loadingPolygons=false;
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
			canvas.setFillStyle(Color.ALPHA_GREY);

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
		//menuPanel.setVisible(false);
		if(litPoly == -1 || experimentIds == null )
			return;
		compareToLabel.setText(descriptions[litPoly]);
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
					comparisonRadios[i] = new RadioButton("comparison", result[i][1]);
					comparisons[i] = Integer.parseInt(result[i][0]);

					containerPanel.add(comparisonRadios[i]);

				}


				menuPanel.setVisible(true);
			}
		});
	}
	Panel buildMenuPanel()
	{
		lowerRatioCB.setValue(true);
		upperRatioCB.setValue(false);

		lowerRatioTB.setEnabled(lowerRatioCB.getValue());
		upperRatioTB.setEnabled(upperRatioCB.getValue());

		VerticalPanel panel = new VerticalPanel();

		HorizontalPanel h =new HorizontalPanel();
		h.setSpacing(2);
		h.add(new Label("Compare "));
		h.add(compareToLabel);
		h.add(new Label(" to: "));
		panel.add(h);

		panel.add(containerPanel);

		panel.add(new HTML("<hr>"));

		 h =new HorizontalPanel();
		h.setSpacing(2);
		h.add(new Label("Min Intensity: "));
		h.add(maxIntensityTB);
		panel.add(h);

		panel.add(submitIntensitySearchButton);

		panel.add(new HTML("<hr>"));

		h=new HorizontalPanel();
		h.setSpacing(2);
		h.add(new Label("Max P-Value:    "));
		h.add(maxPValueTB);
		panel.add(h);

		panel.add(new Label("Change ratio: "));

		h=new HorizontalPanel();
		h.setSpacing(2);
		h.add(lowerRatioCB);
		h.add(lowerRatioTB);
		panel.add(h);

		panel.add(new Label("or"));

		h=new HorizontalPanel();
		h.setSpacing(2);
		h.add(upperRatioCB);
		h.add(upperRatioTB);
		panel.add(h);

		panel.add(submitRatioSearchButton);

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
	int getSelectedComparison()
	{
		for(int i=0; i < comparisonRadios.length; i++)
			if(comparisonRadios[i].getValue())
				return comparisons[i];
		return -1;
	}
	void submitIntensityQuery()
	{
		int comparison=getSelectedComparison();
		double maxIntensity;

		maxIntensity = Double.parseDouble(maxIntensityTB.getText());

		status.setText("submitting query, comparison="+comparison);


		getCoordService().doIntensityQuery(experimentSetKey, "mas5",comparison,
				maxIntensity,new AsyncCallback<Integer>(){

			public void onFailure(Throwable caught) {
				status.setText("query failed: "+caught);
			}
			public void onSuccess(Integer result)
			{
				if(result == -1)
					status.setText("query failed");
				else
					Location.assign("/databaseWeb/QueryPageServlet?hid="+result);
			}
		});

	}
	void submitRatioQuery()
	{
		int comparison=getSelectedComparison();
		double  maxPValue, upperRatio, lowerRatio;

		maxPValue = Double.parseDouble(maxPValueTB.getText());
		upperRatio= upperRatioCB.getValue() ? Double.parseDouble(upperRatioTB.getText())  : Double.POSITIVE_INFINITY;
		lowerRatio = lowerRatioCB.getValue() ? Double.parseDouble(lowerRatioTB.getText()) : Double.NEGATIVE_INFINITY;

		status.setText("submitting query, comparison="+comparison);

		getCoordService().doRatioQuery(experimentSetKey, "mas5",comparison,maxPValue,lowerRatio,upperRatio,
				new AsyncCallback<Integer>(){

			public void onFailure(Throwable caught) {
				status.setText("query failed: "+caught);
			}
			public void onSuccess(Integer result)
			{
				if(result == -1)
					status.setText("query failed");
				else
					Location.assign("/databaseWeb/QueryPageServlet?hid="+result);
			}
		});


	}
	void submitQueryAsForm()
	{

		FormPanel form = new FormPanel();
		Panel formPanel = new VerticalPanel();

		for(int i=0; i < comparisonRadios.length; i++)
			if(comparisonRadios[i].getValue())
			{
				formPanel.add(new Hidden("comparison",""+comparisons[i]));
				break;
			}

		formPanel.add(new Hidden("maxIntensity",maxIntensityTB.getText()));
		formPanel.add(new Hidden("maxPValue",maxPValueTB.getText()));
		formPanel.add(new Hidden("upperRatio",upperRatioTB.getText()));
		formPanel.add(new Hidden("lowerRatio",lowerRatioTB.getText()));
		formPanel.add(new Hidden("experimentSetKey",experimentSetKey));
		formPanel.add(new Hidden("intensityType","mas5"));

		form.setAction("/databaseWeb/servlets.gwt.CySam/coordservice");
		form.setMethod(FormPanel.METHOD_GET);
		form.setWidget(formPanel);
		form.addSubmitCompleteHandler(this);

		form.setVisible(false);

		RootPanel.get(rootDivTag).add(form);

		status.setText("submitting query");
		resultPage.setHTML("");
		form.submit();
		menuPanel.setVisible(false);

	}

	public void onClick(ClickEvent event)
	{
		if(submitRatioSearchButton == event.getSource())
		{
			submitRatioQuery();
			//submitQueryAsForm();
		}
		else if(submitIntensitySearchButton == event.getSource())
		{
			submitIntensityQuery();
		}
		else if(lowerRatioCB == event.getSource())
			lowerRatioTB.setEnabled(lowerRatioCB.getValue());
		else if(upperRatioCB == event.getSource())
			upperRatioTB.setEnabled(upperRatioCB.getValue());
	}

	public void onSubmitComplete(SubmitCompleteEvent event)
	{
		resultPage.setHTML(event.getResults());
		//tabs.selectTab(1);
	}

	public void onSelection(SelectionEvent<Integer> event)
	{
	}


}
