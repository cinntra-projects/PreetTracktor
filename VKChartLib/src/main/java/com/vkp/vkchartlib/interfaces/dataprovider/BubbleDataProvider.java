package com.vkp.vkchartlib.interfaces.dataprovider;

import com.vkp.vkchartlib.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
