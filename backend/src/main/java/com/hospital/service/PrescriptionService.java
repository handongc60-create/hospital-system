package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.dto.PrescriptionDTO;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;

import java.util.List;

/**
 * 处方服务接口
 */
public interface PrescriptionService {

    /**
     * 开具处方
     *
     * @param prescriptionDTO 处方请求
     * @return 处方ID
     */
    Result<String> createPrescription(PrescriptionDTO prescriptionDTO);

    /**
     * 查询处方详情
     *
     * @param prescId 处方ID
     * @return 处方详情
     */
    Result<Prescription> getPrescriptionDetail(String prescId);

    /**
     * 查询处方明细
     *
     * @param prescId 处方ID
     * @return 明细列表
     */
    Result<List<PrescriptionItem>> getPrescriptionItems(String prescId);

    /**
     * 分页查询患者处方列表
     *
     * @param patientId 患者ID
     * @param status    处方状态（可选）
     * @param page      页码
     * @param size      每页大小
     * @return 处方分页
     */
    Result<Page<Prescription>> getPatientPrescriptions(String patientId, String status,
                                                         Integer page, Integer size);

    /**
     * 作废处方
     *
     * @param prescId 处方ID
     * @param reason  作废原因
     * @return 操作结果
     */
    Result<Void> cancelPrescription(String prescId, String reason);
}
