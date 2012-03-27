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
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 *
 * @author khoran
 */
public class CySamEntryPoint  implements  EntryPoint , ValueChangeHandler<String>
{

	final String experimentSetKey="CySam";
	final String rootDivTag=experimentSetKey;
	final double absoluteHeatmapMin=0.0;
	final double absoluteHeatmapMax=101000.0;
	final float heatmapAlpha=0.8f;


	final Label status = new Label("        ");
	final Label helpLabel = new Label();
	final HorizontalPanel canvasPanel = new HorizontalPanel();
	final Label experimentName=new Label();

	final QueryPanel queryPanel = new QueryPanel();
	final HeatmapPanel heatmapPanel = new HeatmapPanel();
	final HeatmapLegend heatmapLegend = new HeatmapLegend(Color.RED, Color.YELLOW);

	PaintableImage[] images=null;
	ExperimentAreas[] experimentAreas;
	int[] comparisons;
	boolean loadingPolygons=false, havePolygons=false;
	double[][] heatmapValues=null;
	double heatmapMin, heatmapMax;
	int litImage,litPoly, stickyLitImage, stickyLitPoly;
	String[] allDescriptions;
	int[] allExperimentIds;


	/** Creates a new instance of MainEntryPoint */
	public CySamEntryPoint()
	{
		litImage=litPoly= stickyLitImage= stickyLitPoly=-1;
	}

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */
	public void onModuleLoad()
	{

		queryPanel.addValueChangeHandler(this );
		heatmapPanel.addValueChangeHandler(this);

		queryPanel.setVisible(false);

		status.setStyleName("statusLabel");


		ScrollPanel imageScrollPanel = new ScrollPanel(canvasPanel);

		Panel searchPanel = new VerticalPanel();
		searchPanel.add(imageScrollPanel);
		searchPanel.add(buildHorizontalPanel(heatmapLegend, experimentName));
		searchPanel.add(heatmapPanel);
		searchPanel.add(helpLabel);
		searchPanel.add(queryPanel);


		helpLabel.setText("Click on the image to start a query");

		RootPanel.get(rootDivTag).add(status);
		RootPanel.get(rootDivTag).add(searchPanel);

		loadPolygons();
		loadExperiments();
	}
	private void redraw()
	{
		if( ! havePolygons)
			return;

		for(int i=0; i < images.length; i++)
			redraw(images[i].getCanvas(), i);
	}
	private void redraw(GWTCanvas canvas, int imageIndex)
	{
		canvas.clear();

		int[][][][] polys = experimentAreas[imageIndex].polys;

		if(heatmapValues != null)
			drawHeatmap(canvas,polys, heatmapValues[imageIndex]);
		for(int i=0; i < polys.length; i++)
			drawPoly(canvas,polys[i],	(imageIndex == litImage && i == litPoly ) ||
															(imageIndex == stickyLitImage && i == stickyLitPoly) );

		if(litPoly == -1)
			experimentName.setText("           ");
		else if(imageIndex == litImage)
			experimentName.setText( experimentAreas[litImage].descriptions[litPoly] +"   " + currentHeatmapValue());
	}
	final String currentHeatmapValue()
	{
		if(heatmapValues==null || litPoly==-1)
			return "";
		double v = ((int)(heatmapValues[litImage][litPoly] * 1000))/1000.0;
		return ""+v;
	}

