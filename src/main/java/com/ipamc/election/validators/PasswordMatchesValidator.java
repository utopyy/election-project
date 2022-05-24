package com.ipamc.election.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ipamc.election.payload.request.SignupRequest;

public class PasswordMatchesValidator
implements ConstraintValidator<PasswordMatches, Object> {
  
  @Override
  public void initialize(PasswordMatches constraintAnnotation) {
  }
  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context){
      SignupRequest user = (SignupRequest) obj;
      return user.getPassword().equals(user.getMatchingPassword());
  }
}
