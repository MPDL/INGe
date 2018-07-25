import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextSearchComponent } from './context-search.component';

describe('ContextSearchComponent', () => {
  let component: ContextSearchComponent;
  let fixture: ComponentFixture<ContextSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ContextSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContextSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
