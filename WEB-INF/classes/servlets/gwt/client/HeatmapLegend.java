/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.allen_sauer.gwt.log.client.Log;

/**
 *
 * @author khoran
 */
public class HeatmapLegend extends Composite
{

	final GWTCanvas heatmapLegend=new GWTCanvas(200,20);
	final Label minValue=new Label();
	final Label maxValue=new Label();

	Color minColor, maxColor;

	public HeatmapLegend(Color minColor, Color maxColor)
	{
		this.minColor=minColor;
		this.maxColor=maxColor;

		AbsolutePanel mainPanel = new AbsolutePanel();
		mainPanel.setSize(""+200, ""+20);
		mainPanel.add(heatmapLegend,0,0);
		mainPanel.add(minValue,12,0);
		mainPanel.add(maxValue,102,0);

		initWidget(mainPanel);
	}
	private void redraw()
	{
		if( ! minValue.getText().equals(""))
		{
			heatmapLegend.setFillStyle(minColor);
			heatmapLegend.fillRect(0, 0, 10, 15);
		}

		if( ! maxValue.getText().equals(""))
		{
			heatmapLegend.setFillStyle(maxColor);
			heatmapLegend.fillRect(90, 0, 10, 15);
		}
	}

	public void setMinValue(double v)
	{
		minValue.setText(""+round(v));
		redraw();
	}
	public void setMaxValue(double v)
	{
		maxValue.setText(""+round(v));
		redraw();
	}

	private double round(double v)
	{
		return ((int)(v*1000))/1000.0;
	}
}
