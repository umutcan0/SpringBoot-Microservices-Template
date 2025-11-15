/*
package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TitleValidator implements ConstraintValidator<ValidTitle, String> {

    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        // null veya boşsa
        if (title == null || title.trim().isEmpty()) {
            setMessage(context, "Başlık boş olamaz");
            return false;
        }

        // uzunluk kontrolü
        if (title.trim().length() < 3) {
            setMessage(context, "Başlık en az 3 karakter olmalı");
            return false;
        }

        // rakam içerme kontrolü
        if (title.matches(".*\\d.*")) {
            setMessage(context, "Başlık rakam içeremez");
            return false;
        }

        // tüm kontroller geçti
        return true;
    }

    // yardımcı metod: default mesajı iptal edip özel mesaj koyar
    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
*/
