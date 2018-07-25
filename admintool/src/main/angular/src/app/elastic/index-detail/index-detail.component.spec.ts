import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IndexDetailComponent } from './index-detail.component';

describe('IndexDetailComponent', () => {
  let component: IndexDetailComponent;
  let fixture: ComponentFixture<IndexDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IndexDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
