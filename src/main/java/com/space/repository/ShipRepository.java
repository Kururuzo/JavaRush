package com.space.repository;

import com.space.model.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ShipRepository extends
        JpaRepository <Ship, Long> {
//        JpaRepository <Ship, Long>, JpaSpecificationExecutor<Ship> {
//        CrudRepository<Ship, Long> {
//        JpaRepository<Ship, Long>, QuerydslPredicateExecutor<Ship> {
}
