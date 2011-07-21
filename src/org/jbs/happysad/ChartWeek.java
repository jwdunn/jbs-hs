package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.achartengine.ChartFactory;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class ChartWeek extends AbstractChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Happy Pie-Chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "This chart shows the percentages of your happiness in a pie chart format.";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
	  
	HappyData datahelper = new HappyData(context);
	ArrayList<HappyBottle> plottables = datahelper.getMyHistory();
	
	//Testers.
	int x = test(plottables);
    String y = Integer.toString(x);
	
	double[] values = percentages(plottables);
    	//new double[] { 12, 14, 11, 10, 19 };
    int[] colors = new int[] { Color.YELLOW, Color.CYAN };
    
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setZoomButtonsVisible(true);
    renderer.setZoomEnabled(true);
    //renderer.setChartTitleTextSize(20);
    
    renderer.setChartTitle("THIS WEEK");
    
    renderer.setChartTitleTextSize(50);
	renderer.setLabelsTextSize(20);
	renderer.setLegendTextSize(40);
	renderer.setMargins(new int[] {20, 30});
    
    return ChartFactory.getPieChartIntent(context, buildCategoryDataset("Happy Pie", values),
        renderer, "MyChart");
  }
  
  public double [] percentages(ArrayList<HappyBottle> plottables){
		
	  	Iterator<HappyBottle> itr = plottables.iterator(); 
		
	  	double happy = 0;
		double sad = 0;
		double [] values = new double[2];
		boolean greek_salad = true;
		boolean breaker = false;
		int x = 0;
		int y = 0;
		int z = 0;
		int w = 0;
	
		HappyBottle element = itr.next();
		int main = new Timestamp (element.getTime()).getDay();
		//*
		//int main2 = new Timestamp (element.getTime()).getDate();
		
		if (element.getEmo() == 1){
	    	happy += 1; 
	     } else {
	    	sad += 1; 
	     }	
		
		for (int i = main; i >= 0; i--){
			
			while(itr.hasNext()) {     
			   	
				if (greek_salad){
					
					element = itr.next();
					x = new Timestamp (element.getTime()).getDay();
					//*
					y = new Timestamp (element.getTime()).getMonth() + 1;
					z = new Timestamp (element.getTime()).getDate();
				}
				
				//*
				if (y == 4 || y == 6 || y == 9 || y == 11){
					
					w = 25;
					
				} else if (y == 2){
					
					w = 23;
				
				} else {
					
					w = 26;
				}
				
				int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
				int date = Calendar.getInstance().get(Calendar.DATE);
				
				//*
				if (y == month){
					
					if (!(z == date || z == date - 1 || z == date - 2 || z == date - 3 || z == date - 4 || z == date - 5 || z == date - 6)){
						
						breaker = true;
						break;
					}
					
				} else if (y == (month - 1)){
					
					if (z < w){
						
						breaker = true;
						break;
					}
					
				} else {
					
					break;
				}
	
				if (i == x){
			   		
			   		if (element.getEmo() == 1){
				    	happy += 1; 
				     } else {
				    	sad += 1; 
				     }	
			   		greek_salad = true;
			   	
				} else {
					
					greek_salad = false;
					break;
				}		
			}
			
			if (breaker){
				
				break;
			}
		}
		 
		double happyprctg = (happy * 100) / (happy + sad);
		double sadprctg = 100 - happyprctg;
		
		int happytransf = (int) (happyprctg * 100);
		int sadtrans = (int) (sadprctg * 100);
		
		happyprctg = (double) happytransf / 100;
		sadprctg = (double) sadtrans / 100;
		
		values[0] = happyprctg;
		values[1] = sadprctg;
		
		return values;
	}
  
  //Tester.
  public int test(ArrayList<HappyBottle> plottables){
	  
	  Iterator<HappyBottle> itr = plottables.iterator(); 
   
	  HappyBottle element = itr.next();
			
	  int x = new Timestamp (element.getTime()).getDate();
	  
	  return x;
  }
}