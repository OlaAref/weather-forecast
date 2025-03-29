package com.olaaref.weather.aop.logger.template.interpolation.dto;

import org.aspectj.lang.JoinPoint;

/**
 * Value object that holds both a JoinPoint and a return value.
 * This class is used as the source parameter for the ReturnValueStringSupplierRegistrar.
 */
public record ReturnValueInfo(JoinPoint joinPoint, Object returnValue) {
}
