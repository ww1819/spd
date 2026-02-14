package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.StkInitialImport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 期初库存导入主表 Mapper
 *
 * @author spd
 */
@Mapper
@Repository
public interface StkInitialImportMapper {

    StkInitialImport selectById(String id);

    List<StkInitialImport> selectList(StkInitialImport query);

    int insert(StkInitialImport record);

    int update(StkInitialImport record);

    String selectMaxBillNo(@Param("datePrefix") String datePrefix);
}
