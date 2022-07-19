package com.artistack.user.controller;

import com.artistack.base.dto.DataResponseDto;
import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    // http://localhost:8080/user
    @GetMapping(path = "")
    public DataResponseDto<Object> get() {
        return DataResponseDto.of(List.of(1, 2, 3));
        /*
          {
            "success": true,
            "code": 0,
            "message": "Ok",
            "data": [
              1,
              2,
              3
            ]
          }
         */
    }


    // http://localhost:8080/user/error/custom
    @GetMapping(path = "/error/custom")
    public DataResponseDto<Object> errorWithCustomException() {
        Boolean isValid = false;

        if (!isValid) {
            // exception occurs
            throw new GeneralException(Code.VALIDATION_ERROR, "Reason why it isn't valid");
            /*
              {
                "success": false,
                "code": 10001,
                "message": "Validation error - Reason why it isn't valid"
              }
            */
        }

        return DataResponseDto.empty();
    }


    // http://localhost:8080/user/error/exception
    @GetMapping(path = "/error/exception")
    public DataResponseDto<Object> errorWithException() {
        try {
            List<Integer> list = List.of(1, 2, 3, 4, 5);

            log.debug(list.get(99999).toString()); // outofbound exception occurs

        } catch (Exception e) {
            log.trace("Exception", e);
            throw e;
                /*
                  {
                    "success": false,
                    "code": 20000,
                    "message": "Internal error - Index 9 out of bounds for length 5"
                  }
                */
        }

        return DataResponseDto.empty();
    }
}