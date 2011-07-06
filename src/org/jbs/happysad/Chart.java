package org.jbs.happysad;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Sales growth demo chart.
 */
public class Chart extends AbstractDemoChart {
  	
	/**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Happy - Sad";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "This chart reflects time (x axis) vs happiness (y axis)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Follow the up's and down's" };
    List<Date[]> dates = new ArrayList<Date[]>();
    List<double[]> values = new ArrayList<double[]>();
    Date[] dateValues = new Date[] { new Date(95, 0, 1), new Date(95, 3, 1), new Date(95, 6, 1),
        new Date(95, 9, 1), new Date(96, 0, 1), new Date(96, 3, 1), new Date(96, 6, 1),
        new Date(96, 9, 1), new Date(97, 0, 1), new Date(97, 3, 1), new Date(97, 6, 1),
        new Date(97, 9, 1), new Date(98, 0, 1), new Date(98, 3, 1), new Date(98, 6, 1),
        new Date(98, 9, 1), new Date(99, 0, 1), new Date(99, 3, 1), new Date(99, 6, 1),
        new Date(99, 9, 1), new Date(100, 0, 1), new Date(100, 3, 1), new Date(100, 6, 1),
        new Date(100, 9, 1), new Date(100, 11, 1) };
    dates.add(dateValues);

    values.add(new double[] { 4.9, 5.3, 3.2, 4.5, 6.5, 4.7, 5.8, 4.3, 4, 2.3, -0.5, -2.9, 3.2, 5.5,
        4.6, 9.4, 4.3, 1.2, 0, 0.4, 4.5, 3.4, 4.5, 4.3, 4 });
    int[] colors = new int[] { Color.CYAN };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    setChartSettings(renderer, "Happy Chart", "Date", "Happiness", dateValues[0].getTime(),
        dateValues[dateValues.length - 1].getTime(), -4, 11, Color.LTGRAY, Color.WHITE);
    //renderer.setXLabels(12);
    renderer.setYLabels(10);
    //renderer.setShowGrid(true);
    //renderer.setXLabelsAlign(Align.RIGHT);
    //renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    //renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    //renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
    return ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, dates, values),
        renderer, "MMM yyyy");
  }

}