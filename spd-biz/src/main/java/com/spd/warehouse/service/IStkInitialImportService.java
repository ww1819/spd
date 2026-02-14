package com.spd.warehouse.service;

import com.spd.common.core.domain.AjaxResult;
import com.spd.warehouse.domain.StkInitialImport;
import com.spd.warehouse.domain.dto.InitialImportExcelRow;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 期初库存导入 Service
 *
 * @author spd
 */
public interface IStkInitialImportService {

    /**
     * 预览：解析 Excel，不落库，返回解析结果供前端确认
     */
    AjaxResult preview(MultipartFile file, Long warehouseId);

    /**
     * 确认导入：根据预览数据生成期初处理单（主表+明细），自动生成批次号，不生成批次对象与库存
     */
    AjaxResult confirmImport(Long warehouseId, List<InitialImportExcelRow> rows);

    /**
     * 分页列表
     */
    List<StkInitialImport> list(StkInitialImport query);

    /**
     * 详情（含明细）
     */
    StkInitialImport getDetail(String id);

    /**
     * 审核：生成批次对象、批次字典、库存明细、仓库流水（lx=QC）
     */
    int audit(String id);
}
