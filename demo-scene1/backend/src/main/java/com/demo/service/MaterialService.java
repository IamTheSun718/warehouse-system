package com.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.mapper.MaterialMapper;
import com.demo.model.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    public List<Material> findAll() {
        return materialMapper.selectList(null);
    }

    public List<Material> search(String keyword, String category) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Material::getName, keyword)
                   .or()
                   .like(Material::getCas, keyword)
                   .or()
                   .like(Material::getCode, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Material::getCategory, category);
        }
        return materialMapper.selectList(wrapper);
    }

    public Material findById(Long id) {
        return materialMapper.selectById(id);
    }

    public Material save(Material material) {
        if (material.getId() == null) {
            materialMapper.insert(material);
        } else {
            materialMapper.updateById(material);
        }
        return material;
    }

    public void deleteById(Long id) {
        materialMapper.deleteById(id);
    }

    public void updateStock(Long id, int delta) {
        Material m = materialMapper.selectById(id);
        if (m != null) {
            m.setStock(Math.max(0, m.getStock() + delta));
            materialMapper.updateById(m);
        }
    }
}
