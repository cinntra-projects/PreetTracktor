package com.vkp.vkchartlib.interfaces.dataprovider;

import com.vkp.vkchartlib.components.YAxis;
import com.vkp.vkchartlib.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
