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
 * Week Pie Chart.
 */
public class ChartWeek extends AbstractChart {
  /**
   * Executes the chart.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
	  
	//Forms array of all personal(local) bottles.
	HappyData datahelper = new HappyData(context);
	ArrayList<HappyBottle> plottables = datahelper.getMyHistory();
	
	//Saves values.
	double[] values = percentages(plottables);
	
	//Saves colors.
    int[] colors = new int[] { Color.YELLOW, Color.CYAN };
    
    //Creates renderer and sets specifications.
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setZoomButtonsVisible(true);
    renderer.setZoomEnabled(true);
    renderer.setChartTitle("THIS WEEK");
    renderer.setChartTitleTextSize(50);
	renderer.setLabelsTextSize(20);
	renderer.setLegendTextSize(40);
	renderer.setMargins(new int[] {20, 30});
    
    return ChartFactory.getPieChartIntent(context, buildCategoryDataset("Happy Pie", values),
        renderer, "MyChart");
  }
  
  /**
   * Builds array with values per section of pie chart.
   * @param array of bottles
   * @return array of values for chart
   */
  public double [] percentages(ArrayList<HappyBottle> plottables){
		
	  	Iterator<HappyBottle> itr = plottables.iterator(); 
		
	  	//Variables.
	  	double happy = 0;
		double sad = 0;
		double [] values = new double[2];
		boolean revision = true;
		boolean breaker = false;
		boolean weekinit = true;
		int x = 0;
		int y = 0;
		int z = 0;
		int w = 0;
	
		//Call first bottle.
		HappyBottle element = itr.next();
		
		//Get day of week, date and month.
		int main1 = new Timestamp (element.getTime()).getDay();
		int main2 = new Timestamp (element.getTime()).getDate();
		int main3 = new Timestamp (element.getTime()).getMonth() + 1;
		
		//If the month has 30 days.
		if (main3 == 4 || main3 == 6 || main3 == 9 || main3 == 11){
			
			//Minimum value for date in the month if current day 
			//has changed of month but still is in the same week.
			w = 25;
		
		//If the month has 28 days.
		} else if (main3 == 2){
			
			w = 23;
		
		//If the month has 31 days.
		} else {
			
			w = 26;
		}
		
		//Get current month and date.
		int main_month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int main_date = Calendar.getInstance().get(Calendar.DATE);
		
		//Bottle's month == current month.
		if (main3 == main_month){
			
			//If dates are separated by more than 6 days; different weeks.
			if (!(main2 == main_date || main2 == main_date - 1 || main2 == main_date - 2 || main2 == main_date - 3 || main2 == main_date - 4 || main2 == main_date - 5 || main2 == main_date - 6)){
				
				//Disables the rest of the method.
				weekinit = false;
			}
			
		//If current month is one after the bottle's month.
		} else if (main3 == (main_month - 1)){
			
			//If date of bottle is lower than the minimum possible 
			//value for it to be within the current week.
			if (main2 < w){
				
				weekinit = false;
			}
			
		//If months don't match for a possibility of the days to be in the same week. 
		} else {
			
			weekinit = false;
		}
		
		//If disabler is off.
		if (weekinit){
			
			//Count first bottle.
			//If emotion = happy.
	   		if (element.getEmo() == 1){
	   			happy += 1; 
	   		//If emotion = sad.
		    } else {
		    	sad += 1; 
		    }
			
	   		//Uses first bottle's week day as a reference for the loop.
			for (int i = main1; i >= 0; i--){
				
				//If there are more bottles.
				while(itr.hasNext()) {     
				   	
					//If disabler is off.
					if (revision){
						
						//Call next bottle.
						element = itr.next();
						
						//Save new week day, month and date.
						x = new Timestamp (element.getTime()).getDay();
						y = new Timestamp (element.getTime()).getMonth() + 1;
						z = new Timestamp (element.getTime()).getDate();
					}
					
					//If the month has 30 days.
					if (y == 4 || y == 6 || y == 9 || y == 11){
						
						//Minimum value for date in the month if current day 
						//has changed of month but still is in the same week.
						w = 25;
						
					//If the month has 28 days.
					} else if (y == 2){
						
						w = 23;
					
					//If the month has 31 days.
					} else {
						
						w = 26;
					}
					
					//Get current month and date.
					int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
					int date = Calendar.getInstance().get(Calendar.DATE);
					
					//Bottle's month == current month.
					if (y == month){
						
						//If dates are separated by more than 6 days; different weeks.
						if (!(z == date || z == date - 1 || z == date - 2 || z == date - 3 || z == date - 4 || z == date - 5 || z == date - 6)){
							
							//Activate enabler and exit loop.
							breaker = true;
							break;
						}
					
					//If current month is one after the bottle's month.
					} else if (y == (month - 1)){
						
						//If date of bottle is lower than the minimum possible 
						//value for it to be within the current week.
						if (z < w){
							
							breaker = true;
							break;
						}
						
					//If months don't match for a possibility of the days to be in the same week.
					} else {
						
						break;
					}
					
					//If current tester week day matches current bottle's week day.
					if (i == x){
				   		
						//If emotion = happy.
				   		if (element.getEmo() == 1){
				   			happy += 1; 
				   		//If emotion = sad.
					    } else {
					    	sad += 1; 
					    }
				   		
				   		//Turn off disabler.
				   		revision = true;
				   	
				   	//If they don't match.
					} else {
						
						//Turn on disabler and exit loop.
						revision = false;
						break;
					}		
				}
				
				//If enabler is on.
				if (breaker){
					
					//Exit loop.
					break;
				}
			}
		}

		//Get percentages of happy's and sad's.
		double happyprctg = (happy * 100) / (happy + sad);
		double sadprctg = 100 - happyprctg;
		
		//Round them both to 2 decimals.
		int happytransf = (int) (happyprctg * 100);
		int sadtrans = (int) (sadprctg * 100);
		happyprctg = (double) happytransf / 100;
		sadprctg = (double) sadtrans / 100;
		
		//Add them to the array.
		values[0] = happyprctg;
		values[1] = sadprctg;
		
		return values;
	}
}