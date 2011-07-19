package org.jbs.happysad;

import android.content.Context;
import android.content.Intent;
/*
 * Charts Interface.
 */
public interface ChartInterface {
  public String NAME = "name";
  public String DESC = "desc";
  public String getName();
  public String getDesc();
  public Intent execute(Context context);
}