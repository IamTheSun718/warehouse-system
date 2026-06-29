package com.demo.controller;

import com.demo.model.Material;
import com.demo.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public List<Map<String, Object>> getInventory(
            @RequestParam(required = false) String category) {
        List<Material> materials = materialService.findAll();
        return materials.stream()
            .filter(m -> category == null || category.isEmpty() || m.getCategory().equals(category))
            .map(m -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", m.getId());
                item.put("code", m.getCode());
                item.put("name", m.getName());
                item.put("cas", m.getCas());
                item.put("category", m.getCategory());
                item.put("stock", m.getStock());
                item.put("unit", m.getUnit());
                item.put("safetyDesc", m.getSafetyDesc());
                return item;
            })
            .collect(Collectors.toList());
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<Material> materials = materialService.findAll();
        Map<String, Integer> categoryStats = materials.stream()
            .collect(Collectors.groupingBy(
                Material::getCategory,
                Collectors.summingInt(Material::getStock)
            ));
        int totalStock = materials.stream().mapToInt(Material::getStock).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("totalStock", totalStock);
        result.put("totalMaterials", materials.size());
        result.put("categoryStats", categoryStats);
        return result;
    }
}