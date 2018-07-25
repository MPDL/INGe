import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchTermComponent } from './search-term.component';

describe('SearchTermComponent', () => {
  let component: SearchTermComponent;
  let fixture: ComponentFixture<SearchTermComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchTermComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchTermComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
