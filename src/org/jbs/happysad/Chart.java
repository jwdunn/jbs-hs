package org.jbs.happysad;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * First HappyData Chart
 * @author HappySad
 */
public class Chart extends AbstractChart {
  	
	/**
	 * Returns the chart name as a string
	 * @return the chart name as a string
	 */
	public String getName() {
		return "Happy - Sad";
	}

	/**
	 * Returns the chart description.
	 * @return the chart description
	 */
	public String getDesc() {
		return "This chart reflects time (x axis) vs happiness (y axis)";
	}

	/**
	 * Executes the chart demo.
	 * @param context the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		HappyData datahelper = new HappyData(context);
		ArrayList<HappyBottle> plottables = datahelper.getAllHistory();
		ArrayList<Double> cat = emoTrace(plottables);
	
		double [] dog = new double[cat.size()]; // = (double []) cat.toArray();
		for (int i=0; i<cat.size(); i++)
			dog[i]=cat.get(i);
		String[] titles = new String[] { "Follow the up's and down's" };
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		Date[] dateValues = new Date[] { new Date(111, 0, 1), new Date(111, 3, 1), new Date(111, 6, 1),
				new Date(111, 9, 1), new Date(112, 0, 1), new Date(112, 3, 1), new Date(112, 6, 1),
				new Date(112, 9, 1), new Date(113, 0, 1), new Date(113, 3, 1), new Date(113, 6, 1),
				new Date(113, 9, 1), new Date(114, 0, 1), new Date(114, 3, 1), new Date(114, 6, 1),
				new Date(114, 9, 1), new Date(115, 0, 1), new Date(115, 3, 1), new Date(115, 6, 1),
				new Date(115, 9, 1), new Date(116, 0, 1), new Date(116, 3, 1), new Date(116, 6, 1),
				new Date(116, 9, 1), new Date(116, 11, 1) };
		dates.add(dateValues);

		values.add(dog);
		int[] colors = new int[] { Color.CYAN };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, "Happy Chart", "Date", "Happiness", dateValues[0].getTime(),
				dateValues[dateValues.length - 1].getTime(), -4, 11, Color.LTGRAY, Color.WHITE);
		renderer.setYLabels(10);
		renderer.setZoomButtonsVisible(true);
		return ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, dates, values),
				renderer, "MMM yyyy");
	}
  
	public ArrayList<Double> emoTrace(ArrayList<HappyBottle> plottables){
		Iterator<HappyBottle> itr = plottables.iterator(); 
		double trace = 0;
		ArrayList<Double> traceline = new ArrayList<Double>();
		traceline.add(trace);
		while(itr.hasNext()) {     
		   	HappyBottle element = itr.next();
		     if (element.getEmo() == 1){
		    	trace += 2; 
		    	traceline.add(trace); 
		     } else {
		    	trace -= 2; 
			    traceline.add(trace); 
		     }
		} 
		return traceline;
	}
	
	public ArrayList<Date> dateTrace(ArrayList<HappyBottle> plottables){
		Iterator<HappyBottle> itr = plottables.iterator(); 
		ArrayList<Date> dates = new ArrayList<Date>();
		while(itr.hasNext()) {     
		   	HappyBottle element = itr.next();
	   } 
	   return dates;
	}
}	