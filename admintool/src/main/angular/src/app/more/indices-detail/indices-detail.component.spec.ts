import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicesDetailComponent } from './indices-detail.component';

describe('IndicesDetailComponent', () => {
  let component: IndicesDetailComponent;
  let fixture: ComponentFixture<IndicesDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IndicesDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicesDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
