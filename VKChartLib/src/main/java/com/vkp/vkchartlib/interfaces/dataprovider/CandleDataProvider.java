package com.vkp.vkchartlib.interfaces.dataprovider;

import com.vkp.vkchartlib.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
