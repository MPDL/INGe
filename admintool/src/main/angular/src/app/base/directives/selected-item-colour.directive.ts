import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
  selector: '[selected-item-color]'
})
export class SelectedItemColourDirective {

  @Input() selectedcolor: string;
  @Input() defaultColor: string;

  constructor(private elemRef: ElementRef) { }

  @HostListener('mouseenter') onMouseEnter() {
    this.highlight(this.selectedcolor || this.defaultColor || 'silver');
  }

  @HostListener('mouseleave') onMouseLeave() {
    this.highlight(null);
  }

  highlight(color: string) {
    this.elemRef.nativeElement.style.backgroundColor = color;
  }

}
