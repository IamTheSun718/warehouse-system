package com.demo.service;

import com.demo.mapper.MaterialMapper;
import com.demo.model.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialMapper materialMapper;

    @InjectMocks
    private MaterialService materialService;

    private Material testMaterial;

    @BeforeEach
    void setUp() {
        testMaterial = new Material();
        testMaterial.setId(1L);
        testMaterial.setCode("M-001");
        testMaterial.setName("甲醇");
        testMaterial.setCas("67-56-1");
        testMaterial.setCategory("易燃");
        testMaterial.setUnit("桶");
        testMaterial.setStock(100);
        testMaterial.setSafetyDesc("阴凉通风，远离火源");
    }

    @Test
    void testFindAll_ShouldReturnMaterialList() {
        List<Material> mockList = Arrays.asList(testMaterial);
        when(materialMapper.selectList(null)).thenReturn(mockList);

        List<Material> result = materialService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("甲醇", result.get(0).getName());
        verify(materialMapper, times(1)).selectList(null);
    }

    @Test
    void testFindById_ShouldReturnMaterial_WhenIdExists() {
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);

        Material result = materialService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("甲醇", result.getName());
    }

    @Test
    void testFindById_ShouldReturnNull_WhenIdNotExists() {
        when(materialMapper.selectById(99L)).thenReturn(null);

        Material result = materialService.findById(99L);

        assertNull(result);
    }

    @Test
    void testSave_ShouldInsertMaterial_WhenIdIsNull() {
        Material newMaterial = new Material();
        newMaterial.setCode("M-006");
        newMaterial.setName("乙醇");
        newMaterial.setCas("64-17-5");
        newMaterial.setCategory("易燃");
        newMaterial.setUnit("桶");
        newMaterial.setStock(50);

        when(materialMapper.insert(newMaterial)).thenReturn(1);

        Material result = materialService.save(newMaterial);

        assertNotNull(result);
        verify(materialMapper, times(1)).insert(newMaterial);
        verify(materialMapper, never()).updateById(any());
    }

    @Test
    void testSave_ShouldUpdateMaterial_WhenIdExists() {
        testMaterial.setName("无水甲醇");
        when(materialMapper.updateById(testMaterial)).thenReturn(1);

        Material result = materialService.save(testMaterial);

        assertNotNull(result);
        assertEquals("无水甲醇", result.getName());
        verify(materialMapper, times(1)).updateById(testMaterial);
    }

    @Test
    void testDeleteById_ShouldDeleteMaterial_WhenIdExists() {
        when(materialMapper.deleteById(1L)).thenReturn(1);

        materialService.deleteById(1L);

        verify(materialMapper, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateStock_ShouldIncreaseStock_WhenPositiveDelta() {
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(materialMapper.updateById(testMaterial)).thenReturn(1);

        materialService.updateStock(1L, 50);

        assertEquals(150, testMaterial.getStock());
    }

    @Test
    void testUpdateStock_ShouldDecreaseStock_WhenNegativeDelta() {
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(materialMapper.updateById(testMaterial)).thenReturn(1);

        materialService.updateStock(1L, -20);

        assertEquals(80, testMaterial.getStock());
    }

    @Test
    void testUpdateStock_ShouldNotGoBelowZero_WhenStockInsufficient() {
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(materialMapper.updateById(testMaterial)).thenReturn(1);

        materialService.updateStock(1L, -150);

        assertEquals(0, testMaterial.getStock());
    }
}