import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextListComponent } from './context-list.component';

describe('ContextListComponent', () => {
  let component: ContextListComponent;
  let fixture: ComponentFixture<ContextListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ContextListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContextListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
