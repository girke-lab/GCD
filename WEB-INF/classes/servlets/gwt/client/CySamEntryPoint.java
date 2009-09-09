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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 *
 * @author khoran
 */
public class CySamEntryPoint  implements  EntryPoint, MouseMoveHandler, 
		MouseUpHandler,MouseOverHandler, ClickHandler, FormPanel.SubmitCompleteHandler,
		SelectionHandler<Integer>, MouseOutHandler, ValueChangeHandler<Boolean>
{

	final String experimentSetKey="CySam";
	final String rootDivTag=experimentSetKey;
	final double maxIntensity = 1000, maxPValue = 0.01, lowerRatio = -1, upperRatio = 1;


	final Label status = new Label("");
	final Label helpLabel = new Label();
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
	final TextBox controlIntensityTB=new TextBox();
	final TextBox treatmentIntensityTB=new TextBox();
	final ListBox controlPmaLB=new ListBox();
	final ListBox treatmentPmaLB=new ListBox();
	final Button submitRatioSearchButton = new Button("Submit");
	final Button submitIntensitySearchButton = new Button("Submit");
	final Button submitSearchButton = new Button("Submit");
	final HTML resultPage = new HTML();

	final CheckBox lowerRatioCB=new CheckBox("less than");
	final CheckBox upperRatioCB=new CheckBox("greater than");
	final CheckBox minIntensityCB=new CheckBox("Min Intensity: ");
	final CheckBox controlIntensityCB=new CheckBox();
	final CheckBox treatmentIntensityCB=new CheckBox();
	final CheckBox  controlPmaCB= new CheckBox("PMA");
	final CheckBox  treatmentPmaCB= new CheckBox("PMA");
	final CheckBox maxPValueCB = new CheckBox("Max P-Value: ");
	final CheckBox ratioCB = new CheckBox("Log2 Ratio:");

	final RadioButton minIntensityOperationAnd =new RadioButton("minIntensityOperation","and");
	final RadioButton minIntensityOperationOr =new RadioButton("minIntensityOperation","or");

	boolean outlinePolys =false;

	RadioButton[] comparisonRadios;
	int[] comparisons;

	int litPoly=-1; //hilighted on mouse over
	int stickyLitPoly =-1;  //stays hightlighed after clicking
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
		submitSearchButton.addClickHandler(this);

		lowerRatioCB.addValueChangeHandler(this);
		upperRatioCB.addValueChangeHandler(this);
		minIntensityCB.addValueChangeHandler(this);
		controlIntensityCB.addValueChangeHandler(this);
		treatmentIntensityCB.addValueChangeHandler(this);
		controlPmaCB.addValueChangeHandler(this);
		treatmentPmaCB.addValueChangeHandler(this);
		maxPValueCB.addValueChangeHandler(this);
		ratioCB.addValueChangeHandler(this);



		maxIntensityTB.setText(""+maxIntensity);
		controlIntensityTB.setText(""+maxIntensity);
		treatmentIntensityTB.setText(""+maxIntensity);
		maxPValueTB.setText(""+maxPValue);
		upperRatioTB.setText(""+upperRatio);
		lowerRatioTB.setText(""+lowerRatio);

		controlPmaLB.addItem("P"); controlPmaLB.addItem("M"); controlPmaLB.addItem("A");
		treatmentPmaLB.addItem("P"); treatmentPmaLB.addItem("M"); treatmentPmaLB.addItem("A");

		minIntensityOperationOr.setValue(true);

		menuPanel = buildMenuPanel();
		menuPanel.setVisible(false);

		status.setStyleName("statusLabel");
		RootPanel.get(rootDivTag).add(status);

		// make sure these two canvases overlap each other
		canvasPanel.add(backgroundCanvas,0,0);
		canvasPanel.add(canvas,0,0);

		Panel searchPanel = new VerticalPanel();
		searchPanel.add(canvasPanel);
		searchPanel.add(helpLabel);
		searchPanel.add(menuPanel);

		//tabs.add(searchPanel,"Search");
		//tabs.add(resultPage,"Results");

		//tabs.selectTab(0);
		//tabs.getTabBar().setStyleName("tab-bar");
		//RootPanel.get(rootDivTag).add(tabs);

		helpLabel.setText("Click on the image to start a query");

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
				drawPoly(canvas,getPolygons()[i], i == litPoly || i == stickyLitPoly);
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
		//if(stickyLitPoly != -1 && stickyLitPoly != litPoly )
			//comparisonRadios[litPoly].setValue(true); // not correct, cannot use litPoly here
		//else
		//{
			buildSelectionPanel();
			stickyLitPoly = litPoly;
			redraw(canvas);
		//}
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
		controlIntensityCB.setText(descriptions[litPoly]);
		treatmentIntensityCB.setText("other");
		helpLabel.setText("");

		final ValueChangeHandler<Boolean> handler = this; // 'this' does not refer to the right thing inside the anonymous class

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
					comparisonRadios[i].addValueChangeHandler(handler);
					comparisons[i] = Integer.parseInt(result[i][0]);

					containerPanel.add(comparisonRadios[i]);

				}


				menuPanel.setVisible(true);
			}
		});
	}
	Panel buildMenuPanel()
	{
		// set all to true, then change some to false so
		// that they actually fire a value changed event
		// and update the enabled state of other things
		minIntensityCB.setValue(true);
		controlIntensityCB.setValue(true);
		treatmentIntensityCB.setValue(true);
		controlPmaCB.setValue(true);
		treatmentPmaCB.setValue(true);
		maxPValueCB.setValue(true);
		ratioCB.setValue(true);
		lowerRatioCB.setValue(true);
		upperRatioCB.setValue(true);

		upperRatioCB.setValue(false,true);
		minIntensityCB.setValue(false,true);
		controlPmaCB.setValue(false,true);
		treatmentPmaCB.setValue(false,true);

		VerticalPanel panel = new VerticalPanel();

		panel.add(buildHorizontalPanel(new Label("Compare"),compareToLabel,new Label("to: ")));
		panel.add(containerPanel);

		panel.add(new HTML("<hr>"));

		panel.add(minIntensityCB);
		panel.add(buildHorizontalPanel(controlIntensityCB, controlIntensityTB, controlPmaCB,controlPmaLB));
		panel.add(buildHorizontalPanel(minIntensityOperationAnd,minIntensityOperationOr));
		panel.add(buildHorizontalPanel(treatmentIntensityCB, treatmentIntensityTB, treatmentPmaCB,treatmentPmaLB));

		panel.add(new HTML("<hr>"));

		panel.add(buildHorizontalPanel(maxPValueCB,maxPValueTB));

		panel.add(new HTML("<hr>"));

		panel.add(ratioCB);
		panel.add(buildHorizontalPanel(lowerRatioCB, lowerRatioTB));
		panel.add(new Label("or"));
		panel.add(buildHorizontalPanel(upperRatioCB, upperRatioTB));

		panel.add(submitSearchButton);
		panel.setStylePrimaryName("selectionPanel");

		return panel;
	}
	private Panel buildHorizontalPanel(Widget... widgets)
	{
		HorizontalPanel h=new HorizontalPanel();
		h.setSpacing(2);
		for(Widget w : widgets)
			h.add(w);
		return h;
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

		//status.setText("submitting query, comparison="+comparison);
		status.setText("Fetching results, please wait");


		getCoordService().doIntensityQuery(experimentSetKey, "mas5",comparison,
				maxIntensity,new AsyncCallback<Integer>(){

			public void onFailure(Throwable caught) {
				status.setText("Query failed: "+caught);
			}
			public void onSuccess(Integer result) {
				handleQueryResult(result);
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

		//status.setText("submitting query, comparison="+comparison);
		status.setText("Fetching results, please wait");

		getCoordService().doRatioQuery(experimentSetKey, "mas5",comparison,maxPValue,lowerRatio,upperRatio,
				new AsyncCallback<Integer>(){

			public void onFailure(Throwable caught) {
				status.setText("Query failed: "+caught);
			}
			public void onSuccess(Integer result) {
				handleQueryResult(result);
			}
		});
	}
	void submitQuery()
	{
		status.setText("Fetching results, please wait, test");

	}
	void handleQueryResult(Integer result)
	{
		if(result == -1)
			status.setText("Query failed");
		if(result == -2)
			status.setText("No results found");
		else
			Location.assign("/databaseWeb/QueryPageServlet?hid="+result);

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

		//status.setText("submitting query");
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
		else if(submitSearchButton == event.getSource())
			submitQuery();

	}

	public void onSubmitComplete(SubmitCompleteEvent event)
	{
		resultPage.setHTML(event.getResults());
		//tabs.selectTab(1);
	}

	public void onSelection(SelectionEvent<Integer> event)
	{
	}

	public void onValueChange(ValueChangeEvent<Boolean> event)
	{
		//status.setText(status.getText()+"\n<p> value changed on "+((CheckBox)event.getSource()).getText()+" object value: "+
						//((CheckBox)event.getSource()).getValue()+", given value: "+event.getValue());

		if( event.getSource() instanceof RadioButton  && ((RadioButton)event.getSource()).getValue())
		{
			for(int i=0; i < comparisonRadios.length; i++)
				if(event.getSource() == comparisonRadios[i])
				{
					treatmentIntensityCB.setText(comparisonRadios[i].getText());
					break;
				}
		}
		else if(lowerRatioCB == event.getSource())
			lowerRatioTB.setEnabled(lowerRatioCB.getValue());
		else if(upperRatioCB == event.getSource())
			upperRatioTB.setEnabled(upperRatioCB.getValue());
		else if(minIntensityCB  == event.getSource())
		{
			controlIntensityTB.setEnabled(controlIntensityCB.getValue());
			treatmentIntensityTB.setEnabled(treatmentIntensityCB.getValue());
			controlPmaLB.setEnabled(controlPmaCB.getValue());
			treatmentPmaLB.setEnabled(treatmentPmaCB.getValue());

			controlIntensityCB.setEnabled(minIntensityCB.getValue());
			treatmentIntensityCB.setEnabled(minIntensityCB.getValue());
			controlPmaCB.setEnabled(minIntensityCB.getValue());
			treatmentPmaCB.setEnabled(minIntensityCB.getValue());

			minIntensityOperationAnd.setEnabled(minIntensityCB.getValue());
			minIntensityOperationOr.setEnabled(minIntensityCB.getValue());

		}
		else if( controlIntensityCB == event.getSource())
			controlIntensityTB.setEnabled(controlIntensityCB.getValue());
		else if( treatmentIntensityCB == event.getSource())
			treatmentIntensityTB.setEnabled(treatmentIntensityCB.getValue());
		else if( controlPmaCB == event.getSource())
			controlPmaLB.setEnabled(controlPmaCB.getValue());
		else if(treatmentPmaCB  == event.getSource())
			treatmentPmaLB.setEnabled(treatmentPmaCB.getValue());
		else if(  maxPValueCB== event.getSource())
			maxPValueTB.setEnabled(maxPValueCB.getValue());
		else if(ratioCB  == event.getSource())
		{
			upperRatioTB.setEnabled(upperRatioCB.getValue());
			lowerRatioTB.setEnabled(lowerRatioCB.getValue());

			upperRatioCB.setEnabled(ratioCB.getValue());
			lowerRatioCB.setEnabled(ratioCB.getValue());
		}

	}


}
