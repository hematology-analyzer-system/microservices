package com.example.demo.controller;

import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.service.TestOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testorder")
public class TestOrderController {

    @Autowired
    private TestOrderService testOrderService;

    @PutMapping("/{TOId}")
    public ResponseEntity<TOResponse> modifiedTestOrder(
            @PathVariable("TOId") Long TOId,
            @Valid @RequestBody UpdateTORequest updateTO)
    {
        TOResponse toResponse = testOrderService.modifyTO(TOId, updateTO);

        return ResponseEntity.ok(toResponse);
    }

    @DeleteMapping("/{TOId}")
    public String deleteTestOrder(
            @PathVariable("TOId") Long TOId) {
        testOrderService.deteleTestOrder(TOId);

        return "Deleted successfully";
    }
}
