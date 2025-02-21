package com.spd.yj.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.warehouse.service.IStkIoStocktakingService;
import com.spd.yj.vo.MonthInitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 月结初始化Controller
 *
 */
@RestController
@RequestMapping("/warehouse/yj")
public class MonthInitController extends BaseController
{
    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    @Autowired
    private IStkIoStocktakingService stkIoStocktakingService;

    /**
     * 查询月结初始化列表
     */
    @GetMapping("/monthInitDataList")
    public List<MonthInitVo> monthInitDataList(@RequestParam(value = "beginDate",required = false) String beginDate, @RequestParam(value = "endDate",required = false) String endDate,
                                               @RequestParam(value = "toStatDate",required = false) String toStatDate,
                                               @RequestParam(value = "toEndDate",required = false) String toEndDate)
    {
        List<MonthInitVo> monthInitVos = new ArrayList<MonthInitVo>();

        if(beginDate != null && endDate != null && toStatDate != null && toEndDate != null){
            List<Map<String, Object>> mapList = stkIoBillService.
                    selectMonthInitDataList(beginDate,endDate,toStatDate,toEndDate);

            for(Map<String, Object> map : mapList){
                MonthInitVo monthInitVo = new MonthInitVo();
                monthInitVo.setwCategoryName(map.get("wCategoryName").toString());
                monthInitVo.setInitAmont((BigDecimal) map.get("initAmount"));
                monthInitVo.setBeginAmount((BigDecimal) map.get("beginAmount"));
                monthInitVo.setEndAmount((BigDecimal) map.get("endAmount"));
                monthInitVo.setSettleAmount((BigDecimal) map.get("settleAmount"));
                monthInitVo.setProfitAmount((BigDecimal) map.get("profitAmount"));
                monthInitVo.setLoseAmount((BigDecimal) map.get("loseAmount"));
                monthInitVo.setSettleRealityAmount((BigDecimal) map.get("settleRealityAmount"));
                monthInitVos.add(monthInitVo);
            }
        }

        return monthInitVos;
    }

    /**
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/monthHandleData")
    public AjaxResult monthHandleData(@RequestParam(value = "beginDate",required = false) String beginDate,
                                      @RequestParam(value = "endDate",required = false) String endDate)
    {
        boolean flag = false;
        if(beginDate != null && endDate != null){
            List<StkIoBill> dataList = stkIoBillService.getMonthHandleDataList(beginDate,endDate);
            List<StkIoStocktaking> stockDataList = stkIoStocktakingService.getMonthHandleDataList(beginDate,endDate);

            for(StkIoBill data : dataList){
                StkIoBill stkIoBill = stkIoBillService.selectStkIoBillById(data.getId());

                stkIoBill.setIsMonthInit(1);//设为已月结
                stkIoBill.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                stkIoBill.setUpdateTime(new Date());
                stkIoBillService.updateStkIoBill(stkIoBill);
            }

            for(StkIoStocktaking data : stockDataList){
                StkIoStocktaking stkIoStocktaking = stkIoStocktakingService.selectStkIoStocktakingById(data.getId());
                stkIoStocktaking.setIsMonthInit(1);
                stkIoStocktaking.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                stkIoStocktaking.setUpdateTime(new Date());
                stkIoStocktakingService.updateStkIoStocktaking(stkIoStocktaking);
            }

            if(dataList.size() > 0 || stockDataList.size() > 0){
                flag = true;
            }
        }

        if(flag){
            return AjaxResult.success("操作成功",200);
        }else{
            return AjaxResult.warn("暂无数据");
        }
    }

    /**
     * 清除月结
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/monthClearData")
    public AjaxResult monthClearData(@RequestParam(value = "beginDate",required = false) String beginDate,
                                      @RequestParam(value = "endDate",required = false) String endDate)
    {
        boolean isFlag = false;
        if(beginDate != null && endDate != null){
            List<StkIoBill> dataList = stkIoBillService.getMonthHandleDataList(beginDate,endDate);
            List<StkIoStocktaking> stockDataList = stkIoStocktakingService.getMonthHandleDataList(beginDate,endDate);

            for(StkIoBill data : dataList){
                StkIoBill stkIoBill = stkIoBillService.selectStkIoBillById(data.getId());

                stkIoBill.setIsMonthInit(0);//清除
                stkIoBill.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                stkIoBill.setUpdateTime(new Date());
                stkIoBillService.updateStkIoBill(stkIoBill);
            }

            for(StkIoStocktaking data : stockDataList){
                StkIoStocktaking stkIoStocktaking = stkIoStocktakingService.selectStkIoStocktakingById(data.getId());
                stkIoStocktaking.setIsMonthInit(0);
                stkIoStocktaking.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                stkIoStocktaking.setUpdateTime(new Date());
                stkIoStocktakingService.updateStkIoStocktaking(stkIoStocktaking);
            }

            if(dataList.size() > 0 || stockDataList.size() > 0){
                isFlag = true;
            }
        }

        if(isFlag){
            return AjaxResult.success("操作成功",200);
        }else{
            return AjaxResult.warn("暂无数据");
        }
    }
}
