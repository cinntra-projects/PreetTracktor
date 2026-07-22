package com.vkp.vkchartlib.interfaces.dataprovider;

import com.vkp.vkchartlib.components.YAxis.AxisDependency;
import com.vkp.vkchartlib.data.BarLineScatterCandleBubbleData;
import com.vkp.vkchartlib.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
