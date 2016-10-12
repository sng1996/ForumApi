package ru.mail.park.main.controllers;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Created by farid on 13.10.16.
 */
public class Controller {
    protected Validator validator;

    public Controller() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
}
