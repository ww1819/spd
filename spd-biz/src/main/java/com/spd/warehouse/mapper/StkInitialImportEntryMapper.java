package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.StkInitialImportEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 期初库存导入明细表 Mapper
 *
 * @author spd
 */
@Mapper
@Repository
public interface StkInitialImportEntryMapper {

    List<StkInitialImportEntry> selectByParenId(String parenId);

    int insert(StkInitialImportEntry entry);

    int insertBatch(List<StkInitialImportEntry> list);
}
