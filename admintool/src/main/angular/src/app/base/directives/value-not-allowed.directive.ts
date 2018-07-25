import { Directive, Input, OnChanges, SimpleChanges } from '@angular/core';
import { NG_VALIDATORS, Validator, Validators, ValidatorFn, AbstractControl } from '@angular/forms';

export function valueValidator(regex: RegExp): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} => {
    const notAllowed = regex.test(control.value);
    return notAllowed ? {'value-not-allowed': {value: control.value}} : null;
  };
}

@Directive({
  selector: '[value-not-allowed]',
  providers: [{provide: NG_VALIDATORS, useExisting: ValueNotAllowedDirective, multi: true}]
})
export class ValueNotAllowedDirective implements Validator {

  @Input() valuenotallowed: string;

  constructor() { }

  validate(c: AbstractControl): {[key: string]: any} {
    return this.valuenotallowed ? valueValidator(new RegExp(this.valuenotallowed, 'i'))(c) : null;
  }

}
