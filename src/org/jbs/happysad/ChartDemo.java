package org.jbs.happysad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
import org.achartengine.chartdemo.demo.chart.AverageTemperatureChart;
import org.achartengine.chartdemo.demo.chart.BudgetDoughnutChart;
import org.achartengine.chartdemo.demo.chart.BudgetPieChart;
import org.achartengine.chartdemo.demo.chart.CombinedTemperatureChart;
import org.achartengine.chartdemo.demo.chart.IChart;
import org.achartengine.chartdemo.demo.chart.MultipleTemperatureChart;
import org.achartengine.chartdemo.demo.chart.ProjectStatusBubbleChart;
import org.achartengine.chartdemo.demo.chart.ProjectStatusChart;
import org.achartengine.chartdemo.demo.chart.SalesBarChart;
import org.achartengine.chartdemo.demo.chart.SalesComparisonChart;
import org.achartengine.chartdemo.demo.chart.SalesStackedBarChart;
import org.achartengine.chartdemo.demo.chart.ScatterChart;
import org.achartengine.chartdemo.demo.chart.SensorValuesChart;
import org.achartengine.chartdemo.demo.chart.TemperatureChart;
import org.achartengine.chartdemo.demo.chart.TrigonometricFunctionsChart;
import org.achartengine.chartdemo.demo.chart.WeightDialChart;
import org.achartengine.chartdemo.demo.chart.XYChartBuilder;
*/

import org.jbs.happysad.Chart;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ChartDemo extends ListActivity {
  private IChart[] mCharts = new IChart[] { new Chart() };

  private String[] mMenuText;

  private String[] mMenuSummary;
  
  public ArrayList<Integer> chartline;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //int length = mCharts.length;
    mMenuText = new String[1];
    mMenuSummary = new String[1];
    mMenuText[0] = "HAPPY CHART!!!";
    mMenuSummary[0] = "Click here to see the graph for your happiness performance!";
    
    /*
      mMenuText[1] = mCharts[1].getName();
      mMenuSummary[1] = mCharts[1].getDesc();
    */
    
    
    setListAdapter(new SimpleAdapter(this, getListValues(), android.R.layout.simple_list_item_2,
        new String[] { IChart.NAME, IChart.DESC }, new int[] { android.R.id.text1,
            android.R.id.text2 }));
  }

  private List<Map<String, String>> getListValues() {
    List<Map<String, String>> values = new ArrayList<Map<String, String>>();
    int length = mMenuText.length;
    for (int i = 0; i < length; i++) {
      Map<String, String> v = new HashMap<String, String>();
      v.put(IChart.NAME, mMenuText[i]);
      v.put(IChart.DESC, mMenuSummary[i]);
      values.add(v);
    }
    return values;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Intent intent = null;
    /*
    if (position == 0) {
      intent = new Intent(this, XYChartBuilder.class);
    } else if (position <= mCharts.length) {
    */
    HappyData datahelper = new HappyData(this);
	ArrayList<HappyBottle> plottables = datahelper.getAllHistory();
	
	this.chartline = lineTrace(plottables);
    
      intent = mCharts[0].execute(this);
    /*
  	} else {
      intent = new Intent(this, GeneratedChartDemo.class);
    }
    */
    startActivity(intent);
  }
  
  public ArrayList<Integer> lineTrace(ArrayList<HappyBottle> plottables){
	   Iterator<HappyBottle> itr = plottables.iterator(); 
	   int trace = 0;
	   ArrayList<Integer> traceline = new ArrayList<Integer>();
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
}