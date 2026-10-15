package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.common.ResultCode;
import com.hospital.dto.PrescriptionDTO;
import com.hospital.entity.Appointment;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;
import com.hospital.mapper.AppointmentMapper;
import com.hospital.mapper.PrescriptionItemMapper;
import com.hospital.mapper.PrescriptionMapper;
import com.hospital.service.PrescriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 处方服务实现类
 */
@Slf4j
@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createPrescription(PrescriptionDTO dto) {
        // 1. 校验挂号记录
        Appointment appt = appointmentMapper.selectById(dto.getApptId());
        if (appt == null) {
            return Result.error(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        if (!"就诊中".equals(appt.getVisitStatus()) && !"已就诊".equals(appt.getVisitStatus())) {
            return Result.error("患者尚未就诊，无法开具处方");
        }

        // 2. 计算处方总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PrescriptionDTO.PrescriptionItemDTO item : dto.getItems()) {
            BigDecimal subtotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        // 3. 创建处方
        Prescription prescription = new Prescription();
        prescription.setApptId(dto.getApptId());
        prescription.setPatientId(dto.getPatientId());
        prescription.setDoctorId(dto.getDoctorId());
        prescription.setDeptId(appt.getDeptId());
        prescription.setPrescNo(generatePrescNo());
        prescription.setPrescType(dto.getPrescType());
        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setChiefComplaint(dto.getChiefComplaint());
        prescription.setPresentIllness(dto.getPresentIllness());
        prescription.setTotalAmount(totalAmount);
        prescription.setStatus("待缴费");
        prescription.setRemark(dto.getRemark());
        prescription.setCreateTime(LocalDateTime.now());
        prescriptionMapper.insert(prescription);

        // 4. 保存处方明细
        for (PrescriptionDTO.PrescriptionItemDTO itemDTO : dto.getItems()) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescId(prescription.getPrescId());
            item.setMedicineId(itemDTO.getMedicineId());
            item.setMedicineName(itemDTO.getMedicineName());
            item.setSpecification(itemDTO.getSpecification());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnit(itemDTO.getUnit());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setSubtotal(itemDTO.getUnitPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            item.setUsageMethod(itemDTO.getUsageMethod());
            item.setDosage(itemDTO.getDosage());
            item.setFrequency(itemDTO.getFrequency());
            item.setDays(itemDTO.getDays());
            prescriptionItemMapper.insert(item);
        }

        log.info("开具处方成功: prescId={}, apptId={}, amount={}",
                prescription.getPrescId(), dto.getApptId(), totalAmount);
        return Result.success("处方开具成功", prescription.getPrescId());
    }

    @Override
    public Result<Prescription> getPrescriptionDetail(String prescId) {
        Prescription prescription = prescriptionMapper.selectById(prescId);
        if (prescription == null) {
            return Result.error(ResultCode.PRESCRIPTION_NOT_EXIST);
        }
        return Result.success(prescription);
    }

    @Override
    public Result<List<PrescriptionItem>> getPrescriptionItems(String prescId) {
        List<PrescriptionItem> items = prescriptionItemMapper.selectByPrescId(prescId);
        return Result.success(items);
    }

    @Override
    public Result<Page<Prescription>> getPatientPrescriptions(String patientId, String status,
                                                                Integer page, Integer size) {
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prescription::getPatientId, patientId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Prescription::getStatus, status);
        }
        wrapper.orderByDesc(Prescription::getCreateTime);

        Page<Prescription> pageParam = new Page<>(page, size);
        Page<Prescription> result = prescriptionMapper.selectPage(pageParam, wrapper);
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelPrescription(String prescId, String reason) {
        Prescription prescription = prescriptionMapper.selectById(prescId);
        if (prescription == null) {
            return Result.error(ResultCode.PRESCRIPTION_NOT_EXIST);
        }
        if ("已缴费".equals(prescription.getStatus()) || "已取药".equals(prescription.getStatus())) {
            return Result.error("已缴费或已取药的处方不能作废，请走退费流程");
        }

        prescription.setStatus("已作废");
        prescription.setCancelTime(LocalDateTime.now());
        prescription.setCancelReason(reason);
        prescriptionMapper.updateById(prescription);

        log.info("作废处方: prescId={}, reason={}", prescId, reason);
        return Result.success("处方已作废", null);
    }

    /**
     * 生成处方编号
     */
    private String generatePrescNo() {
        return "RX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }
}
