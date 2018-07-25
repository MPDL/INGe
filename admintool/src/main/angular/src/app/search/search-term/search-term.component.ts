import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup } from '@angular/forms';

export const queryTypes = ['must', 'must_not', 'filter', 'should'];

@Component({
  selector: 'search-term',
  templateUrl: './search-term.component.html',
  styleUrls: ['./search-term.component.scss']
})
export class SearchTermComponent {

  filteredTerms: string[] = [];
  types: string[] = queryTypes;

  @Input() searchTermForm: FormGroup;
  @Input() fields: string[];
  @Output() notice = new EventEmitter<string>();

  filter() {
    const selectedField = this.searchTermForm.get('field') as FormGroup;
    if (selectedField.value !== '') {
      this.filteredTerms = this.fields.filter((el) => {
        return el.toLowerCase().indexOf(selectedField.value.toLowerCase()) > -1;
      });
    } else {
      this.filteredTerms = [];
    }
  }

  select(term) {
    this.searchTermForm.patchValue({field: term});
    this.filteredTerms = [];
  }

  onQueryTypeSelect(type) {
    this.searchTermForm.patchValue({type: type});
  }

  close() {
    this.searchTermForm.patchValue({field: ''});
    this.filteredTerms = [];
  }

  addSearchTerm() {
    this.notice.emit('add');
  }

  removeSearchTerm() {
    this.notice.emit('remove');
  }
  /*
  searchItems(f,t) {
    console.log('sending notification from child: ' + t);
    this.notice.emit(f+':'+t);
  }
  */
}
