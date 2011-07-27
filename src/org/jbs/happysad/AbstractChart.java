package org.jbs.happysad;

import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * An abstract class for the charts to extend.
 */
public abstract class AbstractChart implements ChartInterface {

	/**
	 * Builds a category series using the provided values.
	 * @param titles the series titles
	 * @param values the values
	 * @return the category series
	 */
	protected CategorySeries buildCategoryDataset(String title, double[] values) {
		CategorySeries series = new CategorySeries(title);
		series.add("Happy - " + values [0] + " %", values [0]);
		series.add("Sad - " + values [1] + " %", values [1]);
		return series;
	}

	/**
	 * Builds a category renderer to use the provided colors. 
	 * @param colors the colors
	 * @return the category renderer
	 */
	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	
}