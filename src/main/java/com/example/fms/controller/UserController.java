package com.example.fms.controller;

import com.example.fms.dto.UserDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseMessage createUser(@ApiParam(value = "a link is sent to the user's email and by this link the user can activate his account") @RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @PutMapping("/position/{position}")
    public ResponseEntity<User> setPosition(@PathVariable String position, Principal principal){
        return userService.setPosition(position, principal.getName());
    }

    @PutMapping("/image")
    public ResponseEntity<User> setImage(@RequestParam(name = "file") MultipartFile multipartFile, //больше одного RequestParam нельзя, когда MultipartFile
                                         Principal principal) throws IOException {
        return userService.setImage(multipartFile, principal.getName());
    }

    @PostMapping("/destroyImage")
    public ResponseMessage deleteImage(Principal principal){
        return userService.deleteImage(principal.getName());
    }

    @PutMapping("/block/{id}")
    public ResponseMessage blockUserById(@PathVariable Long id, Principal principal){
        return userService.blockUserById(id, principal.getName());
    }

    @PutMapping("/unBlock/{id}")
    public ResponseEntity<User> unBlockUserById(@PathVariable Long id, Principal principal){
        return userService.unBlockUserById(id, principal.getName());
    }

    @PutMapping("/changePassword/{newPassword}")
    public ResponseEntity<User> changePassword(@PathVariable String newPassword, Principal principal){
        return userService.changePassword(principal.getName(), newPassword);
    }

    @PutMapping("/departmentList")
    public ResponseEntity<User> setDepartmentLIst(@RequestParam String userEmail,
                                                  @RequestParam List<Long> departmentIdList, Principal principal){
        return userService.setDepartmentList(departmentIdList, userEmail, principal.getName());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email){ //email нужно написать точно и правильно
        return userService.getByEmail(email);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        return userService.getById(id);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page."),
    })

    @GetMapping("/profile")
    public ResponseEntity<User> getUserForProfile(Principal principal){
        return userService.getByEmail(principal.getName());
    }

    @GetMapping("/get")
    public Page<User> getAllByParam(Pageable pageable,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) Boolean isActive,
                                    @RequestParam(required = false) String surname,
                                    @RequestParam(required = false) List<Long> departmentIdList,
                                    @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                    @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore,
                                    @RequestParam(required = false) String position){

        Set<User> fooSet = new LinkedHashSet<>(userService.getAll());

        if (name != null)
            fooSet.retainAll(userService.getAllByName(name));
        if (isActive != null)
            fooSet.retainAll(userService.getAllByActive(isActive));
        if (surname != null)
            fooSet.retainAll(userService.getAllBySurname(surname));
        if (dateAfter != null)
            fooSet.retainAll(userService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(userService.getAllByDateCreatedBefore(dateBefore));
        if (position != null)
            fooSet.retainAll(userService.getAllByPosition(position));
        if (departmentIdList != null)
            fooSet.retainAll(userService.getAllByDepartments(departmentIdList));

        List<User> userList = new ArrayList<>(fooSet);
        return userService.getByPage(userList, pageable);
    }


}
