/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import java.lang.String;

/**
 *
 * @author khoran
 */
public class HeatmapPanel extends Composite implements ClickHandler, ChangeHandler, KeyPressHandler,
		HasValueChangeHandlers<String>
{
	final TextBox accessionTB=new TextBox();
	final Button submitAccessionB=new Button("Submit");
	final Button clearB=new Button("Clear");
	final ListBox probeSetKeysLB=new ListBox();
	final Panel probeKeyQueryPanel;
	final ListBox scalingLB=new ListBox();

	//Runnable accessionQueryHandler, probeKeyQueryHandler, scalingHandler=null;

	//public HeatmapPanel(Runnable accessionQueryHandler, Runnable probeKeyQueryHandler)
	public HeatmapPanel()
	{
		//this.accessionQueryHandler=accessionQueryHandler;
		//this.probeKeyQueryHandler = probeKeyQueryHandler;

		submitAccessionB.addClickHandler(this);
		accessionTB.addKeyPressHandler(this);
		probeSetKeysLB.addChangeHandler(this);
		scalingLB.addChangeHandler(this);
		clearB.addClickHandler(this);

		scalingLB.addItem("Relative scale", "relative");
		scalingLB.addItem("Aboslute scale","absolute");
		scalingLB.addItem("Aboslute log scale", "absoluteLog");

		probeKeyQueryPanel = buildHorizontalPanel(new Label("Select probe set key: "),probeSetKeysLB);
		probeKeyQueryPanel.setVisible(false);

		initWidget(buildMainPanel());
	}

	public String getAccession()
	{
		return accessionTB.getText();
	}
	public String getProbeSetKey()
	{
		int selection=probeSetKeysLB.getSelectedIndex();
		if( selection == -1)
			throw new IllegalStateException("no probe set key selected");
		return probeSetKeysLB.getValue(selection);
	}
	public void setProbeSetKeys(String[] probeSetKeys)
	{
		probeSetKeysLB.clear();

		probeSetKeysLB.addItem(" ");
		for(String s : probeSetKeys)
			probeSetKeysLB.addItem(s);

		probeKeyQueryPanel.setVisible(true);
	}
	//public void setScalingHandler(Runnable scalingHandler)
	//{
		//this.scalingHandler=scalingHandler;
	//}
	public String getScalingMethod()
	{
		return scalingLB.getValue(scalingLB.getSelectedIndex());
	}

	private Panel buildMainPanel()
	{

		VerticalPanel mainPanel=new VerticalPanel();
		mainPanel.setStyleName("selectionPanel");
		mainPanel.setSpacing(2);

		Label title = new Label("Heatmap settings");
		title.setStyleName("heatmapTitle");
		mainPanel.add(title);
		mainPanel.add(scalingLB);
		mainPanel.add(buildHorizontalPanel(new Label("Accession ID: "),accessionTB,submitAccessionB));
		mainPanel.add(probeKeyQueryPanel);
		mainPanel.add(clearB);


		return mainPanel;
	}
	private Panel buildHorizontalPanel(Widget... widgets)
	{
		HorizontalPanel h=new HorizontalPanel();
		h.setSpacing(2);
		for(Widget w : widgets)
			h.add(w);
		return h;
	}

	private void handleAccessionQuery()
	{
		//accessionQueryHandler.run();
		ValueChangeEvent.fire(this,"accession");
		probeKeyQueryPanel.setVisible(false);
	}
	public void onClick(ClickEvent event)
	{
		if(submitAccessionB == event.getSource())
			handleAccessionQuery();
		if(clearB==event.getSource())
			ValueChangeEvent.fire(this,"clear");
	}
	public void onChange(ChangeEvent event)
	{
		if(probeSetKeysLB == event.getSource())
			ValueChangeEvent.fire(this,"probeKey");
			//probeKeyQueryHandler.run();
		else if(scalingLB==event.getSource())
			ValueChangeEvent.fire(this,"scaling");
			//scalingHandler.run();
	}

	public void onKeyPress(KeyPressEvent event)
	{
		if(accessionTB == event.getSource())
			if( ! event.isAnyModifierKeyDown() && event.getCharCode() == '\r')
				handleAccessionQuery();
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler,ValueChangeEvent.getType());
	}

}
