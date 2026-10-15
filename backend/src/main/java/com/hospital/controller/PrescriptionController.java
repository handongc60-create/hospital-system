package com.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.dto.PrescriptionDTO;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;
import com.hospital.service.PrescriptionService;
import com.hospital.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 处方控制器
 */
@Api(tags = "处方管理")
@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("开具处方")
    @PostMapping("/create")
    public Result<String> createPrescription(@Valid @RequestBody PrescriptionDTO dto,
                                              HttpServletRequest request) {
        String doctorId = getUserId(request);
        dto.setDoctorId(doctorId);
        return prescriptionService.createPrescription(dto);
    }

    @ApiOperation("查询处方详情")
    @GetMapping("/{prescId}")
    public Result<Prescription> getPrescriptionDetail(@PathVariable String prescId) {
        return prescriptionService.getPrescriptionDetail(prescId);
    }

    @ApiOperation("查询处方明细")
    @GetMapping("/{prescId}/items")
    public Result<List<PrescriptionItem>> getPrescriptionItems(@PathVariable String prescId) {
        return prescriptionService.getPrescriptionItems(prescId);
    }

    @ApiOperation("查询我的处方列表")
    @GetMapping("/my")
    public Result<Page<Prescription>> getMyPrescriptions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        String patientId = getPatientId(request);
        return prescriptionService.getPatientPrescriptions(patientId, status, page, size);
    }

    @ApiOperation("作废处方")
    @PostMapping("/cancel/{prescId}")
    public Result<Void> cancelPrescription(@PathVariable String prescId,
                                            @RequestParam String reason) {
        return prescriptionService.cancelPrescription(prescId, reason);
    }

    private String getUserId(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader(jwtUtil.getHeader()));
        return token != null ? jwtUtil.getUserIdFromToken(token) : null;
    }

    private String getPatientId(HttpServletRequest request) {
        return getUserId(request);
    }
}
