package com.spd.common.utils.rule;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.utils.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

/**
 * 规则值自动生成工具类
 *
 * @author spd
 * @举例： 自动生成订单号；自动生成当前日期
 */
public class FillRuleUtil {
    private static final int count = 0 ; //总数量：一般在实际业务中是需要在数据库中获取到当前的数据总数量
    private static final String STR_FORMAT = "00000"; //需要格式化的流水号规则

    public static String getFourPipelineNumbers() {
        // 所要获取的流水编码code
        StringBuffer code = new StringBuffer();
        // 设定所需的时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddd");
        // 获取当前时间并转化成所要的格式
        String dateStr = sdf.format(new Date()).toString();
        // 将格式化好的时间拼接到code上
        code.append(dateStr);
        // 根据格式化好的String类型的时间，查询数据库中当天所产生的单据数量生成流水号
        // 这里的count是需要在数据库中根据当天的日期,查询“单据”的创建时间符合条件的数量
        int num = count + 1;
        DecimalFormat dft = new DecimalFormat(STR_FORMAT);
        // 将获取到的数量按照所需的格式进行格式化
        String strNum = dft.format(num);
        // 因为code的类型是StingBuffer，所以要将其转换成String类型
        String autoCode = code.append(strNum).toString();
        return autoCode;
    }

    public static String getNumber(String str,String maxNum,String date) {
        String newNum = "";
        int beginIndex = str.length();
        if (StringUtils.isNotBlank(maxNum)) {
            maxNum = maxNum.substring(beginIndex);
            newNum = date + String.format("%05d",(Integer.parseInt(maxNum.substring(8)) + 1));
        }else {
            newNum = date + "00001";
        }
        return str + newNum ;
    }

    public static String getDateNum(){
        // 获取当前时间
        LocalDate now = LocalDate.now();
        // 格式化当前时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String prefix = now.format(formatter);
        return prefix;
    }

    public static String createBatchNo() {
        // 批次号第一部分：时间
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTimeStr = dateFormat.format(new Date());

        // 批次号第二部分：随机数
        Random random = new Random();
        Integer cusCode = random.nextInt(900000) + 100000;
        String cusCodeStr = cusCode.toString();

        // 返回分配批次
        return currentTimeStr + cusCodeStr;
    }

}
