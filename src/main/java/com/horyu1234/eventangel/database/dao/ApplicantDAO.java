package com.horyu1234.eventangel.database.dao;

import com.horyu1234.eventangel.database.mapper.ApplicantMapper;
import com.horyu1234.eventangel.domain.Applicant;
import com.horyu1234.eventangel.domain.DuplicatedApplicant;
import com.horyu1234.eventangel.factory.DateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by horyu on 2018-04-15
 */
@Repository
public class ApplicantDAO {
    private JdbcTemplate jdbcTemplate;
    private ApplicantMapper applicantMapper;

    @Autowired
    public ApplicantDAO(JdbcTemplate jdbcTemplate, ApplicantMapper applicantMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.applicantMapper = applicantMapper;
    }

    public void createTableIfNotExist() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `APPLICANT` ( " +
                "`EVENT_ID` INT(11) NOT NULL, " +
                "`APPLY_EMAIL` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "`YOUTUBE_NICKNAME` TEXT NOT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "`APPLY_TIME` DATETIME NOT NULL, " +
                "`IP_ADDRESS` TEXT NOT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "`REFERER` TEXT NOT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "`USER_AGENT` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "`FINGERPRINT2` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "`DUPLICATED_YN` VARCHAR(1) NOT NULL DEFAULT 'N' COLLATE 'utf8mb4_unicode_ci', " +
                "`DUP_CHECK_ADMIN_NAME` VARCHAR(20) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci', " +
                "PRIMARY KEY (`EVENT_ID`, `APPLY_EMAIL`) " +
                ") " +
                "COLLATE='utf8mb4_unicode_ci' " +
                "ENGINE=InnoDB;");
    }

    public int getApplicantCount(int eventId, boolean includeDuplicated) {
        String sql = "SELECT COUNT(*) " +
                "FROM `APPLICANT` " +
                "WHERE EVENT_ID = ?";
        if (!includeDuplicated) {
            sql += " AND DUPLICATED_YN = 'N'";
        }

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{eventId},
                Integer.class);
    }

    public int getApplicantCount(int eventId, String ip) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) " +
                        "FROM `APPLICANT` " +
                        "WHERE EVENT_ID = ? AND IP_ADDRESS = ?",
                new Object[]{eventId, ip},
                Integer.class);
    }

    public List<Applicant> getApplicant(int eventId, String email) {
        return jdbcTemplate.query(
                "SELECT * FROM `APPLICANT` " +
                        "WHERE EVENT_ID = ? AND APPLY_EMAIL = ?;",
                new Object[]{eventId, email},
                applicantMapper);
    }

    public List<Applicant> getApplicant(int eventId) {
        return jdbcTemplate.query(
                "SELECT * FROM `APPLICANT` " +
                        "WHERE EVENT_ID = ? " +
                        "ORDER BY APPLY_TIME " +
                        "DESC;",
                new Object[]{eventId},
                applicantMapper);
    }

    public List<Applicant> getApplicant(int eventId, String whereKey, String whereValue) {
        return jdbcTemplate.query(
                "SELECT * FROM `APPLICANT` " +
                        "WHERE EVENT_ID = ? AND " + whereKey + " = ? " +
                        "ORDER BY APPLY_TIME " +
                        "DESC;",
                new Object[]{eventId, whereValue},
                applicantMapper);
    }

    public List<DuplicatedApplicant> getDuplicatedApplicant(int eventId, String columnName) {
        return jdbcTemplate.query("SELECT " +
                        columnName + " AS columnName, " +
                        "COUNT(*) AS duplicatedCount " +
                        "FROM `APPLICANT` " +
                        "WHERE EVENT_ID = ? " +
                        "GROUP BY " + columnName + " " +
                        "HAVING duplicatedCount > 1 " +
                        "ORDER BY COUNT(*) DESC;",
                new Object[]{eventId},
                new BeanPropertyRowMapper<>(DuplicatedApplicant.class));
    }

    public void insertApplicant(Applicant applicant) {
        jdbcTemplate.update("INSERT INTO `APPLICANT` (EVENT_ID, APPLY_EMAIL, YOUTUBE_NICKNAME, APPLY_TIME, IP_ADDRESS, REFERER, USER_AGENT, FINGERPRINT2, DUPLICATED_YN, DUP_CHECK_ADMIN_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                applicant.getEventId(), applicant.getApplyEmail(), applicant.getYoutubeNickname(), DateFactory.DATABASE_FORMAT.format(applicant.getApplyTime()), applicant.getIpAddress(), applicant.getReferer(), applicant.getUserAgent(), applicant.getFingerprint2(), applicant.isDuplicated() ? "Y" : "N", applicant.getDupCheckAdminName());
    }

    public void updateApplicantNotDuplicated(int eventId, String applyEmail) {
        jdbcTemplate.update("UPDATE `APPLICANT` " +
                        "SET DUPLICATED_YN = 'N', DUP_CHECK_ADMIN_NAME = NULL " +
                        "WHERE EVENT_ID = ? AND APPLY_EMAIL = ?;",
                eventId, applyEmail);
    }

    public void updateApplicantDuplicated(int eventId, String applyEmail, String dupCheckAdminName) {
        jdbcTemplate.update("UPDATE `APPLICANT` " +
                        "SET DUPLICATED_YN = 'Y', DUP_CHECK_ADMIN_NAME = ? " +
                        "WHERE EVENT_ID = ? AND APPLY_EMAIL = ?;",
                dupCheckAdminName, eventId, applyEmail);
    }
}
