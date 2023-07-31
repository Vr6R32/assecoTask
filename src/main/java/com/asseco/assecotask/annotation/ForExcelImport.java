package com.asseco.assecotask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark fields in AppUserDTO class which will be exported into an Excel file.
 * When a data field is marked with this annotation, an Excel data export service can use these fields.
 **/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForExcelImport {
}
