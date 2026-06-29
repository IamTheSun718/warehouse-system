package com.demo.controller;

import com.demo.model.Material;
import com.demo.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "*")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public List<Material> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        if (keyword != null || category != null) {
            return materialService.search(keyword, category);
        }
        return materialService.findAll();
    }

    @GetMapping("/{id}")
    public Material getById(@PathVariable Long id) {
        return materialService.findById(id);
    }

    @PostMapping
    public Material create(@RequestBody Material material) {
        return materialService.save(material);
    }

    @PutMapping("/{id}")
    public Material update(@PathVariable Long id, @RequestBody Material material) {
        material.setId(id);
        return materialService.save(material);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        materialService.deleteById(id);
    }
}