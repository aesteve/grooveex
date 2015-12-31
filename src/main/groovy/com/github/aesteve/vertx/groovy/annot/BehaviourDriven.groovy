package com.github.aesteve.vertx.groovy.annot

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Marking annotation used to declared that a test is Behaviour Driven
 *
 * Created by aesteve on 31/12/2015.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@GroovyASTTransformationClass(["com.github.aesteve.vertx.groovy.test.BDDTransformation"])
@interface BehaviourDriven {}