	void loadPolygons()
	{
		if( ! havePolygons &&  ! loadingPolygons)  //try to avoid loading several times, but not a problem if a few slip through
		{
			loadingPolygons =true;
			// first fetch the image info ( id and dimensions) for this experiment set
			getCoordService().getImageInfo(experimentSetKey, new AsyncCallback<int[][]>(){
				public void onFailure(Throwable caught) {
					status.setText("failed to get image id: "+caught);
					loadingPolygons=false;
				}
				public void onSuccess(int[][] info)
				{
					images = new PaintableImage[info.length];
					experimentAreas = new ExperimentAreas[info.length];
					String[] urls = new String[info.length];
					canvasPanel.clear();

					for(int i=0; i < info.length; i++)
					{
						urls[i] = "/databaseWeb/servlets.gwt.CySam/coordservice?imageId="+info[i][0];
						final int index = i;

						// then we can load both the image and the polygons in parallel
						getCoordService().getPolygons(info[index][0],new AsyncCallback<ExperimentAreas>(){
							public void onFailure(Throwable caught) {
								loadingPolygons=false;
								status.setText("failed to get polygons "+caught.getLocalizedMessage());
								experimentAreas[index]=null;
							}
							public void onSuccess(ExperimentAreas result) {
								experimentAreas[index]=result;
								loadingPolygons=false;
								havePolygons=true;
							}
						});
					}

					// load image and redraw canvas when done
					ImageLoader.loadImages(urls, new ImageLoader.CallBack() {
						public void onImagesLoaded(ImageElement[] imageElements)
						{
							for(int i=0; i < imageElements.length; i++)
							{
								ImageHandler handler = new ImageHandler(i);
								images[i] = new PaintableImage(imageElements[i]);
								images[i].addMouseMoveHandler(handler);
								images[i].addMouseUpHandler(handler);
								images[i].addMouseOutHandler(handler);
								canvasPanel.add(images[i]);
							}
						}
					});
				}
			});
		}
	}
	void loadExperiments()
	{
		getCoordService().getAllExperiments(experimentSetKey, new AsyncCallback<String[][]>(){
			public void onFailure(Throwable caught) {
				status.setText("failed to load experiment list");
			}
			public void onSuccess(String[][] result) {
				allDescriptions = new String[result.length];
				allExperimentIds = new int[result.length];
				for(int i=0; i < result.length; i++)
				{
					allDescriptions[i ]= result[i][0];
					allExperimentIds[i] = Integer.parseInt(result[i][1]);
				}
				queryPanel.setControls(allDescriptions);
			}
		});
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
			if(fill)
				canvas.fill();
	}
	private void drawHeatmap(GWTCanvas canvas, int[][][][] polys, double[] heatmapValues)
	{
		for(int j=0; j < polys.length; j++)
		{
			canvas.setFillStyle(getHeatmapColor( scaleHeatmapValue(heatmapPanel.getScalingMethod(),  heatmapValues[j])));
			for(int[][] poly : polys[j])
			{
				canvas.beginPath();
				canvas.moveTo(poly[0][0], poly[0][1]);
				for(int i=0; i < poly.length; i++)
					canvas.lineTo(poly[i][0], poly[i][1]);
				canvas.fill();
			}
		}
	}
	Color getHeatmapColor(double value)
	{
		return new Color(255, (int)(value*255), 0,heatmapAlpha);
	}
	void displayQueryPanel(final String description, int experimentId)
	{
		getCoordService().getComparableExperiments(experimentId,new AsyncCallback<String[][]>(){
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

				queryPanel.setComparisons(description, names);
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
	void displayHeatmap(final String probeSetKey)
	{
		if( ! havePolygons)
			return;
		status.setText("Please wait");
		final int[] count=new int[]{0};
		heatmapValues = new double[experimentAreas.length][];

		for(int i=0; i < experimentAreas.length; i++)
		{
			final int index=i;
			getCoordService().getIntensities(probeSetKey, experimentAreas[index].experimentIds, new AsyncCallback<double[]>(){
				public void onFailure(Throwable caught) {
					status.setText("no results found for "+probeSetKey);
				}
				public void onSuccess(double[] result) {
					if(result.length==0)
					{
						status.setText("no results found for "+probeSetKey);
						return;
					}
					status.setText("          ");
					heatmapValues[index] = result;

					//TODO: this is a little fragile
					count[0]++;   // to see if we are the last to execute
					if(count[0] == experimentAreas.length)
					{
						scaleHeatmap(heatmapPanel.getScalingMethod());

						heatmapLegend.setMinValue(heatmapMin);
						heatmapLegend.setMinColor(getHeatmapColor(heatmapMin));
						heatmapLegend.setMaxValue(heatmapMax);
						heatmapLegend.setMaxColor(getHeatmapColor(heatmapMax));
						heatmapLegend.setVisible(true);

						redraw();
					}
				}

			});
		}
	}
	void scaleHeatmap(String method)
	{
		if("relative".equals(method))
		{
			// compute min and max for later normalization
			heatmapMin=Double.POSITIVE_INFINITY;
			heatmapMax = Double.NEGATIVE_INFINITY;
			for(int j=0; j < experimentAreas.length; j++)
				for(int i=0; i < heatmapValues[j].length; i++)
					if( heatmapValues[j][i] < heatmapMin)
						heatmapMin=heatmapValues[j][i];
					else if(heatmapValues[j][i] > heatmapMax)
						heatmapMax=heatmapValues[j][i];
		}
		else 
		{
			heatmapMin=absoluteHeatmapMin;
			heatmapMax=absoluteHeatmapMax;
		}
	}
	double scaleHeatmapValue(String method,double value)
	{
		if("absoluteLog".equals(method))
			value= Math.log(value);

		double v = (value - heatmapMin )/(heatmapMax-heatmapMin);
		//Log.debug("method: "+method+", orig value: "+value+", scaled: "+v);
		return v;
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

	public static CoordServiceAsync getCoordService()
	{
		CoordServiceAsync service = (CoordServiceAsync) GWT.create(CoordService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL()+"coordservice";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
	}

	private Panel buildHorizontalPanel(Widget... widgets)
	{
		HorizontalPanel h=new HorizontalPanel();
		h.setSpacing(2);
		for(Widget w : widgets)
			h.add(w);
		return h;
	}

	public void onValueChange(ValueChangeEvent<String> event)
	{
		if(heatmapPanel == event.getSource())
		{
			if(event.getValue().equals("accession"))
				getCoordService().getProbeSetKeys(heatmapPanel.getAccession(), new AsyncCallback<String[]>(){
					public void onFailure(Throwable caught) {
						status.setText("could not find any probe set keys for " +heatmapPanel.getAccession());
					}
					public void onSuccess(String[] result) {
						if(result.length==0)
							status.setText("could not find any probe set keys for " +heatmapPanel.getAccession());
						else if(result.length == 1)
							displayHeatmap(result[0]);
						else
						{
							status.setText("             ");
							heatmapPanel.setProbeSetKeys(result);
						}
					}
				});
			else if(event.getValue().equals("probeKey"))
				displayHeatmap(heatmapPanel.getProbeSetKey());
			else if(event.getValue().equals("scaling"))
			{
				scaleHeatmap(heatmapPanel.getScalingMethod());
				heatmapLegend.setMinColor(getHeatmapColor(heatmapMin));
				heatmapLegend.setMinColor(getHeatmapColor(heatmapMax));
				redraw();
			}
			else if(event.getValue().equals("clear"))
			{
				heatmapValues=null;
				//heatmapLegend.setVisible(false);
				heatmapLegend.clear();
				redraw();
			}
		}
		else if(queryPanel == event.getSource())
		{
			if(event.getValue().equals("control"))
			{

				int selectedControl = queryPanel.getSelectedControl();
				if(selectedControl == -1)
					return;
				displayQueryPanel(allDescriptions[selectedControl],allExperimentIds[selectedControl]);
				stickyLitPoly = stickyLitImage = -1;
				redraw();
			}
			else if(event.getValue().equals("submit"))
				submitQuery();
		}

	}

	private class ImageHandler implements MouseMoveHandler, MouseUpHandler, MouseOutHandler
	{

		int imageIndex;

		public ImageHandler(int index)
		{
			this.imageIndex=index;
		}
		private void setLitPoly(int i)
		{
			litImage=imageIndex;
			litPoly=i;
		}
		private void setStickyListPoly(int i)
		{
			stickyLitImage=imageIndex;
			stickyLitPoly = i;
		}
		public void onMouseMove(MouseMoveEvent event)
		{
			int x=event.getX();
			int y=event.getY();

			if( ! havePolygons )
			{
				loadPolygons();
				return;
			}

			int initLitPoly = litPoly;
			setLitPoly(-1);
			int[][][][] polygons = experimentAreas[imageIndex].polys;

			polySearch:
				for(int i=0; i < polygons.length; i++)
					for(int j=0; j < polygons[i].length; j++)
						if(PolygonUtils.inpoly(polygons[i][j], x, y))
						{
							setLitPoly(i);
							break polySearch; // break out of both loops
						}

			if( initLitPoly != litPoly)
				redraw();
		}
		public void onMouseOut(MouseOutEvent event)
		{
			if(litPoly !=  -1) // some polygon is still lit
			{
				setLitPoly(-1);
				redraw();
			}
		}
		public void onMouseUp(MouseUpEvent event)
		{
			if(litPoly == -1)
				return;

			displayQueryPanel(experimentAreas[imageIndex].descriptions[litPoly],
					experimentAreas[imageIndex].experimentIds[litPoly]);
			setStickyListPoly(litPoly);
			redraw();
		}

	}
}
