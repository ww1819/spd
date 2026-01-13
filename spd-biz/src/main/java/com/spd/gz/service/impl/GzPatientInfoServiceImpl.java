package com.spd.gz.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.gz.mapper.GzPatientInfoMapper;
import com.spd.gz.domain.GzPatientInfo;
import com.spd.gz.service.IGzPatientInfoService;

/**
 * 患者信息Service业务层处理
 *
 * @author spd
 * @date 2025-01-01
 */
@Service
public class GzPatientInfoServiceImpl implements IGzPatientInfoService
{
    @Autowired
    private GzPatientInfoMapper gzPatientInfoMapper;

    /**
     * 查询患者信息
     *
     * @param id 患者信息主键
     * @return 患者信息
     */
    @Override
    public GzPatientInfo selectGzPatientInfoById(Long id)
    {
        return gzPatientInfoMapper.selectGzPatientInfoById(id);
    }

    /**
     * 根据病历号查询患者信息
     *
     * @param medicalRecordNo 病历号
     * @return 患者信息
     */
    @Override
    public GzPatientInfo selectGzPatientInfoByMedicalRecordNo(String medicalRecordNo)
    {
        return gzPatientInfoMapper.selectGzPatientInfoByMedicalRecordNo(medicalRecordNo);
    }

    /**
     * 查询患者信息列表
     *
     * @param gzPatientInfo 患者信息
     * @return 患者信息
     */
    @Override
    public List<GzPatientInfo> selectGzPatientInfoList(GzPatientInfo gzPatientInfo)
    {
        return gzPatientInfoMapper.selectGzPatientInfoList(gzPatientInfo);
    }

    /**
     * 新增患者信息
     *
     * @param gzPatientInfo 患者信息
     * @return 结果
     */
    @Override
    public int insertGzPatientInfo(GzPatientInfo gzPatientInfo)
    {
        return gzPatientInfoMapper.insertGzPatientInfo(gzPatientInfo);
    }

    /**
     * 修改患者信息
     *
     * @param gzPatientInfo 患者信息
     * @return 结果
     */
    @Override
    public int updateGzPatientInfo(GzPatientInfo gzPatientInfo)
    {
        return gzPatientInfoMapper.updateGzPatientInfo(gzPatientInfo);
    }

    /**
     * 批量删除患者信息
     *
     * @param ids 需要删除的患者信息主键
     * @return 结果
     */
    @Override
    public int deleteGzPatientInfoByIds(Long[] ids)
    {
        return gzPatientInfoMapper.deleteGzPatientInfoByIds(ids);
    }

    /**
     * 删除患者信息信息
     *
     * @param id 患者信息主键
     * @return 结果
     */
    @Override
    public int deleteGzPatientInfoById(Long id)
    {
        return gzPatientInfoMapper.deleteGzPatientInfoById(id);
    }
}
