package com.example.demo.controller;

import com.example.demo.dto.TestOrder.AddTORequest;
import com.example.demo.dto.TestOrder.PageTOResponse;
import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.dto.search.SearchDTO;
import com.example.demo.service.TestOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/testorder")
public class TestOrderController {

    @Autowired
    private TestOrderService testOrderService;

    @GetMapping("/grpc/{id}")
    public ResponseEntity<TOResponse> grpcTesting(
            @PathVariable Integer id
    ){
        TOResponse toResponse = testOrderService.testGrpc(id);

        return ResponseEntity.ok(toResponse);
    }


    @PostMapping("/create")
    public ResponseEntity<TOResponse> createWithoutId(
            @Valid @RequestBody AddTORequest addTORequest
    ) {

        TOResponse toResponse = testOrderService.createTO(addTORequest, Optional.empty());

        return ResponseEntity.ok(toResponse);
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<TOResponse> createWithId(
            @PathVariable Integer id
    ) {

        TOResponse toResponse = testOrderService.createTO(null, Optional.of(id));

        return ResponseEntity.ok(toResponse);
    }

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

    @GetMapping("/{TOId}")
    public ResponseEntity<TOResponse> viewDetailTestOrder(
            @PathVariable Long TOId
    ){
        TOResponse toResponse = testOrderService.viewDetail(TOId);

        return ResponseEntity.ok(toResponse);
    }

    @GetMapping("search")
    public ResponseEntity<PageTOResponse> searchTestOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "runAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        PageTOResponse pageTOResponse = testOrderService.searchTestOrder(page, size, sortBy, direction, keyword);

        return ResponseEntity.ok(pageTOResponse);
    }

    @GetMapping("filter")
    public ResponseEntity<Page<SearchDTO>> filterTestOrder(
            @RequestParam(required = false) String searchText,
//            @RequestParam(required = false) Map<String, Object> filter,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "runAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "1") int offsetPage,
            @RequestParam(defaultValue = "12") int limitOnePage
    ){
//        Page<SearchDTO> pageSearch = testOrderService.getFilterTO(searchText, filter, sortBy, direction, offsetPage, limitOnePage);
//
//        return ResponseEntity.ok(pageSearch);

        try {
            // Build filter map
            Map<String, Object> filter = new HashMap<>();

            if (fromDate != null && !fromDate.isEmpty()) {
                filter.put("fromDate", fromDate);
            }

            if (toDate != null && !toDate.isEmpty()) {
                filter.put("toDate", toDate);
            }

            // Call service method
            Page<SearchDTO> result = testOrderService.getFilterTO(
                    searchText,
                    filter,
                    sortBy,
                    direction,
                    offsetPage,
                    limitOnePage
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Log the error
//            logger.error("Error filtering test orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
