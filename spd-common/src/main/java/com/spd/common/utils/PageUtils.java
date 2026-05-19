package com.spd.common.utils;

import com.github.pagehelper.PageHelper;
import com.spd.common.core.page.PageDomain;
import com.spd.common.core.page.TableSupport;
import com.spd.common.utils.sql.SqlUtil;
import com.spd.common.utils.StringUtils;

/**
 * 分页工具类
 *
 * @author spd
 */
public class PageUtils extends PageHelper
{
    /**
     * 设置请求分页数据
     */
    public static void startPage()
    {
        startPage(true);
    }

    /**
     * @param doCount false 时不执行 PageHelper 自动 count（由业务侧单独 count 后 setTotal）
     */
    public static void startPage(boolean doCount)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        if (doCount)
        {
            PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
        }
        else
        {
            PageHelper.startPage(pageNum, pageSize, false).setReasonable(reasonable);
            if (StringUtils.isNotEmpty(orderBy))
            {
                PageHelper.orderBy(orderBy);
            }
        }
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }
}
