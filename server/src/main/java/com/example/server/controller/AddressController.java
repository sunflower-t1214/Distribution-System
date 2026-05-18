package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.common.UserContext;
import com.example.server.entity.Address;
import com.example.server.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> list() {
        Integer userId = UserContext.getUserId();
        List<Address> list = addressService.lambdaQuery()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime)
                .list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Address> getById(@PathVariable Long id) {
        Address addr = addressService.getById(id);
        return addr != null ? Result.success(addr) : Result.fail("地址不存在");
    }

    @PostMapping("/save")
    public Result<Address> save(@RequestBody Address address) {
        Integer userId = UserContext.getUserId();
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        if (address.getIsDefault() == 1) {
            addressService.lambdaUpdate()
                    .eq(Address::getUserId, userId)
                    .eq(Address::getIsDefault, 1)
                    .set(Address::getIsDefault, 0)
                    .update();
        }
        addressService.save(address);
        return Result.success(address);
    }

    @PutMapping("/update")
    public Result<Address> update(@RequestBody Address address) {
        address.setUpdateTime(LocalDateTime.now());
        if (address.getIsDefault() == 1) {
            addressService.lambdaUpdate()
                    .eq(Address::getUserId, UserContext.getUserId())
                    .eq(Address::getIsDefault, 1)
                    .set(Address::getIsDefault, 0)
                    .update();
        }
        addressService.updateById(address);
        return Result.success(address);
    }

    @PutMapping("/set-default")
    public Result<String> setDefault(@RequestParam Long id) {
        Integer userId = UserContext.getUserId();
        addressService.lambdaUpdate()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0)
                .update();
        addressService.lambdaUpdate()
                .eq(Address::getId, id)
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 1)
                .update();
        return Result.success("默认地址已更新");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        addressService.removeById(id);
        return Result.success("地址已删除");
    }
}
