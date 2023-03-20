package com.cg.api;


import com.cg.exception.DataInputException;
import com.cg.exception.EmailExistsException;
import com.cg.model.Role;
import com.cg.model.User;
import com.cg.model.dto.RoleDTO;
import com.cg.model.dto.StaffCreateReqDTO;
import com.cg.service.role.IRoleService;
import com.cg.service.staff.IStaffService;
import com.cg.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/staffs")
public class StaffAPI {

    @Autowired
    private IStaffService staffService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody StaffCreateReqDTO staffCreateReqDTO) {

        String username = staffCreateReqDTO.getUsername();

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            throw new EmailExistsException("Email is exists");
        }

        RoleDTO roleDTO = staffCreateReqDTO.getRole();

        Boolean existRole = roleService.existById(roleDTO.getId());

        if (!existRole) {
            throw new DataInputException("Role invalid");
        }

        staffService.create(staffCreateReqDTO);


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
