import { AbstractControl, ValidatorFn } from '@angular/forms';

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const password = control.value;

    // Regular expressions to check for at least one lowercase, one uppercase,
    // one special character, and one number
    const lowerCaseRegex = /[a-z]/;
    const upperCaseRegex = /[A-Z]/;
    const specialCharacterRegex = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;
    const numberRegex = /[0-9]/;

    // Check if the password meets all criteria
    const hasLowerCase = lowerCaseRegex.test(password);
    const hasUpperCase = upperCaseRegex.test(password);
    const hasSpecialCharacter = specialCharacterRegex.test(password);
    const hasNumber = numberRegex.test(password);
    const sufficientLength = password.length >= 8;
    // If all criteria are met, return null (no error)
    // Otherwise, return an object with the error key
    return hasLowerCase &&
      hasUpperCase &&
      hasSpecialCharacter &&
      hasNumber &&
      sufficientLength
      ? null
      : {
          hasLowerCase: !hasLowerCase,
          hasUpperCase: !hasUpperCase,
          hasSpecialCharacter: !hasSpecialCharacter,
          hasNumber: !hasNumber,
          sufficientLength: !sufficientLength,
        };
  };
}
