package com.horyu1234.eventangel.controller.admin.rest;

import com.horyu1234.eventangel.domain.Applicant;
import com.horyu1234.eventangel.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by horyu on 2018-04-16
 */
@RequestMapping("/admin/lottery")
@RestController
public class LotteryRestController {
    private LotteryService lotteryService;

    @Autowired
    public void setLotteryService(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @RequestMapping(value = "/lottery", method = RequestMethod.POST)
    public Applicant lottery(int prizeId) {
        Applicant applicant = lotteryService.lottery(prizeId);
        applicant.hidePrivacy();

        return applicant;
    }
}
