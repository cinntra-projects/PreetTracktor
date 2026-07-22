package com.vkp.vkchartlib.interfaces.dataprovider;

import com.vkp.vkchartlib.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
