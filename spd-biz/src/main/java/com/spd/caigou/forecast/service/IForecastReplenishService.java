package com.spd.caigou.forecast.service;

import com.spd.caigou.forecast.domain.ForecastCalcRequest;
import com.spd.caigou.forecast.domain.ForecastEntryUpdateBody;
import com.spd.caigou.forecast.domain.ForecastGeneratePlanBody;
import com.spd.caigou.forecast.domain.PurchaseForecastTask;

import java.util.List;
import java.util.Map;

/**
 * 采购预测补货
 */
public interface IForecastReplenishService {

    List<PurchaseForecastTask> selectTaskList(PurchaseForecastTask query);

    PurchaseForecastTask selectTaskDetail(Long id);

    PurchaseForecastTask calc(ForecastCalcRequest request);

    void updateEntries(ForecastEntryUpdateBody body);

    Map<String, Object> generatePlan(ForecastGeneratePlanBody body);
}
