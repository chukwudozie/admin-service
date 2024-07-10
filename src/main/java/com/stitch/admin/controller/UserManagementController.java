package com.stitch.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.stitch.admin.utils.Constants.ADMIN_BASE_URL;

@RestController
@RequestMapping(ADMIN_BASE_URL)
@RequiredArgsConstructor

public class UserManagementController {

    //change password
    //update details
    //deactivate user
    //activate user
    //get all active users
    //get all vendor users
    //get all customer users
    //get all orders pending
    // get all orders completed
    //get all transactions completed
}
