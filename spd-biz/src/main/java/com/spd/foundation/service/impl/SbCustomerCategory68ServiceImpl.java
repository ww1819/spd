package com.spd.foundation.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdCategory68;
import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.domain.SbCustomerCategory68Log;
import com.spd.foundation.mapper.FdCategory68Mapper;
import com.spd.foundation.mapper.SbCustomerCategory68LogMapper;
import com.spd.foundation.mapper.SbCustomerCategory68Mapper;
import com.spd.foundation.service.ISbCustomerCategory68Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户 68 分类（sb_customer_category68）实现：初始化/同步时以系统标准表 {@code fd_category68} 为蓝本
 * （该表为全库共用字典模板，无 tenant_id）；本服务内数据按 {@code customerId} 隔离。
 *
 * @author spd
 */
@Service
public class SbCustomerCategory68ServiceImpl implements ISbCustomerCategory68Service {

    @Autowired
    private FdCategory68Mapper fdCategory68Mapper;
    @Autowired
    private SbCustomerCategory68Mapper sbCustomerCategory68Mapper;
    @Autowired
    private SbCustomerCategory68LogMapper sbCustomerCategory68LogMapper;

    private static final String OP_ADD = "add";
    private static final String OP_UPDATE = "update";
    private static final String OP_DELETE = "delete";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initForCustomer(String customerId) {
        if (StringUtils.isEmpty(customerId)) return;
        List<FdCategory68> standardList = fdCategory68Mapper.selectFdCategory68List(new FdCategory68());
        if (standardList == null || standardList.isEmpty()) return;
        String createBy = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        // ref_category68_id -> 本表 id，用于设置 parent_id（父记录的本表 id）
        Map<Long, String> refToIdMap = new HashMap<>();
        for (FdCategory68 std : standardList) {
            if (std.getDelFlag() != null && std.getDelFlag() == 1) continue;
            Long refId = std.getCategory68Id();
            Long stdParentId = std.getParentId();
            String parentIdStr = null;
            if (stdParentId != null && stdParentId != 0) {
                parentIdStr = refToIdMap.get(stdParentId);
            }
            SbCustomerCategory68 row = new SbCustomerCategory68();
            row.setId(UUID7.generateUUID7());
            row.setCustomerId(customerId);
            row.setRefCategory68Id(refId);
            row.setParentId(parentIdStr);
            row.setCategory68Code(std.getCategory68Code());
            row.setCategory68Name(std.getCategory68Name());
            row.setNamePinyin(PinyinUtils.getPinyinInitials(std.getCategory68Name()));
            row.setDelFlag(0);
            row.setCreateBy(createBy);
            row.setCreateTime(now);
            row.setUpdateBy(createBy);
            row.setUpdateTime(now);
            sbCustomerCategory68Mapper.insert(row);
            refToIdMap.put(refId, row.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncFromStandard(String customerId) {
        if (StringUtils.isEmpty(customerId)) return;
        List<FdCategory68> standardList = fdCategory68Mapper.selectFdCategory68List(new FdCategory68());
        if (standardList == null || standardList.isEmpty()) return;
        SbCustomerCategory68 query = new SbCustomerCategory68();
        query.setCustomerId(customerId);
        query.setDelFlag(0);
        List<SbCustomerCategory68> existingList = sbCustomerCategory68Mapper.selectList(query);
        // ref_category68_id -> 本表 id（用于 parent_id）
        Map<Long, String> refToIdMap = new HashMap<>();
        if (existingList != null) {
            for (SbCustomerCategory68 e : existingList) {
                if (e.getRefCategory68Id() != null) refToIdMap.put(e.getRefCategory68Id(), e.getId());
            }
        }
        String operateBy = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        for (FdCategory68 std : standardList) {
            if (std.getDelFlag() != null && std.getDelFlag() == 1) continue;
            Long refId = std.getCategory68Id();
            Long stdParentId = std.getParentId();
            String parentIdStr = null;
            if (stdParentId != null && stdParentId != 0) parentIdStr = refToIdMap.get(stdParentId);
            SbCustomerCategory68 existing = sbCustomerCategory68Mapper.selectByCustomerIdAndRefId(customerId, refId);
            if (existing != null) {
                existing.setParentId(parentIdStr);
                existing.setCategory68Code(std.getCategory68Code());
                existing.setCategory68Name(std.getCategory68Name());
                existing.setNamePinyin(PinyinUtils.getPinyinInitials(std.getCategory68Name()));
                existing.setUpdateBy(operateBy);
                existing.setUpdateTime(now);
                sbCustomerCategory68Mapper.update(existing);
            } else {
                SbCustomerCategory68 row = new SbCustomerCategory68();
                row.setId(UUID7.generateUUID7());
                row.setCustomerId(customerId);
                row.setRefCategory68Id(refId);
                row.setParentId(parentIdStr);
                row.setCategory68Code(std.getCategory68Code());
                row.setCategory68Name(std.getCategory68Name());
                row.setNamePinyin(PinyinUtils.getPinyinInitials(std.getCategory68Name()));
                row.setDelFlag(0);
                row.setCreateBy(operateBy);
                row.setCreateTime(now);
                row.setUpdateBy(operateBy);
                row.setUpdateTime(now);
                sbCustomerCategory68Mapper.insert(row);
                refToIdMap.put(refId, row.getId());
            }
        }
    }

    @Override
    public List<SbCustomerCategory68> selectList(SbCustomerCategory68 query) {
        return sbCustomerCategory68Mapper.selectList(query);
    }

    @Override
    public List<SbCustomerCategory68> selectTree(String customerId) {
        List<SbCustomerCategory68> list = sbCustomerCategory68Mapper.selectTreeByCustomerId(customerId);
        return buildTree(list, null);
    }

    private List<SbCustomerCategory68> buildTree(List<SbCustomerCategory68> list, String parentId) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return list.stream()
            .filter(e -> isChildOf(e, parentId))
            .peek(e -> e.setChildren(buildTree(list, e.getId())))
            .collect(Collectors.toList());
    }

    private boolean isChildOf(SbCustomerCategory68 e, String parentId) {
        if (parentId == null) {
            return e.getParentId() == null || (e.getParentId() != null && e.getParentId().isEmpty());
        }
        return e.getParentId() != null && e.getParentId().equals(parentId);
    }

    @Override
    public SbCustomerCategory68 selectById(String id) {
        return sbCustomerCategory68Mapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(SbCustomerCategory68 row) {
        if (StringUtils.isEmpty(row.getCustomerId())) {
            throw new ServiceException("客户ID不能为空");
        }
        if (row.getRefCategory68Id() == null) {
            throw new ServiceException("对应标准68分类ID不能为空");
        }
        SbCustomerCategory68 exist = sbCustomerCategory68Mapper.selectByCustomerIdAndRefId(row.getCustomerId(), row.getRefCategory68Id());
        if (exist != null) {
            throw new ServiceException("该客户下已存在对应标准68分类记录，请勿重复新增");
        }
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) row.setCreateBy(SecurityUtils.getUserIdStr());
        int n = sbCustomerCategory68Mapper.insert(row);
        saveLog(row.getCustomerId(), row.getId(), row.getRefCategory68Id(), OP_ADD, null, toSummary(row));
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SbCustomerCategory68 row) {
        if (StringUtils.isEmpty(row.getId())) {
            throw new ServiceException("主键不能为空");
        }
        SbCustomerCategory68 old = sbCustomerCategory68Mapper.selectById(row.getId());
        if (old == null) {
            throw new ServiceException("客户68分类记录不存在");
        }
        if (StringUtils.isEmpty(row.getCustomerId())) {
            row.setCustomerId(old.getCustomerId());
        }
        String summaryOld = toSummary(old);
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) row.setUpdateBy(SecurityUtils.getUserIdStr());
        int n = sbCustomerCategory68Mapper.update(row);
        saveLog(old.getCustomerId(), row.getId(), old.getRefCategory68Id(), OP_UPDATE, summaryOld, toSummary(row));
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(String id) {
        SbCustomerCategory68 old = sbCustomerCategory68Mapper.selectById(id);
        if (old == null) {
            throw new ServiceException("客户68分类记录不存在");
        }
        SbCustomerCategory68 row = new SbCustomerCategory68();
        row.setId(id);
        row.setDelFlag(1);
        row.setDelBy(SecurityUtils.getUserIdStr());
        row.setDelTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDelBy());
        row.setUpdateTime(row.getDelTime());
        int n = sbCustomerCategory68Mapper.update(row);
        saveLog(old.getCustomerId(), id, old.getRefCategory68Id(), OP_DELETE, toSummary(old), null);
        return n;
    }

    @Override
    public List<SbCustomerCategory68Log> selectLogByCustomerId(String customerId) {
        return sbCustomerCategory68LogMapper.selectByCustomerId(customerId);
    }

    @Override
    public List<SbCustomerCategory68Log> selectLogByTargetId(String targetId) {
        return sbCustomerCategory68LogMapper.selectByTargetId(targetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePinyinForCustomer(String customerId) {
        if (StringUtils.isEmpty(customerId)) return;
        List<SbCustomerCategory68> list = sbCustomerCategory68Mapper.selectTreeByCustomerId(customerId);
        if (list == null || list.isEmpty()) return;
        String updateBy = SecurityUtils.getUserIdStr();
        Date updateTime = DateUtils.getNowDate();
        for (SbCustomerCategory68 row : list) {
            String pinyin = PinyinUtils.getPinyinInitials(row.getCategory68Name());
            row.setNamePinyin(pinyin);
            row.setUpdateBy(updateBy);
            row.setUpdateTime(updateTime);
            sbCustomerCategory68Mapper.updatePinyinById(row);
        }
    }

    private void saveLog(String customerId, String targetId, Long refCategory68Id, String operationType, String contentOld, String contentNew) {
        SbCustomerCategory68Log log = new SbCustomerCategory68Log();
        log.setId(UUID7.generateUUID7());
        log.setCustomerId(customerId);
        log.setTargetId(targetId);
        log.setRefCategory68Id(refCategory68Id);
        log.setOperationType(operationType);
        log.setContentOld(contentOld);
        log.setContentNew(contentNew);
        log.setOperateBy(SecurityUtils.getUserIdStr());
        log.setOperateTime(DateUtils.getNowDate());
        sbCustomerCategory68LogMapper.insert(log);
    }

    private static String toSummary(SbCustomerCategory68 r) {
        if (r == null) return null;
        return String.format("code=%s,name=%s", r.getCategory68Code(), r.getCategory68Name());
    }
}
