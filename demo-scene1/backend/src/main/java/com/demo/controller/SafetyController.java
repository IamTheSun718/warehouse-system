package com.demo.controller;

import com.demo.model.Material;
import com.demo.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/safety")
@CrossOrigin(origins = "*")
public class SafetyController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/logs")
    public Map<String, Object> getLogs() {
        List<Material> materials = materialService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("totalMaterials", materials.size());
        result.put("materials", materials);
        result.put("totalStock", materials.stream().mapToInt(Material::getStock).sum());
        Map<String, Long> categoryCount = materials.stream()
            .collect(Collectors.groupingBy(
                Material::getCategory,
                Collectors.counting()
            ));
        result.put("categoryCount", categoryCount);
        return result;
    }
}
