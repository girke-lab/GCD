/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets.gwt.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author khoran
 */
public class QueryPanel  extends Composite implements ValueChangeHandler<Boolean>, ClickHandler,
		HasValueChangeHandlers<String>, ChangeHandler
{

	final double maxIntensity = 1000, maxPValue = 0.01, lowerRatio = -1, upperRatio = 1;

	final TextBox maxIntensityTB = new TextBox();
	final TextBox maxPValueTB = new TextBox();
	final TextBox upperRatioTB = new TextBox();
	final TextBox  lowerRatioTB = new TextBox();
	final TextBox controlIntensityTB=new TextBox();
	final TextBox treatmentIntensityTB=new TextBox();
	final ListBox controlPmaLB=new ListBox();
	final ListBox treatmentPmaLB=new ListBox();
	final ListBox intensityType = new ListBox();
	final ListBox controlsLB=new ListBox();
	final ListBox treatmentsLB=new ListBox();
	final Button submitSearchButton = new Button("Submit");

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

	//final Label compareToLabel=new Label();
	//final Panel containerPanel = new VerticalPanel();
	//RadioButton[] comparisonRadios;


	public QueryPanel()
	{

		lowerRatioCB.addValueChangeHandler(this);
		upperRatioCB.addValueChangeHandler(this);
		minIntensityCB.addValueChangeHandler(this);
		controlIntensityCB.addValueChangeHandler(this);
		treatmentIntensityCB.addValueChangeHandler(this);
		controlPmaCB.addValueChangeHandler(this);
		treatmentPmaCB.addValueChangeHandler(this);
		maxPValueCB.addValueChangeHandler(this);
		ratioCB.addValueChangeHandler(this);
		controlsLB.addChangeHandler(this);
		treatmentsLB.addChangeHandler(this);


		submitSearchButton.addClickHandler(this);

		maxIntensityTB.setText(""+maxIntensity);
		controlIntensityTB.setText(""+maxIntensity);
		treatmentIntensityTB.setText(""+maxIntensity);
		maxPValueTB.setText(""+maxPValue);
		upperRatioTB.setText(""+upperRatio);
		lowerRatioTB.setText(""+lowerRatio);

		controlPmaLB.addItem("P"); controlPmaLB.addItem("M"); controlPmaLB.addItem("A");
		treatmentPmaLB.addItem("P"); treatmentPmaLB.addItem("M"); treatmentPmaLB.addItem("A");
		intensityType.addItem("mas5");  intensityType.addItem("rma");

		minIntensityOperationOr.setValue(true);

		//containerPanel.add(treatmentsLB);

		initWidget(buildMenuPanel());
	}

	public void setComparisons(String controlDescription, String[] comparisons)
	{
		buildSelectionPanel(controlDescription, comparisons);
	}
	public void setControls(String[] descriptions)
	{
		controlsLB.clear();
		for(String s : descriptions)
			controlsLB.addItem(s);
		if(controlsLB.getItemCount() > 0)
			controlsLB.setSelectedIndex(0);
	}
	public int getSelectedComparison()
	{
		return treatmentsLB.getSelectedIndex();
		//for(int i=0; i < comparisonRadios.length; i++)
			//if(comparisonRadios[i].getValue())
				//return i;
		//return -1;
	}
	public int getSelectedControl()
	{
		return controlsLB.getSelectedIndex();
	}
	public String getIntensityType()
	{
		return intensityType.getValue(intensityType.getSelectedIndex());
	}
	public String getIntensityOperation()
	{
		if(minIntensityOperationAnd.getValue())
			return "AND";
		else if(minIntensityOperationOr.getValue())
			return "OR";
		return "AND";
	}
	public Double getMinControlIntensity()
	{
		if(minIntensityCB.getValue() && controlIntensityCB.getValue() )
			return getDouble(controlIntensityTB);
		return null;
	}
	public Double getMinTreatmentIntensity()
	{
		if(minIntensityCB.getValue() && treatmentIntensityCB.getValue() )
			return getDouble(treatmentIntensityTB);
		return null;
	}
	public String getControlPma()
	{
		if(minIntensityCB.getValue() && controlPmaCB.getValue())
			return controlPmaLB.getValue(controlPmaLB.getSelectedIndex());
		return null;
	}
	public String getTreatementPma()
	{
		if(minIntensityCB.getValue() && treatmentPmaCB.getValue())
			return treatmentPmaLB.getValue(treatmentPmaLB.getSelectedIndex());
		return null;
	}
	public Double getMaxAdjPValue()
	{
		if(maxPValueCB.getValue())
			return getDouble(maxPValueTB);
		return null;
	}
	public Double getlowerRatio()
	{
		if(ratioCB.getValue() && lowerRatioCB.getValue()  )
			return getDouble(lowerRatioTB);
		return null;
	}
	public Double getUpperRatio()
	{
		if(ratioCB.getValue() && upperRatioCB.getValue() )
			return getDouble(upperRatioTB);
		return null;
	}
	private static final Double getDouble(TextBox tb)
	{
		Double d=null;
		try{
			d=Double.parseDouble(tb.getText());
		}catch(NumberFormatException e)
		{
			return null;
		}
		return d;
	}

	public void onClick(ClickEvent event)
	{
		if(submitSearchButton == event.getSource())
			ValueChangeEvent.fire(this, "submit");
			//submissionHandler.run();
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

		//panel.add(buildHorizontalPanel(new Label("Compare"),compareToLabel,new Label("to: ")));
		panel.add(buildHorizontalPanel(new Label("Compare"),controlsLB,new Label("to: "),treatmentsLB));
		//panel.add(containerPanel);

		panel.add(new HTML("<hr>"));
		panel.add(buildHorizontalPanel(new Label("Intensity type: "), intensityType));

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

	void buildSelectionPanel(String description, String[] comparables)
	{
		//compareToLabel.setText(description);
		selectControl(description);
		controlIntensityCB.setText(description);
		treatmentIntensityCB.setText("other");

		//comparisonRadios = new RadioButton[comparables.length];
		treatmentsLB.clear();
		//containerPanel.clear();


		for(int i=0; i < comparables.length; i++)
		{
			treatmentsLB.addItem(comparables[i]);
			//comparisonRadios[i] = new RadioButton("comparison",comparables[i]);
			//comparisonRadios[i].addValueChangeHandler(this);
			//containerPanel.add(comparisonRadios[i]);
		}
		if(comparables.length > 0)
		{
			treatmentsLB.setSelectedIndex(0);
			treatmentIntensityCB.setText(treatmentsLB.getValue(0));
		}
	}
	private void selectControl(String description)
	{
		for(int i=0; i < controlsLB.getItemCount(); i++)
			if(description.equals(controlsLB.getValue(i)))
			{
				controlsLB.setSelectedIndex(i);
				break;
			}
	}
	private Panel buildHorizontalPanel(Widget... widgets)
	{
		HorizontalPanel h=new HorizontalPanel();
		h.setSpacing(2);
		for(Widget w : widgets)
			h.add(w);
		return h;
	}

	public void onChange(ChangeEvent event)
	{
		if( controlsLB == event.getSource())
			ValueChangeEvent.fire(this, "control");
		else if(treatmentsLB == event.getSource() && treatmentsLB.getSelectedIndex() != -1)
		{
			treatmentIntensityCB.setText(treatmentsLB.getValue(treatmentsLB.getSelectedIndex()));
		}
	}


	public void onValueChange(ValueChangeEvent<Boolean> event)
	{

		/*
		if( event.getSource() instanceof RadioButton  && ((RadioButton)event.getSource()).getValue())
		{
			for(int i=0; i < comparisonRadios.length; i++)
				if(event.getSource() == comparisonRadios[i])
				{
					treatmentIntensityCB.setText(comparisonRadios[i].getText());
					break;
				}
		}
		 */
		if(lowerRatioCB == event.getSource())
			lowerRatioTB.setEnabled(lowerRatioCB.getValue());
		else if(upperRatioCB == event.getSource())
			upperRatioTB.setEnabled(upperRatioCB.getValue());
		else if(minIntensityCB  == event.getSource())
		{
			controlIntensityTB.setEnabled(minIntensityCB.getValue() && controlIntensityCB.getValue());
			treatmentIntensityTB.setEnabled(minIntensityCB.getValue() && treatmentIntensityCB.getValue());
			controlPmaLB.setEnabled(minIntensityCB.getValue() && controlPmaCB.getValue());
			treatmentPmaLB.setEnabled(minIntensityCB.getValue() && treatmentPmaCB.getValue());

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
			upperRatioTB.setEnabled(ratioCB.getValue() && upperRatioCB.getValue());
			lowerRatioTB.setEnabled(ratioCB.getValue() && lowerRatioCB.getValue());

			upperRatioCB.setEnabled(ratioCB.getValue());
			lowerRatioCB.setEnabled(ratioCB.getValue());
		}

	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}


}
