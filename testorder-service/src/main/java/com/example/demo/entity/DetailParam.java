package com.example.demo.entity;

import com.example.demo._enum.Gender;

public record DetailParam(String name, Double min, Double max, String unit, Gender gender) {
}
