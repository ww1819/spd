package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzPatientInfo;

/**
 * 患者信息Service接口
 *
 * @author spd
 * @date 2025-01-01
 */
public interface IGzPatientInfoService
{
    /**
     * 查询患者信息
     *
     * @param id 患者信息主键
     * @return 患者信息
     */
    public GzPatientInfo selectGzPatientInfoById(Long id);

    /**
     * 根据病历号查询患者信息
     *
     * @param medicalRecordNo 病历号
     * @return 患者信息
     */
    public GzPatientInfo selectGzPatientInfoByMedicalRecordNo(String medicalRecordNo);

    /**
     * 查询患者信息列表
     *
     * @param gzPatientInfo 患者信息
     * @return 患者信息集合
     */
    public List<GzPatientInfo> selectGzPatientInfoList(GzPatientInfo gzPatientInfo);

    /**
     * 新增患者信息
     *
     * @param gzPatientInfo 患者信息
     * @return 结果
     */
    public int insertGzPatientInfo(GzPatientInfo gzPatientInfo);

    /**
     * 修改患者信息
     *
     * @param gzPatientInfo 患者信息
     * @return 结果
     */
    public int updateGzPatientInfo(GzPatientInfo gzPatientInfo);

    /**
     * 批量删除患者信息
     *
     * @param ids 需要删除的患者信息主键集合
     * @return 结果
     */
    public int deleteGzPatientInfoByIds(Long[] ids);

    /**
     * 删除患者信息信息
     *
     * @param id 患者信息主键
     * @return 结果
     */
    public int deleteGzPatientInfoById(Long id);
}
