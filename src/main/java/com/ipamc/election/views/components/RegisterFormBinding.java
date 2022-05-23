package com.ipamc.election.views.components;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;


public class RegisterFormBinding {

   private RegisterForm registrationForm;
   private UserService userService;

   /**
    * Flag for disabling first run for password validation
    */
   private boolean enablePasswordValidation;

   public RegisterFormBinding(RegisterForm registrationForm, UserService userService) {
       this.registrationForm = registrationForm;
       this.userService = userService;
   }

   /**
    * Method to add the data binding and validation logics
    * to the registration form
    */
   public void addBindingAndValidation() {
       BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
       binder.bindInstanceFields(registrationForm);

       // A custom validator for password fields
       binder.forField(registrationForm.getPasswordField())
               .withValidator(this::passwordValidator).bind("motDePasse");

       // The second password field is not connected to the Binder, but we
       // want the binder to re-check the password validator when the field
       // value changes. The easiest way is just to do that manually.
       registrationForm.getPasswordConfirmField().addValueChangeListener(e -> {
           // The user has modified the second field, now we can validate and show errors.
           // See passwordValidator() for how this flag is used.
           enablePasswordValidation = true;

           binder.validate();
       });

       // Set the label where bean-level error messages go
       binder.setStatusLabel(registrationForm.getErrorMessageField());

       // And finally the submit button
       registrationForm.getSubmitButton().addClickListener(event -> {
           try {
               // Create empty bean to store the details into
               User userBean = new User();

               // Run validators and write the values to the bean
               binder.writeBean(userBean);

               // Typically, you would here call backend to store the bean
               userService.createUser(userBean);

               // Show success message if everything went well
               showSuccess(userBean);
           } catch (ValidationException exception) {
               // validation errors are already visible for each field,
               // and bean-level errors are shown in the status label.
               // We could show additional messages here if we want, do logging, etc.
           }
       });
   }

   /**
    * Method to validate that:
    * <p>
    * 1) Password is at least 8 characters long
    * <p>
    * 2) Values in both fields match each other
    */
   private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
       /*
        * Just a simple length check. A real version should check for password
        * complexity as well!
        */

       if (pass1 == null || pass1.length() < 8) {
           return ValidationResult.error("Le mot de passe doit faire minimum 8 caractères");
       }

       if (!enablePasswordValidation) {
           // user hasn't visited the field yet, so don't validate just yet, but next time.
           enablePasswordValidation = true;
           return ValidationResult.ok();
       }

       String pass2 = registrationForm.getPasswordConfirmField().getValue();

       if (pass1 != null && pass1.equals(pass2)) {
           return ValidationResult.ok();
       }

       return ValidationResult.error("Les mots de passe sont diférents");
   }

   /**
    * We call this method when form submission has succeeded
    */
   private void showSuccess(User userBean) {
       Notification notification =
               Notification.show("Bienvenue " + userBean.getUsername()+"\nVérifie tes mails pour activer ton compte!");
       notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

       // Here you'd typically redirect the user to another view
   }

}
