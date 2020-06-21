package com.cimb.weekendtask.dao;

import com.cimb.weekendtask.entity.Produk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProdukRepo extends JpaRepository<Produk, Integer> {
    
    @Query(value = "SELECT * FROM Produk", nativeQuery = true)
    public Iterable<Produk> findProduk();
}