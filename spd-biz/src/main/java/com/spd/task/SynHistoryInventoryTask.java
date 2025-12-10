package com.spd.task;

import com.spd.warehouse.service.IStkIoBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 同步每天的历史库存
 */
@Component("SynHistoryInventoryTask")
public class SynHistoryInventoryTask {

    protected Logger logger = LoggerFactory.getLogger(SynHistoryInventoryTask.class);

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    public void Task(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //当前日期
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        //将日期向前推一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        //获取前一天的日期
        Date previousDate = calendar.getTime();

        //格式化前一天的日期
        String previousDateString = sdf.format(previousDate);

//        IStkIoBillService iStkIoBillService = SpringUtils.getBean(IStkIoBillService.class);
//        StkIoBill stkIoBill = iStkIoBillService.selectStkIoBillById(Long.valueOf(15));

        List<Map<String, Object>> mapList = stkIoBillService.selectHistoryInventory(previousDateString);
        for(Map<String, Object> map : mapList){
            //插入到历史库存表中

            logger.debug("处理历史库存数据: {}", map);
        }
    }

}
