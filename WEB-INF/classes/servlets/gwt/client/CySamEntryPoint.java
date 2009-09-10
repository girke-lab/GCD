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
		MouseUpHandler, ClickHandler, MouseOutHandler, ValueChangeHandler<Boolean>
{

	final String experimentSetKey="CySam";
	final String rootDivTag=experimentSetKey;


	final Label status = new Label("");
	final Label helpLabel = new Label();
	final ListeningCanvas canvas = new ListeningCanvas(400,400);
	final ListeningCanvas backgroundCanvas = new ListeningCanvas(400,400);
	final AbsolutePanel canvasPanel = new AbsolutePanel();
	final Label experimentName=new Label();

	QueryPanel queryPanel = new QueryPanel(buildSubmissionHandler());

	boolean outlinePolys =false;


	int litPoly=-1; //hilighted on mouse over
	int stickyLitPoly =-1;  //stays hightlighed after clicking
	int[][][][] polygons=null;
	int[] experimentIds=null;
	int[] comparisons;
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
		canvas.addMouseUpHandler(this);
		canvas.addMouseOutHandler(this);


		queryPanel.setVisible(false);

		status.setStyleName("statusLabel");
		RootPanel.get(rootDivTag).add(status);


		// make sure these two canvases overlap each other
		canvasPanel.add(backgroundCanvas,0,0);
		canvasPanel.add(canvas,0,0);

		Panel searchPanel = new VerticalPanel();
		searchPanel.add(canvasPanel);
		searchPanel.add(helpLabel);
		searchPanel.add(queryPanel);


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
	public void onMouseUp(MouseUpEvent event)
	{
		//if(stickyLitPoly != -1 && stickyLitPoly != litPoly )
			//comparisonRadios[litPoly].setValue(true); // not correct, cannot use litPoly here
		//else
		//{

			displayQueryPanel();
			//buildSelectionPanel();
			stickyLitPoly = litPoly;
			redraw(canvas);
		//}
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
		void displayQueryPanel()
	{
		if(litPoly == -1 || experimentIds == null )
			return;


		getCoordService().getComparableExperiments(experimentIds[litPoly],new AsyncCallback<String[][]>(){
			public void onFailure(Throwable caught) {
				status.setText("failed to get experiments: "+caught);
			}
			public void onSuccess(String[][] result) {
				String[] names = new String[result.length];
				comparisons = new int[result.length];

				helpLabel.setText("");

				for(int i=0; i < result.length; i++)
				{
					comparisons[i] = Integer.parseInt(result[i][0]);
					names[i] = result[i][1];
				}

				queryPanel.setComparisons(descriptions[litPoly],names);
				queryPanel.setVisible(true);
			}
		});
	}
	Runnable buildSubmissionHandler()
	{
		return new Runnable(){
			public void run() {
				submitQuery();
			}
		};
	}
	void submitQuery()
	{
		status.setText("Fetching results, please wait");

		Integer comparison=comparisons[queryPanel.getSelectedComparison()];

		getCoordService().doQuery(experimentSetKey, queryPanel.getIntensityType(), comparison,
				queryPanel.getMinControlIntensity(), queryPanel.getMinTreatmentIntensity(),
				queryPanel.getControlPma(), queryPanel.getTreatementPma(), queryPanel.getIntensityOperation(),
				queryPanel.getMaxAdjPValue(), queryPanel.getlowerRatio(), queryPanel.getUpperRatio(),
				new AsyncCallback<Integer>(){
			public void onFailure(Throwable caught) {
				status.setText("Query failed: "+caught);
			}
			public void onSuccess(Integer result) {
				handleQueryResult(result);
			}
		}) ;
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
	public void onClick(ClickEvent event)
	{
	}
	public void onValueChange(ValueChangeEvent<Boolean> event)
	{
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
