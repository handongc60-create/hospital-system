package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班Mapper接口
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 扣减号源（乐观锁）
     *
     * @param scheduleId 排班ID
     * @param version    版本号
     * @return 影响行数
     */
    @Update("UPDATE schedule SET remain_count = remain_count - 1, " +
            "booked_count = booked_count + 1, version = version + 1 " +
            "WHERE schedule_id = #{scheduleId} AND version = #{version} " +
            "AND remain_count > 0 AND status = 1 AND deleted = 0")
    int decreaseRemainCount(@Param("scheduleId") String scheduleId,
                             @Param("version") Integer version);

    /**
     * 增加号源（退号时调用）
     *
     * @param scheduleId 排班ID
     * @return 影响行数
     */
    @Update("UPDATE schedule SET remain_count = remain_count + 1, " +
            "booked_count = booked_count - 1, version = version + 1 " +
            "WHERE schedule_id = #{scheduleId} AND booked_count > 0 AND deleted = 0")
    int increaseRemainCount(@Param("scheduleId") String scheduleId);

    /**
     * 根据科室、日期范围查询排班列表
     */
    List<Schedule> selectByCondition(@Param("deptId") String deptId,
                                      @Param("doctorId") String doctorId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}
