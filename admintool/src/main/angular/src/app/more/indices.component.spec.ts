import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicesComponent } from './indices.component';

describe('IndicesComponent', () => {
  let component: IndicesComponent;
  let fixture: ComponentFixture<IndicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IndicesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
