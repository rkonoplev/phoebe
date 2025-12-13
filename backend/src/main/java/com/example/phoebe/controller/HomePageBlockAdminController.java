package com.example.phoebe.controller;

import com.example.phoebe.dto.HomePageBlockDto;
import com.example.phoebe.service.HomePageBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/homepage-blocks")
@RequiredArgsConstructor
public class HomePageBlockAdminController {

    private final HomePageBlockService blockService;

    @GetMapping
    public ResponseEntity<List<HomePageBlockDto>> getAllBlocks() {
        return ResponseEntity.ok(blockService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomePageBlockDto> getBlockById(@PathVariable Integer id) {
        return ResponseEntity.ok(blockService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HomePageBlockDto> createBlock(@RequestBody HomePageBlockDto dto) {
        HomePageBlockDto createdDto = blockService.create(dto);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomePageBlockDto> updateBlock(@PathVariable Integer id, @RequestBody HomePageBlockDto dto) {
        HomePageBlockDto updatedDto = blockService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Integer id) {
        blockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
